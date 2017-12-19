/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.gisutils;

import org.geotools.data.DataStore;
import org.junit.Test;

import com.vividsolutions.jts.geom.MultiLineString;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年9月19日 下午9:42:43  
 */
public class PBGeoShapeUtilTest {

	
	@Test
	public void testShapeToPostGIS() {
		String shapePath = "G:\\项目文档\\TOCC\\gis\\110000chooseroad\\gd_beijing.shp";
		DataStore dataStor = PBGISDBUtil.GetDataStoreFromPostGIS("localhost", "5433", "opengis", "postgres", "000000","public");
		String charSet = "GBK";
		String tableName = "gd_beijing";
		Class classzs = MultiLineString.class;
		String crs = "EPSG:4326";
		String result = PBGeoShapeUtil.ShapeToPostGIS(shapePath, dataStor, charSet, tableName, classzs, crs);
		System.out.println(result);
	}
	
	/**
	 * 导入林鹏飞北京导航数据
	 */
	@Test
	public void testShapeToPostGISLPF() {
		String shapePath = "F:\\toccworkspace\\lpfgps-sh\\sh_shp\\乡镇村道.shp";
		DataStore dataStor = PBGISDBUtil.GetDataStoreFromPostGIS("localhost", "5433", "opengis", "postgres", "000000","public");
		String charSet = "GBK";
		String tableName = "lpf_sh_xzcd";
		Class classzs = MultiLineString.class;
		String crs = "EPSG:4326";
		String result = PBGeoShapeUtil.ShapeToPostGIS(shapePath, dataStor, charSet, tableName, classzs, crs);
		System.out.println(result);
	}
	
	@Test
	public void testCreatePointShapeByTxt() {
		String crs = "EPSG:4326";
		String txtpath = "F:\\toccworkspace\\didigps\\result\\B_G_2016_08_01_da.csv";
		String encoding = "GBK";
		String topath = "F:\\toccworkspace\\lpfgps\\gps_03_xj.shp";
		String[] attriDesc = new String[]{"fid:int","id:String","beijingTime:String","x:double","y:double","speed:double","directionAngle:double"};
		PBGeoShapeUtil.CreatePointShapeByTxt(txtpath, ",", crs, encoding, attriDesc, topath);
	}
}
