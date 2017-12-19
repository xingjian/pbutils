/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.gpsmatch.vo;

import com.promisepb.utils.gisdata.CarGPS;
import com.promisepb.utils.gisdata.SWLink;
import com.vividsolutions.jts.geom.Coordinate;

/**  
 * 功能描述: 出租车gps和四维路链匹配结果
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年10月11日 下午1:41:17  
 */
public class CarGPSMatchSWLinkResult {

	//匹配的路链
	private LindNodeToployData swLink;
	//原始的gps点
	private CarGPS oldGPS;
	//新的gps点
	private Coordinate newGPS;
	//车辆编号
	private String carCode;
	//gps时间
	private String timeStr;
	private String status;
	
	public LindNodeToployData getSwLink() {
		return swLink;
	}
	public void setSwLink(LindNodeToployData swLink) {
		this.swLink = swLink;
	}
	public CarGPS getOldGPS() {
		return oldGPS;
	}
	public void setOldGPS(CarGPS oldGPS) {
		this.oldGPS = oldGPS;
	}
	
	public Coordinate getNewGPS() {
		return newGPS;
	}
	public void setNewGPS(Coordinate newGPS) {
		this.newGPS = newGPS;
	}
	public String getCarCode() {
		return carCode;
	}
	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}
	public String getTimeStr() {
		return timeStr;
	}
	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
