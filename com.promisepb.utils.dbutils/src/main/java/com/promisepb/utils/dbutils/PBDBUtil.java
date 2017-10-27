package com.promisepb.utils.dbutils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.promisepb.utils.dbutils.vo.ColumnDesc;

/**  
 * 功能描述: 数据库帮助类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年10月23日 下午3:42:36  
 */
@SuppressWarnings("all")
public class PBDBUtil {
    
    /**
     * 根据数据库的不同返回对应的不同映射
     * @param dbType
     * @return
     */
    public static Map<String,String> GetColumnMapping(PBDBType dbType){
        Map<String,String> retMap = new HashMap<String,String>();
        if(dbType.equals(PBDBType.PostgreSQL)){
            retMap.put("varchar", "String");
            retMap.put("text", "String");
            retMap.put("int8", "int");
            retMap.put("float8", "double");
            retMap.put("int4", "int");
            retMap.put("float4", "float");
            retMap.put("int2", "int");
            retMap.put("date", "date");
            retMap.put("String", "varchar");
            retMap.put("String", "text");
        }else if(dbType.equals(PBDBType.Oracle)){
            retMap.put("var", "varchar");
            retMap.put("text", "text");
            retMap.put("int8", "bigint");
            retMap.put("float8", "double precision");
            retMap.put("int4", "integer");
            retMap.put("float4", "real");
            retMap.put("int2", "smallint");
            retMap.put("date", "date");
        }
        return retMap;
    }
    
    /**
     * 复制表
     * 支持跨数据库
     * @param source
     * @param des
     * @return
     */
    public static String CopyTable(Connection source,String sourceTName,Connection des,String desTName){
        String ret = "success";
        boolean boo1 = IsTableExist(source,sourceTName);
        if(boo1){
            boolean boo2 = IsTableExist(des,desTName);
            if(boo2){//如果存在的话，验证表结构，并插入数据
                
            }else{//创建表，并插入数据
                
            }
        }else{
            ret = sourceTName+" is not exist.";
        }
        return ret;
    }
    
    /**
     * 通过sql语句对数据进行copy,前提是表必须存在
     * @param source 数据源链接
     * @param dbType 源数据源类型，为了分页语句使用
     * @param sourceSQL 数据源的查询语句
     * @param des 目标数据源连接
     * @param desSQL 目标执行插入的语句
     * @param pageNum 如果数据量过大的话，会自动分页 。默认5000
     * @param columnType String int double float 
     * @return 执行结果状态
     */
    public static String CopyTable(Connection source,PBDBType dbType,String sourceSQL,Connection des,String desSQL,int pageNum,String[] columnType) throws Exception{
        ResultSet rs = source.createStatement().executeQuery(sourceSQL);
        PreparedStatement psInsert = des.prepareStatement(desSQL);
        int index = 0;
        while(rs.next()){
            for(int i=0;i<columnType.length;i++){
                String columnStr = columnType[i];
                if(columnStr.trim().toLowerCase().equals("string")){
                    psInsert.setString(i+1, rs.getString(i+1));
                }else if(columnStr.trim().toLowerCase().equals("int")){
                    psInsert.setInt(i+1, rs.getInt(i+1));
                }else if(columnStr.trim().toLowerCase().equals("double")){
                    psInsert.setDouble(i+1, rs.getDouble(i+1));
                }else if(columnStr.trim().toLowerCase().equals("float")){
                    psInsert.setFloat(i+1, rs.getFloat(i+1));
                }else{
                    psInsert.setString(i+1, rs.getString(i+1));
                }
            }
            psInsert.addBatch();
            index++;
            if(index%pageNum==0){
                psInsert.executeBatch();
            }
        }
        psInsert.executeBatch();
        psInsert.close();
        rs.close();
        return "success";
    }
    
    
    /**
     * 获取表结构信息
     * 列表,类型,长度,小数部分的位数,是否为空(0false-1true),注释,是否为主键(true,false)
     * @param connection
     * @param tName
     * @return
     */
    public static List<ColumnDesc> GetTableStructure(Connection connection,String tName) throws Exception{
        List<ColumnDesc> ret = new ArrayList<ColumnDesc>();
        DatabaseMetaData dbMeta = connection.getMetaData(); 
        ResultSet pkRS = dbMeta.getPrimaryKeys(null, null, tName);
        Map<String,String> idMap = new HashMap<String,String>();
        while(pkRS.next()){
            String pkString = pkRS.getString("COLUMN_NAME");
            idMap.put(pkString, "1");
        }
        ResultSet colRet = null;
        if(GetDataBaseTypeConnection(connection)==PBDBType.DM) {
        	String userName = connection.getMetaData().getUserName().toUpperCase();
        	colRet = dbMeta.getColumns(connection.getCatalog(),userName, tName,"%"); 
        }else {
        	colRet = dbMeta.getColumns(connection.getCatalog(),"%", tName,"%"); 
        }
         
        while(colRet.next()) { 
            String columnName = colRet.getString("COLUMN_NAME");
            String columnType = colRet.getString("TYPE_NAME");
            int datasize = colRet.getInt("COLUMN_SIZE");
            int digits = colRet.getInt("DECIMAL_DIGITS");
            int nullable = colRet.getInt("NULLABLE");
            String remarks= colRet.getString("REMARKS");
            boolean isPK = idMap.containsKey(columnName);
            ColumnDesc columnDesc = new ColumnDesc(columnName,null==remarks?"无":remarks,columnType,isPK?"是":"否",nullable==1?"是":"否","无");
            ret.add(columnDesc);
        }
        pkRS.close();
        colRet.close();
        return ret;
    }
    
