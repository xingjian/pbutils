/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.gisdata;

import com.vividsolutions.jts.geom.MultiLineString;

/**  
 * 功能描述:四维link信息
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年10月10日 下午9:43:15  
 */
public class SWLink {
	//图幅号
	private String mapid;
	//路链id
	private String linkid;
	//种别代码数
	private String kindNum;
	//种类 前两位表示：高速路 0x00都市高速路 0x01国道 0x02省道 0x03县道 0x04乡镇村道 0x06其它道路 0x08九级路 0x09轮渡 0x0a行人道路 0x0b
	private String kind;
	//方向 0:未调查(默认为双向)  1:双向  2:顺方向(单向通行，通行方向为起点到终点方向) 3:逆方向(单向通行，通行方向为终点到起点方向)
	private String direction;
	//路链长度
	private double length;
	//画线方向起点号码
	private String snodeID;
	//画线方向终点号码
	private String enodeID;
	//集合对象
	private MultiLineString mLine;
	public String getMapid() {
		return mapid;
	}
	public void setMapid(String mapid) {
		this.mapid = mapid;
	}
	public String getLinkid() {
		return linkid;
	}
	public void setLinkid(String linkid) {
		this.linkid = linkid;
	}
	public String getKindNum() {
		return kindNum;
	}
	public void setKindNum(String kindNum) {
		this.kindNum = kindNum;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public double getLength() {
		return length;
	}
	public void setLength(double length) {
		this.length = length;
	}
	public String getSnodeID() {
		return snodeID;
	}
	public void setSnodeID(String snodeID) {
		this.snodeID = snodeID;
	}
	public String getEnodeID() {
		return enodeID;
	}
	public void setEnodeID(String enodeID) {
		this.enodeID = enodeID;
	}
	public MultiLineString getmLine() {
		return mLine;
	}
	public void setmLine(MultiLineString mLine) {
		this.mLine = mLine;
	}
}
