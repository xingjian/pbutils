/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.gisdata;

import com.promisepb.utils.stringutils.PBStringUtil;

/**  
 * 功能描述: 出租汽车GPS数据
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年10月10日 下午3:44:10  
 */
public class CarGPS implements Comparable<CarGPS>{
    //类型
	private String type;
    //gps数据时间
    private String gpsTime;
    //经度
    private double longitude;
    //纬度
    private double latitude;
    //速度
    private double speed;
    //方向角
    private double angle;
    //状态
    private int status;
	//车辆唯一编码
    private String carCode;
    
    public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getGpsTime() {
		return gpsTime;
	}
	public void setGpsTime(String gpsTime) {
		this.gpsTime = gpsTime;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCarCode() {
		return carCode;
	}
	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}
	public double getAngle() {
		return angle;
	}
	public void setAngle(double angle) {
		this.angle = angle;
	}
	@Override
	public int compareTo(CarGPS o) {
		return PBStringUtil.CompareDate(this.gpsTime, o.getGpsTime());
	} 
    
}
