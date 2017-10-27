/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.gpsmatch.data;

import java.util.List;

/**  
 * 功能描述:拓扑数据的封装
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年10月20日 下午2:06:42  
 */
public class LindNodeToployData {
	//父节点
	private List<LindNodeToployData> parentList;
	//子节点
	private List<LindNodeToployData> childrenList;
	//长度
	private double length;
	//方向
	private String direction;
	//路段ID
	public String linkID;
	//nodeID
	public String nodeID;
	
	public List<LindNodeToployData> getParentList() {
		return parentList;
	}
	public void setParentList(List<LindNodeToployData> parentList) {
		this.parentList = parentList;
	}
	public List<LindNodeToployData> getChildrenList() {
		return childrenList;
	}
	public void setChildrenList(List<LindNodeToployData> childrenList) {
		this.childrenList = childrenList;
	}
	public double getLength() {
		return length;
	}
	public void setLength(double length) {
		this.length = length;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getLinkID() {
		return linkID;
	}
	public void setLinkID(String linkID) {
		this.linkID = linkID;
	}
	public String getNodeID() {
		return nodeID;
	}
	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}
	
}
