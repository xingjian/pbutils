/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.gisutils;

import org.junit.Test;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年10月9日 下午5:35:59  
 */
public class PBGTGeometryUtilTest {

	@Test
	public void testGetDistance84() {
		//蓬莱公寓速8  116.415917,40.125613
		double x1 = 116.415917;
		double y1 = 40.125613;
		//蓬莱公寓爱国教育  116.410494,40.124776
		double x2 = 116.410494;
		double y2 = 40.124776;
		double dis = PBGTGeometryUtil.GetDistance84(x1, y1, x2, y2);
		System.out.println(dis);
		double disLPF = PBGTGeometryUtil.GetDistance84(116.3547266940,39.975205656088,116.355495569824,39.9752124119753);
		System.out.println(disLPF);
	}
}
