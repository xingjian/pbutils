/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.pathfind;

import org.junit.Test;

/**  
 * 功能描述:PBTopyUtil测试用例
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年11月24日 上午10:25:05  
 */
public class PBTopyUtilTest {

	@Test
	public void testCreateSWTopyFile() {
		String shpFilePath = "G:\\项目文档\\公交都市\\工大\\林鹏飞\\北京导航图\\北京导航图\\all_road.shp";
		int  gridSize = 200;
		String topyFilePath = "F:\\toccworkspace\\pathfind\\bj_topy.csv";
		String shpEncoding = "GBK";
		try {
			PBTopyUtil.newInstance().createSWTopyFile(shpFilePath, gridSize, shpEncoding, topyFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