    /**
     * 判断表是否存在
     * @return 
     */
    public static boolean IsTableExist(Connection connection,String tName){
        boolean boo = false;
        try {
            DatabaseMetaData dmd = connection.getMetaData();
            //%"就是表示*的意思，也就是任意所有
            ResultSet tableRet = dmd.getTables(null, "%",tName,new String[]{"TABLE"});
            boo = tableRet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        return boo;
    }
    
    /**
     * 根据连接获取所有表名 格式如: user:用户表
     * @param connection
     * @return
     */
    public static List<String> GetTables(Connection connection){
        List<String> retList = new ArrayList<String>();
        try{
            DatabaseMetaData dmd = connection.getMetaData();
            String[] types   =   {"TABLE"};
            PBDBType dbType = PBDBUtil.GetDataBaseTypeConnection(connection);
            ResultSet rs = null;
            if(dbType.equals(PBDBType.PostgreSQL)){
                rs = dmd.getTables(connection.getCatalog(), "", null, types);
            }else{
                rs = dmd.getTables(connection.getCatalog(), dmd.getUserName(), null, types);
            }
            while(rs.next()){
                String tableName = rs.getObject("TABLE_NAME").toString();
                String remark = rs.getString("REMARKS");
                retList.add(tableName+":"+(null==remark?"无":remark));
            }
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return retList;
    }
    
    /**
     * 根据连接获取所有视图  格式如: user:用户表
     * @param connection
     * @return
     */
    public static List<String> GetViews(Connection connection){
        List<String> retList = new ArrayList<String>();
        try{
            DatabaseMetaData dmd = connection.getMetaData();
            String[] types   =   {"VIEW"};
            ResultSet rs = dmd.getTables(null, null, null, types);
            while(rs.next()){
                String tableName = rs.getObject("TABLE_NAME").toString();
                String remark = rs.getString("REMARKS");
                retList.add(tableName+":"+(null==remark?"无":remark));
            }
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return retList;
    }
    
    /**
     * 通过driverName是否包含关键字判断  数据库类型
     * @return
     * @throws SQLException
     */
    public static PBDBType GetDataBaseTypeConnection(Connection connection) throws SQLException {  
        //通过driverName是否包含关键字判断  
        if (connection.getMetaData().getDriverName().toUpperCase().indexOf("MYSQL") != -1) {  
            return PBDBType.MySQL;  
        }else if (connection.getMetaData().getDriverName().toUpperCase().indexOf("SQL SERVER") != -1) {  
            //sqljdbc与sqljdbc4不同，sqlserver中间有空格
            return PBDBType.SQLServer;  
        }else if(connection.getMetaData().getDriverName().toUpperCase().indexOf("POSTGRESQL") != -1){
            return PBDBType.PostgreSQL;
        }else if(connection.getMetaData().getDriverName().toUpperCase().indexOf("ORACLE") != -1){
            return PBDBType.Oracle;
        }else if(connection.getMetaData().getDriverName().toUpperCase().indexOf("DM") != -1){
            return PBDBType.DM;
        }
        return null;  
    }
    
    /**
     * 通过driverName是否包含关键字判断  数据库类型
     * @return
     * @throws SQLException
     */
    public static PBDBType GetDataBaseTypeByJDBCURL(String jdbcURL){  
        //通过driverName是否包含关键字判断  
        if (jdbcURL.toUpperCase().indexOf("MYSQL") != -1) {  
            return PBDBType.MySQL;  
        }else if (jdbcURL.toUpperCase().indexOf("SQL SERVER") != -1) {  
            //sqljdbc与sqljdbc4不同，sqlserver中间有空格  
            return PBDBType.SQLServer;  
        }else if(jdbcURL.toUpperCase().indexOf("POSTGRESQL") != -1){
            return PBDBType.PostgreSQL;
        }else if(jdbcURL.toUpperCase().indexOf("ORACLE") != -1){
            return PBDBType.Oracle;
        }else if(jdbcURL.toUpperCase().indexOf("DM") != -1) {
        	return PBDBType.DM;
        }
        return null;  
    }
    
}
