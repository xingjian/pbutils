/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.hadooputils.test;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.utils.hadooputils.HBaseUtil;


/**  
 * 功能描述: HBaseUtil 测试
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年9月8日 下午2:49:34  
 */
public class BJJTWHBaseUtilTest {

	public Logger logger = LoggerFactory.getLogger(BJJTWHBaseUtilTest.class);
	//北京hbase存储byte,下面变量是用户转换对比用
	public static final byte[] COL_VALUE = Bytes.toBytes("VALUE");
	public static final byte[] COL_TEXT = Bytes.toBytes("TEXT");
	public static final byte[] COL_VER = Bytes.toBytes("VER");
	public static final byte[] COL_VT = Bytes.toBytes("VT");
	public static final byte[] COL_VERSIONINFO = Bytes.toBytes("VERSIONINFO");
	public static final byte[] COL_VERSION_IND = Bytes.toBytes("IND_CONF_VER");
	public static final byte[] COL_BATCH_CONF_ID = Bytes.toBytes("BATCH_CONF_ID");
	public static final byte[] COL_STATUS = Bytes.toBytes("STATUS");
	public static final byte[] COL_DESC = Bytes.toBytes("DESC");
	/**
	 * 测试GetHBaseConfiguration
	 * zbdb1（10.212.138.128） master
	 * zbdb2（10.212.138.130）
	 * zbdb3（10.212.138.129）
	 * zbdb4（10.212.138.191）
	 * zbdb5（10.212.138.192）
	 * zbdb6（10.212.138.193）
	 */
	@Test
	public void testGetHBaseConfiguration() {
		Configuration  configuration = HBaseUtil.GetHBaseConfiguration("10.212.138.128,10.212.138.130,10.212.138.129,10.212.138.191,10.212.138.192,10.212.138.193",
				"2181");
		Assert.assertNotNull(configuration);
	}
	
	/**
	 * 测试GetConnection
	 */
	@Test
	public void testGetConnection() {
		Connection  connection = HBaseUtil.GetConnection("10.212.138.128,10.212.138.130,10.212.138.129,10.212.138.191,10.212.138.192,10.212.138.193",
				"2181");
		Assert.assertNotNull(connection);
	}
	
	
	/**
	 * 测试GetTableNames
	 */
	@Test
	public void testGetTableNames() {
		System.setProperty("hadoop.home.dir", "D:\\hadoop-2.8.1");
		Connection  connection = HBaseUtil.GetConnection("10.212.138.128,10.212.138.130,10.212.138.129,10.212.138.191,10.212.138.192,10.212.138.193",
				"2181");
		List<String> list = HBaseUtil.GetTableNames(connection);
		for (String string : list) {
			logger.info(string);//INDICATOR_VALUE
		}
	}
	
	/**
	 * 获取tableName的数据
	 * 
	 * @param tableName
	 */
	@Test
	public  void testGetAllRecord() {
		try {
			System.setProperty("hadoop.home.dir", "D:\\hadoop-2.8.1");
			Connection  connection = HBaseUtil.GetConnection("10.212.138.128,10.212.138.130,10.212.138.129,10.212.138.191,10.212.138.192,10.212.138.193",
					"2181");
			String tableName = "INDICATOR_VALUE";
			Table table = connection.getTable(TableName.valueOf(tableName));
			Scan scan = new Scan();
			Filter filer = new PrefixFilter(Bytes.toBytes("0001000100000000000000000000000004000200e2110016-2017#12"));
	        scan.setFilter(filer);
			ResultScanner rs = table.getScanner(scan);
			for (Result r : rs) {
				for (Cell cell : r.rawCells()) {
					byte[] qua = CellUtil.cloneQualifier(cell);
					byte[] bytes = CellUtil.cloneValue(cell);
					if(bytesEquals(COL_VALUE, qua)){
						try{
							System.out.println(getDoubleValue(bytes));
						}catch(Exception e){}
					}
					if(bytesEquals(COL_TEXT, qua)){
						System.out.println(Bytes.toString(bytes));
					}
					if(bytesEquals(COL_VER, qua)){
						try{
							System.out.println(Bytes.toInt(bytes));
						}catch(Exception e){}
					}
					if(bytesEquals(COL_VT, qua)){
						try{
							System.out.println(Bytes.toInt(bytes));
						}catch(Exception e){}
					}
					if(bytesEquals(COL_VERSIONINFO, qua)){
						System.out.println(Bytes.toString(bytes));
					}
					if(bytesEquals(COL_VERSION_IND, qua)){
						try{
							System.out.println(Bytes.toInt(bytes));
						}catch(Exception e){}
					}
					if(bytesEquals(COL_BATCH_CONF_ID, qua)){
						System.out.println(Bytes.toString(bytes));
					}
					if(bytesEquals(COL_STATUS, qua)){
						System.out.println(Bytes.toString(bytes));
					}
					if(bytesEquals(COL_DESC, qua)){
						System.out.println(Bytes.toString(bytes));
					}
				}
			}
			table.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 比较两个byte数组内容是否一样
	 * @param b1
	 * @param b2
	 * @return
	 */
	public boolean bytesEquals(byte[] b1, byte[] b2){
		if(b1 == null || b2 == null){
			return false;
		}
		int len = b1.length;
		if(len != b2.length){
			return false;
		}
		for(int i=0; i<len; i++){
			if(b1[i] != b2[i]){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 根据hbase bytes 转换成double
	 * @param ret
	 * @return
	 */
	public Double getDoubleValue(byte[] ret) {
		if (ret == null) {
			return null;
		}
		try{
			return Bytes.toDouble(ret);
		}catch(Exception e){
			try{
				return Double.parseDouble(Bytes.toString(ret));
			}catch(Exception e1){
				return null;
			}
		}
	}
}
