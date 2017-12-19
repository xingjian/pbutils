/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.httputils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**  
 * 功能描述:PBHttpUtil测试类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年12月18日 下午4:14:21  
 */
public class PBHttpUtilTest {

	@Test
	public void testDoPost() {
		String url = "http://180.150.188.224/zld/api/parkinfo/queryPark";
		Map<String, String> paramMap = new HashMap();
	    paramMap.put("local", "beijing");
	    paramMap.put("time", "1352333917");
	    paramMap.put("time", (int)(new Date().getTime() / 1000) - 300+"");
		String result = PBHttpUtil.DoPost(url, paramMap, "UTF-8");
		System.out.println(result);
	}
}
