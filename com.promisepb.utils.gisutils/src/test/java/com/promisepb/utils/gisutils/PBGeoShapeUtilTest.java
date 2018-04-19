/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.gisutils;

import org.geotools.data.DataStore;
import org.junit.Test;

import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;

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
		String crs = "EPSG:4326";
		String result = PBGeoShapeUtil.ShapeToPostGIS(shapePath, dataStor, charSet, tableName, MultiLineString.class, crs);
		System.out.println(result);
	}
	
	@Test
	public void testCreatePointShapeByTxt() {
		String crs = "EPSG:4326";
		String txtpath = "E:\\tempworkspace\\zzgml\\zz-bus.csv";
		String encoding = "GBK";
		String topath = "E:\\tempworkspace\\zzgml\\zz-bus-gps.shp";
		String[] attriDesc = new String[]{"id:String","beijingTime:String","x:double","y:double","speed:double","mzl:double"};
		PBGeoShapeUtil.CreatePointShapeByTxt(txtpath, ",", crs, encoding, attriDesc, topath);
	}
	
	/**
	 * 导入林鹏飞北京ring_road
	 */
	@Test
	public void testShapeToPostGISLPFRing() {
		String shapePath = "F:\\project\\北京工业大学\\林鹏飞\\shape\\ring_road\\sixring.shp";
		DataStore dataStor = PBGISDBUtil.GetDataStoreFromPostGIS("localhost", "5432", "gistest", "postgis", "postgis","public");
		String charSet = "GBK";
		String tableName = "sixring_lpf";
		String crs = "EPSG:4326";
		String result = PBGeoShapeUtil.ShapeToPostGIS(shapePath, dataStor, charSet, tableName, MultiPolygon.class, crs);
		System.out.println(result);
	}
	
	/**
	 * 导入北京行政区划shape数据到postgis
	 * 2180302
	 */
	@Test
	public void testShapeToPostGIS1() {
		String shapePath = "E:\\gisworkspace\\shape_data\\北京行政区划\\beijing_adcd.shp";
		DataStore dataStor = PBGISDBUtil.GetDataStoreFromPostGIS("localhost", "5432", "gis_data", "postgres", "000000","public");
		String charSet = "GBK";
		String tableName = "beijing_adcd";
		Class classzs = MultiPolygon.class;
		String crs = "EPSG:4326";
		String result = PBGeoShapeUtil.ShapeToPostGIS(shapePath, dataStor, charSet, tableName, classzs, crs);
		System.out.println(result);
	}
	
	/**
	 * 导入北京行政区划shape数据到postgis
	 * 2180304
	 */
	@Test
	public void testShapeToPostGIS2() {
		String shapePath = "E:\\gisworkspace\\shape_data\\北京整体面要素\\beijing_border.shp";
		DataStore dataStor = PBGISDBUtil.GetDataStoreFromPostGIS("localhost", "5432", "gis_data", "postgres", "000000","public");
		String charSet = "GBK";
		String tableName = "beijing_out_border";
		Class classzs = MultiPolygon.class;
		String crs = "EPSG:4326";
		String result = PBGeoShapeUtil.ShapeToPostGIS(shapePath, dataStor, charSet, tableName, classzs, crs);
		System.out.println(result);
	}
	
}
