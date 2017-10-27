/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.dbutils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.promisepb.utils.dbutils.vo.ColumnDesc;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年10月24日 下午4:39:22  
 */
public class PBDBUtilTest {

	@Test
    public void testGetViewStructure(){
        try {
        	Connection connectionDM = PBDBConnection.GetDMConnection("jdbc:dm://10.212.138.110:5236", "taxi", "admin123");
            List<String> views = PBDBUtil.GetTables(connectionDM);
            for(String viewname : views){
                try {
                	 List<ColumnDesc> result = PBDBUtil.GetTableStructure(connectionDM, viewname.replace(":", ""));
                     for(ColumnDesc s : result){
                         System.out.println(s.getName());
                     }
                }catch(Exception e) {
                	System.out.println(viewname);
                }
               
            }
        }catch(Exception e) {
        	e.printStackTrace();
        }
    }
	
	@Test
    public void testGetDMColumn(){
        try {
        	Connection connectionDM = PBDBConnection.GetDMConnection("jdbc:dm://10.212.138.110:5236", "hgj", "admin123456");
        	DatabaseMetaData dbMeta = connectionDM.getMetaData(); 
            ResultSet pkRS = dbMeta.getPrimaryKeys(null, null, "DL_COMMONBUS");
            Map<String,String> idMap = new HashMap<String,String>();
            while(pkRS.next()){
                String pkString = pkRS.getString("COLUMN_NAME");
                idMap.put(pkString, "1");
            }
            ResultSet colRet = dbMeta.getColumns(connectionDM.getCatalog(),"HGJ", "DL_COMMONBUS","%"); 
            while(colRet.next()) { 
                String columnName = colRet.getString("COLUMN_NAME");
                String columnType = colRet.getString("TYPE_NAME");
                int datasize = colRet.getInt("COLUMN_SIZE");
                int digits = colRet.getInt("DECIMAL_DIGITS");
                int nullable = colRet.getInt("NULLABLE");
                String remarks= colRet.getString("REMARKS");
                System.out.println(columnName);
            }
        }catch(Exception e) {
        	e.printStackTrace();
        }
    }
}
