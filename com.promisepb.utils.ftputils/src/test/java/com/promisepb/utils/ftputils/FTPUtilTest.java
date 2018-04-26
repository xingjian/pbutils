/**
* @Copyright@2018 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.ftputils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.promisepb.utils.ftputils.vo.FTPFileInfo;

/**  
 * 功能描述:FTPUtilTest
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2018年4月23日 下午5:21:36  
 */
public class FTPUtilTest {

	@Test
	public void test() {
		String hostName = "172.24.186.99";
		int port = 21;
		String userName = "tocc";
		String passwd = "!Tocc@2017";
		String controlEncoding = "GBK";
		FTPUtil.InitFTPClient(hostName, port, userName, passwd, controlEncoding);
		try {
			List<FTPFileInfo> listFTPFileInfo = new ArrayList<FTPFileInfo>();
			FTPUtil.GetFilesByPath("/日常文档/监测报告/日周报相关/总体运行情况/OA发布/日报",listFTPFileInfo);
			for(FTPFileInfo ftpFileTemp : listFTPFileInfo) {
				System.out.println(ftpFileTemp.getFtpPath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
