/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.gisutils;

import java.sql.Connection;
import java.util.List;

import org.geotools.data.DataStore;
import org.junit.Test;

import com.promisepb.utils.dbutils.PBDBConnection;
import com.vividsolutions.jts.geom.Polygon;

/**  
 * 功能描述:PBGISDBUtil测试类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年11月2日 上午9:55:00  
 */
public class PBGISDBUtilTest {

	@Test
	public void testGetDataStoreFromPostGIS() throws Exception {
		String urlPG = "jdbc:postgresql://localhost:5433/opengis";
        String usernamePG = "postgres";
        String passwdPG = "000000";
        Connection connectionPG = PBDBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        double[] result = PBGISDBUtil.GetPostGISTableBounds(connectionPG,"lpf_bj_gs","the_geom");
        for(double d : result) {
			System.out.println(d);
		}
		connectionPG.close();
		double[] result1 = PBGeoShapeUtil.GetShapeFileBounds("G:\\项目文档\\公交都市\\工大\\林鹏飞\\北京导航图\\北京导航图\\高速_polyline.shp", "GBK");
		for(double d : result1) {
			System.out.println(d);
		}
	}
	
	@Test
	public void testGetAttributeByTableName() {
		DataStore dataStore = PBGISDBUtil.GetDataStoreFromPostGIS("localhost", "5433", "opengis", "postgres", "000000", "public");
		List<String> list = PBGISDBUtil.GetGeometryNameByTableName(dataStore, "lpf_beijing_roadall");
		for(String str : list) {
			System.out.println(str);
		}
	}
	
}
