/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.gpsmatch.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.utils.fileutils.PBFileUtil;
import com.promisepb.utils.gisdata.CarGPS;
import com.promisepb.utils.gisdata.LinkPoint;
import com.promisepb.utils.gisutils.PBGTGeometryUtil;
import com.promisepb.utils.gisutils.PBGeoShapeUtil;
import com.promisepb.utils.gpsmatch.service.GPSMatchSWLinkService;
import com.promisepb.utils.gpsmatch.vo.CarGPSMatchSWLinkResult;
import com.promisepb.utils.gpsmatch.vo.LindNodeToployData;
import com.promisepb.utils.mathutils.PBMathUtil;
import com.promisepb.utils.stringutils.PBStringUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年11月11日 上午11:40:28  
 */
public class GPSMatchSWLinkServiceImpl implements GPSMatchSWLinkService {

	public static Logger logger = LoggerFactory.getLogger(GPSMatchSWLinkServiceImpl.class);
	public double matchAngle;
	public String gpsFilePath;
	public String workspacePath;
	public String topyFileName;
	public String shapeFileName;
	public String shapeFileCharSet;
	public double distance;
	public int gridSize;
	public int orderSplitTime;
	public Map<String,List<LindNodeToployData>> mapGridTopy = new HashMap<String,List<LindNodeToployData>>();
	public Map<String,LindNodeToployData> linkIDTopyMap = new HashMap<String,LindNodeToployData>();
	public double[] bounds;
	@Override
	public void executeMatch() {
		try {
			logger.info("start gpsmath ......");
			logger.info("shapeFileName : "+shapeFileName+"("+shapeFileCharSet+")");
			logger.info("workspacePath : "+workspacePath);
			logger.info("topyFileName : "+topyFileName);
			logger.info("gpsFilePath : "+gpsFilePath);
			logger.info("matchAngle : "+matchAngle);
			logger.info("buffer distance : "+distance);
			bounds = getLinksBounds();
			loadTopyFile();
			List<File> gpsFiles = new ArrayList<File>();
			PBFileUtil.GetFilesByPath(gpsFilePath, gpsFiles);
			for(File gpsFileTemp : gpsFiles) {
				logger.info("prepare handler gps file "+gpsFileTemp.getName());
				BufferedWriter resultTempWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(workspacePath+File.separator+gpsFileTemp.getName().substring(0, gpsFileTemp.getName().indexOf(".csv"))+"_match_success.csv")), shapeFileCharSet), 10240);
				Map<String,List<CarGPS>> carGPSMap = loadGPSFile(gpsFileTemp.getAbsolutePath());
				for (Map.Entry<String,List<CarGPS>> entry : carGPSMap.entrySet()) {
					String carCode = entry.getKey();
					LindNodeToployData preLNTD = null;
					List<CarGPS> listCarGPS = entry.getValue();
					List<CarGPSMatchSWLinkResult> carMatchResult = new ArrayList<CarGPSMatchSWLinkResult>();
					int carMatchResultIndex = 0;
					for(CarGPS carGPSTemp : listCarGPS) {
						LindNodeToployData rlPatch = null;
						Map<String,LindNodeToployData> mapResult =  getLindNodeToployData(carGPSTemp);
						double distanceTemp=0;
						loop1:for (Map.Entry<String,LindNodeToployData> entryTemp : mapResult.entrySet()) {
							//方向 0:未调查(默认为双向)  1:双向  2:顺方向(单向通行，通行方向为起点到终点方向) 3:逆方向(单向通行，通行方向为终点到起点方向)
							double angleTemp = 180;
							LindNodeToployData rlTemp =  entryTemp.getValue();
							if(rlTemp.getDirection().equals("2")||rlTemp.getDirection().equals("3")) {
		    					angleTemp = Math.abs(rlTemp.getAngle() - carGPSTemp.getAngle());
		    				}else if(rlTemp.getDirection().equals("0")||rlTemp.getDirection().equals("1")) {
		    					angleTemp = Math.abs(rlTemp.getAngle() - carGPSTemp.getAngle());
		    					double angleTemp2 = Math.abs(rlTemp.getAngle()+180 - carGPSTemp.getAngle());
		    					if(angleTemp>angleTemp2) {
		    						angleTemp = angleTemp2;
		    					}
		    				}
							if(angleTemp > matchAngle&&(360-angleTemp)>matchAngle) {
		    					continue loop1;	
		    				}else {
		    					Point carPoint = PBGTGeometryUtil.createPoint(carGPSTemp.getLongitude(),carGPSTemp.getLatitude());
		            			if(distanceTemp==0) {
		                			distanceTemp = PBGTGeometryUtil.distanceGeo(carPoint, rlTemp.getmLine());
		                			rlPatch = rlTemp;
		                		}else {
		                			double otherDistance = PBGTGeometryUtil.distanceGeo(carPoint, rlTemp.getmLine());
		                			if(otherDistance<distanceTemp) {
		                				distanceTemp = otherDistance;
		                				rlPatch = rlTemp;
		                			}
		                		}
		            		}
						}  
						if(null!=rlPatch) {
							preLNTD = rlPatch;
		            		LineString lsTemp = (LineString)rlPatch.getmLine().getGeometryN(0);
		            		Coordinate coorTemp = new Coordinate(carGPSTemp.getLongitude(),carGPSTemp.getLatitude());
		            		int[] indexes = PBGTGeometryUtil.findClosestSegmentAndCoordinate(lsTemp, coorTemp);
		            		CoordinateSequence coordinates = lsTemp.getCoordinateSequence();
		            		Coordinate cuizu = null;
		            		if(indexes[0]!=-1) {
		            			if(indexes[2]==1) {//正好是节点
		            				cuizu = coordinates.getCoordinate(indexes[0]);
		            			}else {
		            				Coordinate c1 = coordinates.getCoordinate(indexes[0]);
		            				Coordinate c2 = coordinates.getCoordinate(indexes[0]+1);
		            				double[] resultTemp = PBMathUtil.GetBetweenLinePoints(carGPSTemp.getLongitude(),carGPSTemp.getLatitude(), c1.x, c1.y, c2.x, c2.y);
		            				if(null!=resultTemp) {
		            					cuizu = new Coordinate(resultTemp[0],resultTemp[1]);
		            				}
		            			}
		            			if(null!=cuizu) {
		            				CarGPSMatchSWLinkResult cgpsmsllr = new CarGPSMatchSWLinkResult();
		            				cgpsmsllr.setCarCode(carGPSTemp.getCarCode());
		            				cgpsmsllr.setNewGPS(cuizu);
		            				cgpsmsllr.setOldGPS(carGPSTemp);
		            				cgpsmsllr.setSwLink(rlPatch);
		            				cgpsmsllr.setStatus("1");
		            				carMatchResult.add(carMatchResultIndex,cgpsmsllr);
		            				carMatchResultIndex++;
		            			}
		            		}else {
		            			System.out.println("findClosestSegmentAndCoordinate is error!");
		            		}
		            	}
					}
					exportMathResult(carMatchResult,resultTempWtriter);
				}
				resultTempWtriter.flush();
				resultTempWtriter.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void exportMathResult(List<CarGPSMatchSWLinkResult> carMatchResult,BufferedWriter resultTempWtriter) throws Exception {
		checkNodeConnect(carMatchResult);
		Coordinate current = null;
		CarGPSMatchSWLinkResult currentCarGPSMatch = null;
		int orderIndex = 1;
		for(int i=0;i<carMatchResult.size();i++) {
			CarGPSMatchSWLinkResult resultTemp = carMatchResult.get(i);
			double distanceS = 0.0;
			double distanceE = 0.0;
			if(resultTemp.getSwLink().getDirection().equals("3")) {
				distanceE = PBGTGeometryUtil.GetDistance84(resultTemp.getNewGPS().x,resultTemp.getNewGPS().y,resultTemp.getSwLink().getFirstPoint().getX(),resultTemp.getSwLink().getFirstPoint().getY());
				distanceS  = resultTemp.getSwLink().getLength() - distanceS;
			}else {
				distanceS = PBGTGeometryUtil.GetDistance84(resultTemp.getNewGPS().x,resultTemp.getNewGPS().y,resultTemp.getSwLink().getFirstPoint().getX(),resultTemp.getSwLink().getFirstPoint().getY());
				distanceE = resultTemp.getSwLink().getLength() - distanceS;
			}
			double distanceCZAndGPS = PBGTGeometryUtil.GetDistance84(resultTemp.getNewGPS().x,resultTemp.getNewGPS().y,resultTemp.getOldGPS().getLongitude(),resultTemp.getOldGPS().getLatitude());
			if(distanceCZAndGPS>distance) {
				//表示垂足的点距离投影的点的距离大于distance
				resultTemp.setStatus("2");
				continue;
			}
			double distanceOffLast = 0.0;
			int timeOffLast = 0;
			if(null==current) {
				current = resultTemp.getNewGPS();
				currentCarGPSMatch = resultTemp;
			}else {
				if(!resultTemp.getStatus().trim().equals("0")&&!currentCarGPSMatch.getStatus().equals("0")) {
					distanceOffLast = PBGTGeometryUtil.GetDistance84(resultTemp.getNewGPS().x,resultTemp.getNewGPS().y,current.x,current.y);
					long time1 = PBStringUtil.sdf_yMdHms.parse(resultTemp.getOldGPS().getGpsTime()).getTime();
					long time2 =PBStringUtil.sdf_yMdHms.parse(currentCarGPSMatch.getOldGPS().getGpsTime()).getTime();
					timeOffLast = (int)((time1-time2)/1000);
					if(timeOffLast>orderSplitTime) {
						orderIndex++;
					}
				}
				current = resultTemp.getNewGPS();
				currentCarGPSMatch = resultTemp;
			}
			String wstr =  resultTemp.getCarCode()+","+resultTemp.getOldGPS().getGpsTime()+","+resultTemp.getOldGPS().getLongitude()+","+resultTemp.getOldGPS().getLatitude()+","+resultTemp.getOldGPS().getSpeed()+","+resultTemp.getOldGPS().getAngle()
					+","+resultTemp.getNewGPS().x+","+resultTemp.getNewGPS().y+","+resultTemp.getSwLink().getLinkid()+","+distanceS+","+distanceE+","+resultTemp.getStatus()+","+distanceOffLast+","+timeOffLast+","+resultTemp.getCarCode()+"_"+orderIndex;
			resultTempWtriter.write(wstr);
			resultTempWtriter.newLine();
		}
	}
	
	
	/**
	 * 
	 * @return
	 */
	public void checkNodeConnect(List<CarGPSMatchSWLinkResult> carMatchResult) {
		if(carMatchResult.size()>=3) {
			for(int i=0;i<carMatchResult.size()-2;i++) {
				CarGPSMatchSWLinkResult c1 = carMatchResult.get(i);
				CarGPSMatchSWLinkResult c2 = carMatchResult.get(i+1);
				CarGPSMatchSWLinkResult c3 = carMatchResult.get(i+2);
				
				if(c1.getSwLink().getLinkid().equals(c3.getSwLink().getLinkid())) {
					if(!c2.getSwLink().getLinkid().equals(c1.getSwLink().getLinkid())) {
						c2.setStatus("0");
					}
				}else {
					if(PBGTGeometryUtil.distanceGeo(c1.getSwLink().getmLine(),c3.getSwLink().getmLine())<0.0000006) {
						if(!c2.getSwLink().getLinkid().equals(c1.getSwLink().getLinkid())&&!c2.getSwLink().getLinkid().equals(c3.getSwLink().getLinkid())) {
							c2.setStatus("0");
						}
					}
				}
			}
		}
	}
	
