/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.hadooputils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;

/**  
 * 功能描述: hbase 帮助类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年9月5日 下午5:56:13  
 */
public class HBaseUtil {
	
	public static String HBASE_ZOOKEEPER_QUORUM = "hbase.zookeeper.quorum";
	public static String HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT = "hbase.zookeeper.property.clientPort";
	/**
	 * 获取hbase Configuration
	 * @param ips 格式：192.168.0.1,192.168.0.2,192.168.0.3,192.168.0.4,192.168.0.5
	 * @param clientPort
	 * @param hbaseMaster  格式ip:60000
	 * @return
	 */
	public static Configuration  GetHBaseConfiguration(String ips,String clientPort) {
		Configuration config = HBaseConfiguration.create();
		//与hbase/conf/hbase-site.xml中hbase.zookeeper.quorum配置的值相同
		config.set(HBASE_ZOOKEEPER_QUORUM, ips);
		//与hbase/conf/hbase-site.xml中hbase.zookeeper.property.clientPort配置的值相同
		config.set(HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT, clientPort);
        return config;
	}
	
	/**
	 * 获取hbase connection
	 * @param config
	 * @return
	 */
	public static Connection GetConnection(Configuration config) {
		Connection conn = null;
		try {
			conn = ConnectionFactory.createConnection(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * 获取hbase connection
	 * @param ips
	 * @return
	 */
	public static Connection GetConnection(String ips,String clientPort) {
		Connection conn = null;
		try {
			conn = ConnectionFactory.createConnection(GetHBaseConfiguration(ips,clientPort));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * 获取hbase下面表名称集合
	 * @param con
	 * @return
	 */
	public static List<String> GetTableNames(Connection con) {
		List<String> result = new ArrayList<String>();
		try {
			Admin admin = con.getAdmin();
			TableName[] tableNames = admin.listTableNames();
			for(TableName tableNameTemp : tableNames) {
				result.add(tableNameTemp.getNameAsString());
			}
			admin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 根据连接和表名获取table 对象
	 * @param con
	 * @param tableName
	 * @return
	 */
	public static Table GetTable(Connection con,String tableName) {
		Table table = null;
		try {
			table = con.getTable(TableName.valueOf(tableName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return table;
	}
}
