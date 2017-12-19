/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.gpsmatch.vo;

import com.promisepb.utils.gisdata.LinkPoint;
import com.promisepb.utils.gisdata.SWLink;

/**  
 * 功能描述:拓扑数据的封装
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年10月20日 下午2:06:42  
 */
public class LindNodeToployData  extends SWLink{
	
	public double angle;
	public LinkPoint firstPoint;
	public LinkPoint lastPoint;
	public double getAngle() {
		return angle;
	}
	public void setAngle(double angle) {
		this.angle = angle;
	}
	public LinkPoint getFirstPoint() {
		return firstPoint;
	}
	public void setFirstPoint(LinkPoint firstPoint) {
		this.firstPoint = firstPoint;
	}
	public LinkPoint getLastPoint() {
		return lastPoint;
	}
	public void setLastPoint(LinkPoint lastPoint) {
		this.lastPoint = lastPoint;
	}
	
		
}
