/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.gpsmatch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.junit.Test;

import com.promisepb.utils.dbutils.PBDBConnection;
import com.promisepb.utils.fileutils.PBFileUtil;
import com.promisepb.utils.gisutils.PBGTGeometryUtil;
import com.promisepb.utils.stringutils.PBStringUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年11月11日 下午11:22:37  
 */
public class GPSMatchSWLinkTest {

	@Test
	public void importGPSData() throws Exception {
		System.out.println(PBStringUtil.GetCurrentDateString());
		String dataPath = "F:\\toccworkspace\\lpfgps-sh\\S_G_2016_12_22_17_da_match_success.csv";
		List<String> list = PBFileUtil.ReadFileByLine(dataPath,"GBK");
		String tableName = "lpf_sh_gps20161222_17";
		String urlPG = "jdbc:postgresql://localhost:5433/opengis";
		String insertSQL = "INSERT INTO "+tableName+"(carcode, beijingtime, wgs84lng, wgs84lat, speed,directionangle,v_x,v_y,linkid,startlength,endlength,status,old_geom,new_geom,predistance) VALUES (?, to_timestamp(?,'yyyy-mm-dd hh24:mi:ss'), ?, ?, ?,?,?,?,?,?,?,?,st_geometryfromtext(?,4326),st_geometryfromtext(?,4326),?)";
        String usernamePG = "postgres";
        String passwdPG = "000000";
        Connection connectionPG = PBDBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        PreparedStatement psInsert = connectionPG.prepareStatement(insertSQL);
		int index = 0;
        for(String strTemp : list) {
			String[] arryTemp = strTemp.split(",");
			if(null!=arryTemp[0].trim()&&!arryTemp[0].trim().equals("")) {
				String carcode = arryTemp[0];
				String  beijingtime = arryTemp[1];
				double  wgs84lng = Double.valueOf(arryTemp[2]);
				double  wgs84lat = Double.valueOf(arryTemp[3]);
				double  speed = Double.valueOf(arryTemp[4]);
				double  directionangle = Double.valueOf(arryTemp[5]);
				double  v_x = Double.valueOf(arryTemp[6]);
				double  v_y = Double.valueOf(arryTemp[7]);
				String linkid = arryTemp[8];
				psInsert.setString(1, carcode);
				psInsert.setString(2, beijingtime);
				psInsert.setDouble(3, wgs84lng);
				psInsert.setDouble(4, wgs84lat);
				psInsert.setDouble(5, speed);
				psInsert.setDouble(6, directionangle);
				psInsert.setDouble(7, v_x);
				psInsert.setDouble(8, v_y);
				psInsert.setString(9, linkid);
				psInsert.setDouble(10, Double.valueOf(arryTemp[9]));
				psInsert.setDouble(11, Double.valueOf(arryTemp[10]));
				psInsert.setString(12, arryTemp[11]);
				psInsert.setString(13, PBGTGeometryUtil.createPoint(wgs84lng, wgs84lat).toText());
				psInsert.setString(14, PBGTGeometryUtil.createPoint(v_x, v_y).toText());
				psInsert.setDouble(15, Double.valueOf(arryTemp[12]));
				psInsert.addBatch();
				index++;
				if(index%100000==0) {
					psInsert.executeBatch();
					System.out.println(index);
				}
			}
		}
        psInsert.executeBatch();
        System.out.println(index);
		System.out.println(PBStringUtil.GetCurrentDateString());
		psInsert.close();
		connectionPG.close();
	}
}
