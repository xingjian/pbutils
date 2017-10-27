/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.httputils;

import java.util.List;

import org.junit.Test;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年9月28日 上午10:05:31  
 */
public class PBGDUtilTest {

	/**
	 * 测试获取高德api点信息
	 */
	@Test
	public void testGetPOIObject() {
		List<String> result = PBGDUtil.GetPOIObject("六里桥", "beijing", null);
		for(String strTemp : result) {
			System.out.println(strTemp);
		}		
	}
}
