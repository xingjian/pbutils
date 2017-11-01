/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.mathutils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  
 * 功能描述:PBMathUtil测试程序
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年10月28日 下午7:56:16  
 */
public class PBMathUtilTest {

	private static final Logger logger = LoggerFactory.getLogger(PBMathUtilTest.class);
	
	/**
	 * 测试与正北方向的角度
	 */
	@Test
	public void testGetVectorAngle() {
		//116.3263068 39.737349675,116.3263068 39.736839735
		double angle = PBMathUtil.getVectorAngle(116.3263068 , 39.737349675, 116.3263068 , 39.736839735);
		double d1 = 116.32630681111112222;
		double d2 = 116.32630681111111111;
		boolean boo = d1==d2;
		System.out.println(boo);
		logger.info("angle : "+angle);
	}
}
