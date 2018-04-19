/**
* @Copyright@2018 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.fileutils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2018年4月16日 上午10:44:24  
 */
public class PBFileUtilTest {

	@Test
	public void testGetFilesByPath() {
		List<File> listFile = new ArrayList<File>();
		String filePath = "E:\\tempworkspace\\busgps";
		PBFileUtil.GetFilesByPath(filePath, listFile);
		for(File f : listFile) {
			System.out.println(f.getAbsolutePath());
		}
	}
}
