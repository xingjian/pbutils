/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.gpsmatch.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.utils.fileutils.PBFileUtil;
import com.promisepb.utils.gisdata.CarGPS;
import com.promisepb.utils.gisutils.PBGISCoorTransformUtil;
import com.promisepb.utils.gpsmatch.service.DIDIGPSService;
import com.promisepb.utils.mathutils.PBMathUtil;
import com.promisepb.utils.stringutils.PBStringUtil;

/**  
 * 功能描述:DIDIGPSService接口实现类，处理滴滴gps数据
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年11月24日 上午11:03:14  
 */
public class DIDIGPSServiceImpl implements DIDIGPSService {

	public static Logger logger = LoggerFactory.getLogger(DIDIGPSServiceImpl.class);
	private String gpsFilePath;
	private String fileCharSet;
	private String exportFilePath;
	private int orderSplitTime;
	private int orderMinCount;
	private double maxSpeed;
	private String filePrefix;
	
	@Override
	public void handleGPSFile(){
		try {
			File file = new File(gpsFilePath);
			String year = file.getName()+"_";
			File[] fileMonthArr = file.listFiles();
			for(File fileMonth : fileMonthArr) {
				File[] fileDayArr = fileMonth.listFiles();
				String month = fileMonth.getName()+"_";
				for(File fileDay : fileDayArr) {
					String day = fileDay.getName();
					List<File> gpsDayFiles = new ArrayList<File>();
					Map<String,List<CarGPS>> mapTemp = new HashMap<String,List<CarGPS>>();
					PBFileUtil.GetFilesByPath(fileDay.getAbsolutePath(), gpsDayFiles);
					for(File gpsFileTemp : gpsDayFiles) {
						List<String> gpsList = PBFileUtil.ReadFileByLine(gpsFileTemp.getAbsolutePath(), fileCharSet);
						for(String strTemp : gpsList) {
							String[] strArrTemp = strTemp.split(",");
							if(strArrTemp.length==5) {
								double speedTemp = Double.parseDouble(strArrTemp[4]);
								if(speedTemp<=maxSpeed) {
									CarGPS carGPSTemp = new CarGPS();
									String idTemp = strArrTemp[0];
									Long timeLong = Long.valueOf(strArrTemp[1]);
									double xDouble = Double.parseDouble(strArrTemp[2]);
									double yDouble = Double.parseDouble(strArrTemp[3]);
									double[] conver84XY = PBGISCoorTransformUtil.From02To84(yDouble,xDouble);
									carGPSTemp.setCarCode(idTemp);
									carGPSTemp.setGpsTime(converDate(timeLong));
									carGPSTemp.setLongitude(conver84XY[1]);
									carGPSTemp.setLatitude(conver84XY[0]);
									carGPSTemp.setSpeed(speedTemp);
									if(null==mapTemp.get(carGPSTemp.getCarCode())) {
										List<CarGPS> mapListTemp = new ArrayList<CarGPS>();
										mapListTemp.add(carGPSTemp);
										mapTemp.put(carGPSTemp.getCarCode(), mapListTemp);
									}else {
										mapTemp.get(carGPSTemp.getCarCode()).add(carGPSTemp);
									}
								}
							}
						}
					}
					//处理分类结果
					BufferedWriter resultTempWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(exportFilePath+File.separator+filePrefix+year+month+day+"_da.csv")), fileCharSet), 10240);
					Set<String> set = mapTemp.keySet();
					Iterator<String> iterator = set.iterator();
					String head = ",ID,BeijingTime,wgs84lng,wgs84lat,speed,directionAngle";
				    resultTempWtriter.write(head);
					resultTempWtriter.newLine();
					int index=0;
					while (iterator.hasNext()) {
					    String str = iterator.next();
					    List<CarGPS> gpsList = mapTemp.get(str);
					    removeDuplicate(gpsList);
					    Collections.sort(gpsList);
					    if(gpsList.size()>=orderMinCount) {
					    	removeOrderSplitTime(gpsList);
					    	calcAngle(gpsList);
					    }
					    //'ID', 'BeijingTime', 'wgs84lng', 'wgs84lat', 'speed','directionAngle'
					    for(CarGPS carGPSTemp : gpsList) {
					    	if(carGPSTemp.getAngle()>0) {
					    		String wstr =  index+","+carGPSTemp.getCarCode()+","+carGPSTemp.getGpsTime()+","+PBStringUtil.FormatDoubleStr("#.000000", carGPSTemp.getLongitude())+","+
						    			PBStringUtil.FormatDoubleStr("#.000000", carGPSTemp.getLatitude())+","+carGPSTemp.getSpeed()+","+PBStringUtil.FormatDoubleStr("#.0", carGPSTemp.getAngle());
								resultTempWtriter.write(wstr);
								resultTempWtriter.newLine();
								index++;
					    	}
					    }
					}
					resultTempWtriter.flush();
					resultTempWtriter.close();
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void calcAngle(List<CarGPS> list) {
		double angleTemp = -1.0;
		for(int i=0;i<list.size()-1;i++) {
			CarGPS c1 = list.get(i);
			CarGPS c2 = list.get(i+1);
			if(angleTemp<0) {
				c1.setAngle(-1.0);
				if(c2.getSpeed()>0.0000) {
					double angleResult = PBMathUtil.getVectorAngle(c1.getLongitude(), c1.getLatitude(),c2.getLongitude(), c2.getLatitude());
					angleTemp = angleResult;
					c2.setAngle(angleTemp);
				}else {
					c2.setAngle(-1.0);
				}
			}else {
				if(c2.getLatitude()==c1.getLatitude()&&c2.getLongitude()==c1.getLongitude()) {
					
				}else {
					double angleResult = PBMathUtil.getVectorAngle(c1.getLongitude(), c1.getLatitude(),c2.getLongitude(), c2.getLatitude());
					angleTemp = angleResult;
				}
				c2.setAngle(angleTemp);	
			}
			
		}
	}
	
	public void removeOrderSplitTime(List<CarGPS> list) throws Exception{
		for(int i=0;i<list.size()-1;i++) {
			long time1 = PBStringUtil.sdf_yMdHms.parse(list.get(i).getGpsTime()).getTime();
			long time2 =PBStringUtil.sdf_yMdHms.parse(list.get(i+1).getGpsTime()).getTime();
			if(time2 - time1>orderSplitTime*1000) {
				list.remove(i+1);
				//删除了元素，迭代的下标也跟着改变
			}
		}
	}
	
	 public  void  removeDuplicate(List<CarGPS> list)  {
		 for  ( int  i = 0 ; i < list.size()-1 ;i ++ )  {
			 for  ( int  j = list.size()-1; j>i; j--)  {
				 if  ((list.get(j).getCarCode()+list.get(j).getGpsTime()).equals(list.get(i).getCarCode()+list.get(i).getGpsTime()))  {
					 list.remove(j);
				 } 
		     } 
		 } 
	}
	
	public String getFilePrefix() {
		return filePrefix;
	}

	public void setFilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
	}

	public String converDate(long time) {
		return PBStringUtil.sdf_yMdHms.format(new Date(time*1000));
	}
	
	public String getGpsFilePath() {
		return gpsFilePath;
	}

	public void setGpsFilePath(String gpsFilePath) {
		this.gpsFilePath = gpsFilePath;
	}

	public String getFileCharSet() {
		return fileCharSet;
	}

	public void setFileCharSet(String fileCharSet) {
		this.fileCharSet = fileCharSet;
	}

	public String getExportFilePath() {
		return exportFilePath;
	}

	public void setExportFilePath(String exportFilePath) {
		this.exportFilePath = exportFilePath;
	}

	public int getOrderSplitTime() {
		return orderSplitTime;
	}

	public void setOrderSplitTime(int orderSplitTime) {
		this.orderSplitTime = orderSplitTime;
	}

	public int getOrderMinCount() {
		return orderMinCount;
	}

	public void setOrderMinCount(int orderMinCount) {
		this.orderMinCount = orderMinCount;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	
}
