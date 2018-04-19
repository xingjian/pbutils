/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.stringutils;

import org.junit.Test;

/**  
 * 功能描述:StringUtil 测试用例
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年8月25日 下午5:07:31  
 */
public class StringUtilTest {
	
	@Test
	public void testGetCurrentDateString() {
		System.out.println(PBStringUtil.GetCurrentDateString());
	}
	
	@Test
	public void testDayForWeek() {
		System.out.println(PBStringUtil.DayForWeek("2017-08-26"));
		System.out.println(PBStringUtil.DayForWeek(""));
	}
	
	@Test
	public void testGetWebTime() {
		System.out.println(PBStringUtil.GetWebTime());
	}
}
