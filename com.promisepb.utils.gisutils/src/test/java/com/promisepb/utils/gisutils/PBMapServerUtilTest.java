/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.gisutils;

import java.util.List;
import java.util.Map;

import org.junit.Test;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年12月21日 下午4:12:38  
 */
public class PBMapServerUtilTest {

	/**
     * 测试下周指定范围的高德地图和Dark_All
     */
    @Test
    public void testGetGDTileByExtent(){
        //郑州空间范围：  左上-（113.098243,35.04068），右下-（114.279273,34.499202） 
        double[] dArr1 = PBGISCoorTransformUtil.From84To900913(113.098243,34.499202);
        double[] dArr2 = PBGISCoorTransformUtil.From84To900913(114.279273,35.04068);
        double minX = dArr1[0]; 
        double minY = dArr1[1];
        double maxX = dArr2[0]; 
        double maxY = dArr2[1]; 
        String filePath = "E://tempworkspace//dark_all"; 
        int zLevel = 15; 
        Map<String,List<String>> list = PBMapServerUtil.GetDarkAllTileByIndexXY( minX, minY, maxX, maxY, filePath, zLevel, "png");
        System.out.println(list.size());
    }
}
