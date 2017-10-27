/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.gpsmatch;

import java.util.ArrayList;
import java.util.List;

import com.promisepb.utils.gisdata.CarGPS;
import com.promisepb.utils.gisdata.SWLink;
import com.promisepb.utils.gpsmatch.data.CarGPSMatchSWLinkResult;

/**  
 * 功能描述: GPS匹配帮助类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年10月10日 下午3:22:57  
 */
public class GPSMatchUtil {

	private double distance = 30;//设定误差为30米
	
	/**
	 * 匹配cargps点到四维路链，并返回匹配结果
	 * @param carGPS 车辆gps点
	 * @param links gps附近的路链
	 * @return 匹配结果
	 */
	public static CarGPSMatchSWLinkResult GetCarGPSMatchSWLinkResult(CarGPS carGPS,List<SWLink> links) {
		CarGPSMatchSWLinkResult result = new CarGPSMatchSWLinkResult();
		return result;
	}
	
	/**
	 * 根据四维的导航数据路链生成拓扑关系文件
	 */
	public static void GeneratorTopolyFileBySWLink() {
		String querySQL = "select id,direction,length,snodeid,enodeid,st_astext(ST_Transform(st_endpoint(ST_GeometryN(the_geom,1)),900913)) endpoint,st_astext(ST_Transform(st_startpoint(ST_GeometryN(the_geom,1)),900913)) startpoint from navigationline";
		List<String> linkStrList = new ArrayList<String>();
	}
	
}
