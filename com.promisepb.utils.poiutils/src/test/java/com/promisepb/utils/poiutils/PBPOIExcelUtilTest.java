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
        String result = PBPOIExcelUtil.ExportCSVBySQL(sql, connectionPG, "d:\\navigation_2016.csv", 0);
        logger.info(result);
	}
	
	@Test
	public void testExportCSVBySQL_Oracle() {
		String url = "jdbc:oracle:thin:@192.168.1.188:1521:orcl";
        String username = "buscitynew";
        String passwd = "admin123ttyj7890uiop";
        Connection connection = PBDBConnection.GetOracleConnection(url, username, passwd);
        String sql = "select t.adcd_name,t.busline_name from ADCD_BUSLINE_REF t where adcd_name='海淀区'";
        String result = PBPOIExcelUtil.ExportCSVBySQL(sql, connection, "d:\\ADCD_BUSLINE_REF.csv", 50);
        logger.info(result);
	}
}
