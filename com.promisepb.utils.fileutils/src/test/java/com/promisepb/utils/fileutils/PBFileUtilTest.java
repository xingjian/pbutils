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
	
	@Test
	public void testGetFileMD5Code() {
		String filePath = "d:\\某某项目-运维周报20171002-20171008.docx";
		String md51 = PBFileUtil.GetFileMD5Code(filePath);
		String filePath2 = "d:\\某某项目-运维周报20171002-20171008 - 副本.docx";
		String md52 = PBFileUtil.GetFileMD5Code(filePath2);
		System.out.println(md51);
		System.out.println(md52);
	}
	
	@Test
	public void testIsSameFile() {
		String filePath = "d:\\某某项目-运维周报20171002-20171008.docx";
		String filePath2 = "d:\\某某项目-运维周报20171002-20171008 - 副本.docx";
		boolean boo = PBFileUtil.IsSameFile(filePath, filePath2);
		System.out.println(boo);
	}
}
