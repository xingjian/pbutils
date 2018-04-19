/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.poiutils;

import java.sql.Connection;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.utils.dbutils.PBDBConnection;

/**  
 * 功能描述:测试PBPOIExcelUtil
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年10月26日 下午6:23:21  
 */
public class PBPOIExcelUtilTest {

	public  Logger logger = LoggerFactory.getLogger(PBPOIExcelUtilTest.class);
	
	@Test
	public void testExportCSVBySQL_PostgreSQL() {
		String urlPG = "jdbc:postgresql://localhost:5432/sw_navigation";
        String usernamePG = "postgis";
        String passwdPG = "postgis";
        Connection connectionPG = PBDBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        String sql = "SELECT fid, \"Object_ID\", \"MapID\", \"ID\", \"Kind_num\", \"Kind\", \"Width\", \"Direction\", \"Toll\", \"Const_St\", \"UndConCRID\", \"SnodeID\", \"EnodeID\", \"FuncClass\", \"Length\", \"DetailCity\", \"Through\", \"UnThruCRID\", \"Ownership\", \"Road_Cond\", \"Special\", \"AdminCodeL\", \"AdminCodeR\", \"Uflag\", \"OnewayCRID\", \"AccessCRID\", \"SpeedClass\", \"LaneNumS2E\", \"LaneNumE2S\", \"LaneNum\", \"Vehcl_Type\", \"Elevated\", \"Structure\", \"UseFeeCRID\", \"UseFeeType\", \"SpdLmtS2E\", \"SpdLmtE2S\", \"SpdSrcS2E\", \"SpdSrcE2S\", \"DC_Type\", \"NoPassCRID\", \"Shape_Leng\"FROM navigation_2016;";
        String result = PBPOIExcelUtil.ExportCSVBySQL(sql, connectionPG, "d:\\navigation_2016.csv", 0,null);
        logger.info(result);
	}
	
	@Test
	public void testExportCSVBySQL_Oracle() {
		String url = "jdbc:oracle:thin:@192.168.1.188:1521:orcl";
        String username = "buscitynew";
        String passwd = "admin123ttyj7890uiop";
        Connection connection = PBDBConnection.GetOracleConnection(url, username, passwd);
        String sql = "select t.adcd_name,t.busline_name from ADCD_BUSLINE_REF t where adcd_name='海淀区'";
        String result = PBPOIExcelUtil.ExportCSVBySQL(sql, connection, "d:\\ADCD_BUSLINE_REF.csv", 50,null);
        logger.info(result);
	}
	
	@Test
	public void testMergeCSVFiles() {
		String csvPath = "D:\\2017jgjjcq-data-20180202";
		String exportPath = "d:\\2017jgjjcq-data-20180202-merge.csv";
		PBPOIExcelUtil.MergeCSVFiles(csvPath, exportPath, "UTF-8");
	}
	
	@Test
	public void testArrayToCSVStr() {
		String[] testArrString = new String[3];
		testArrString[0] = "4DE8CE166D0BF007E0530100007FBA4B";
		testArrString[1] = "10-APR-17 08.04.00.000000 PM";
		testArrString[2] = "东四十条,朝阳门,建国门,永安里,国贸,大望路,四惠,四惠东,高碑店,传媒大学,双桥,管庄,八里桥,通州北苑,果园,九棵树,梨园,临河里,土桥,";
		String result = PBPOIExcelUtil.ArrayToCSVStr(testArrString);
		System.out.println(result);
		String[] splitArr = result.trim().split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1); //双引号内的逗号不分割  双引号外的逗号进行分割
		for(String str : splitArr) {
			System.out.println(str);
		}
	}
}