	/**
	 * 加载topy文件
	 */
	public void loadTopyFile() {
		List<String> topyList = PBFileUtil.ReadCSVFile(workspacePath+File.separator+topyFileName+".csv", "UTF-8");
		logger.info("load topy file "+topyFileName+".csv success!");
		for(String str : topyList) {
			String[] strArrTemp = str.split("#");
			String linkID = strArrTemp[0];
			String direction = strArrTemp[1];
			double length = Double.parseDouble(strArrTemp[2])*1000;
			String[] firstPointStrArr = strArrTemp[3].split(":");
			LinkPoint firstPoint = new LinkPoint();
			firstPoint.setX(Double.parseDouble(firstPointStrArr[0]));
			firstPoint.setY(Double.parseDouble(firstPointStrArr[1]));
			String[] lastPointStrArr = strArrTemp[4].split(":");
			LinkPoint lastPoint = new LinkPoint();
			lastPoint.setX(Double.parseDouble(lastPointStrArr[0]));
			lastPoint.setY(Double.parseDouble(lastPointStrArr[1]));
			String snodeStr = strArrTemp[6];
			String enodeStr = strArrTemp[7];
			double angle = Double.parseDouble(strArrTemp[8]);
			String wktTemp = strArrTemp[9];
			LindNodeToployData lntdTemp = new LindNodeToployData();
			lntdTemp.setDirection(direction);
			lntdTemp.setEnodeID(enodeStr);
			lntdTemp.setLength(length);
			lntdTemp.setLinkid(linkID);
			lntdTemp.setSnodeID(snodeStr);
			lntdTemp.setAngle(angle);
			lntdTemp.setFirstPoint(firstPoint);
			lntdTemp.setLastPoint(lastPoint);
			try {
				lntdTemp.setmLine(PBGTGeometryUtil.createMLineByWKT(wktTemp));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			linkIDTopyMap.put(lntdTemp.getLinkid(), lntdTemp);
			String[] gridArrTemp = strArrTemp[5].split(":");
			for(String gridIndx : gridArrTemp) {
				if(null==mapGridTopy.get(gridIndx)) {
					List<LindNodeToployData> listMapTemp = new ArrayList<LindNodeToployData>();
					listMapTemp.add(lntdTemp);
					mapGridTopy.put(gridIndx, listMapTemp);
				}else {
					mapGridTopy.get(gridIndx).add(lntdTemp);
				}
			}
		}
	}
	
	
	public Map<String,List<CarGPS>> loadGPSFile(String path){
		List<String> gpsList = PBFileUtil.ReadCSVFile(path, "UTF-8");
		Map<String,List<CarGPS>> result = new HashMap<String,List<CarGPS>>();
		for(String strTemp : gpsList) {
			String[] arryTemp = strTemp.split(",");
			if(null!=arryTemp[0].trim()&&!arryTemp[0].trim().equals("")) {
				String carcode = arryTemp[1];
				String  beijingtime = arryTemp[2];
				double  wgs84lng = Double.valueOf(arryTemp[3]);
				double  wgs84lat = Double.valueOf(arryTemp[4]);
				double  speed = Double.valueOf(arryTemp[5]);
				double  directionangle = Double.valueOf(arryTemp[6]);
				CarGPS carGPS = new CarGPS();
				carGPS.setAngle(directionangle);
				carGPS.setCarCode(carcode);
				carGPS.setGpsTime(beijingtime);
				carGPS.setLatitude(wgs84lat);
				carGPS.setLongitude(wgs84lng);
				carGPS.setSpeed(speed);
				if(null==result.get(carGPS.getCarCode())) {
					List<CarGPS> mapListTemp = new ArrayList<CarGPS>();
					mapListTemp.add(carGPS);
					result.put(carGPS.getCarCode(), mapListTemp);
				}else {
					result.get(carGPS.getCarCode()).add(carGPS);
				}
			}
		}
		logger.info("load gps file success ! ");
		return result;
	}
	
	public int[] getGridIndex(double x,double y) {
		int[] result = new int[] {-1,-1};
		double xmin = bounds[0];
		double ymin = bounds[1];
		double xmax = bounds[2];
		double ymax = bounds[3];
		double step = (double)gridSize/1852/60;
		if(x>=xmin&&x<=xmax&&y>=ymin&&y<=ymax) {
			result[0] = (int)((x-xmin)/step);
			result[1] = (int)((ymax-y)/step);
		}
		return result;
	}
	
	/**
	 * 根据距离最近切小于distance，角度小于matchAngle
	 * 获取gps相关联的路链
	 * 9个格子防止漏掉路链号
	 * @return
	 */
	public Map<String,LindNodeToployData> getLindNodeToployData(CarGPS carGPSTemp) {
		int[] gridIntArr = getGridIndex(carGPSTemp.getLongitude(),carGPSTemp.getLatitude());
		Map<String,LindNodeToployData> result = new HashMap<String,LindNodeToployData>();
		if(gridIntArr[0]!=-1 &&gridIntArr[1]!=-1) {
			for(int i=gridIntArr[0]-1;i<=gridIntArr[0]+1;i++) {
				for(int j=gridIntArr[1]-1;j<=gridIntArr[1]+1;j++) {
					List<LindNodeToployData> valueList = mapGridTopy.get(i+"-"+j);
					if(null!=valueList) {
						for(LindNodeToployData lntdTemp : valueList) {
							if(null==result.get(lntdTemp.getLinkid())) {
								result.put(lntdTemp.getLinkid(), lntdTemp);
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	public double[] getLinksBounds() {
		double[]  result = PBGeoShapeUtil.GetShapeFileBounds(workspacePath+File.separator+shapeFileName+".shp",shapeFileCharSet);
		logger.info("link bounds [minx :"+result[0]+" miny :"+result[1]+" maxx :"+result[2]+" maxy : "+result[3]+"]");
		return result;
	}
	
	public int getOrderSplitTime() {
		return orderSplitTime;
	}

	public void setOrderSplitTime(int orderSplitTime) {
		this.orderSplitTime = orderSplitTime;
	}

	public double getMatchAngle() {
		return matchAngle;
	}

	public void setMatchAngle(double matchAngle) {
		this.matchAngle = matchAngle;
	}

	public String getGpsFilePath() {
		return gpsFilePath;
	}

	public void setGpsFilePath(String gpsFilePath) {
		this.gpsFilePath = gpsFilePath;
	}

	public String getWorkspacePath() {
		return workspacePath;
	}

	public void setWorkspacePath(String workspacePath) {
		this.workspacePath = workspacePath;
	}

	public String getTopyFileName() {
		return topyFileName;
	}

	public void setTopyFileName(String topyFileName) {
		this.topyFileName = topyFileName;
	}

	public String getShapeFileName() {
		return shapeFileName;
	}

	public void setShapeFileName(String shapeFileName) {
		this.shapeFileName = shapeFileName;
	}

	public String getShapeFileCharSet() {
		return shapeFileCharSet;
	}

	public void setShapeFileCharSet(String shapeFileCharSet) {
		this.shapeFileCharSet = shapeFileCharSet;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public int getGridSize() {
		return gridSize;
	}

	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}
	
}
