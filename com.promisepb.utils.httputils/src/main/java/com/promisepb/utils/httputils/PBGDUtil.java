/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.httputils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


/**  
 * 功能描述: 在线高德的帮助类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年9月28日 上午9:35:36  
 */
public class PBGDUtil {

	public static String GD_POI_URL = "http://restapi.amap.com/v3/place/text?s=rsv3&key=af57806649467f8471f123182e5387f1&extensions=base&types=&keywords={{value}}&city={{cityname}}&offset=100&page=1&random="+Math.random();
	
	/**
	 * @param value 关键字
	 * @param cityName 城市名称全拼音如beijing
	 * @param gd_poi_url 默认采用系统自带的url
	 * @return
	 */
	public static List<String> GetPOIObject(String value,String cityName,String gd_poi_url){
        List<String> resultList = new ArrayList<String>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String url = "";
        String returnStr = "";
        if(null!=gd_poi_url&&!gd_poi_url.trim().equals("")) {
        	url = gd_poi_url.replace("{{value}}", value).replace("{{cityname}}", cityName);
        }else {
        	url = GD_POI_URL.replace("{{value}}", value).replace("{{cityname}}", cityName);
        }
        try {
            HttpGet httpget = new HttpGet(url);
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };
            returnStr = httpclient.execute(httpget, responseHandler);
            JSONObject dataJson = new JSONObject(returnStr);
            JSONArray array = dataJson.getJSONArray("pois");  
            for(int i=0;i<array.length();i++){  
                JSONObject jsonobject = array.getJSONObject(i);  
                String nametemp = jsonobject.get("name").toString();
                String location = jsonobject.get("location").toString();
                resultList.add(nametemp+":"+location);
            }  
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }
	
	/**
	 * @param adcdCode 行政区划编码
	 * @param keyword   线路数字
	 * @return 查询结果
	 * @throws Exception
	 */
	public static void GetBusLineNameByAdcdCode(String adcdCode,String keyword) {
//        Map<String,String> map = new HashMap<String,String>();
//        List<String> list = new ArrayList<String>();
//        int index = 1;
//        if(null!=keyword) {
//        	
//        }else {
//        	
//        }
//        for(int a=1;a<1000;a++){
//            String url = "http://api.go2map.com/engine/api/businfo/json?hidden_MapTool=busex2.BusInfo&what="+a+"&city=%25u5317%25u4EAC&pageindex=1&pagesize=100&fromuser=bjbus&datasource=bjbus&clientid=9db0f8fcb62eb46c&cb=SGS.modules_businfo15cf80ad8ab17";
//            String result = PBHttpUtil.GetByString(url);
//            JSONObject jb = JSONObject.fromObject(result.substring(result.indexOf("(")+1, result.lastIndexOf(")")));
//            if(!jb.get("status").equals("ok")){continue;}
//            if(null==jb){
//                continue;
//            }
//            JSONObject json1 = JSONObject.fromObject(jb.get("response"));
//            if(null==json1){
//                continue;
//            }
//            JSONObject json2 = JSONObject.fromObject(json1.get("resultset"));
//            if(null==json2){
//                continue;
//            }
//            JSONObject json3 = JSONObject.fromObject(json2.get("data"));
//            if(null==json3){
//                continue;
//            }
//            if(null==json2.get("curresult")){
//                continue;
//            }else{
//                String curresult = json2.get("curresult").toString();
//                if(curresult.equals("1")){
//                    JSONObject json4 = JSONObject.fromObject(json3.get("feature"));
//                    String idTemp = json4.get("id")+"";
//                    String captionTemp = json4.get("caption")+"";
//                    if(null==map.get(idTemp)){
//                        map.put(idTemp, captionTemp);
//                        index++;
//                    }
//                }else{
//                    JSONArray json4 = json3.getJSONArray("feature");
//                    for(int i=0;i<json4.size();i++){
//                        JSONObject jsonTemp = JSONObject.fromObject(json4.get(i));
//                        String idTemp = jsonTemp.get("id")+"";
//                        String captionTemp = jsonTemp.get("caption")+"";
//                        if(null==map.get(idTemp)){
//                            map.put(idTemp, captionTemp);
//                            index++;
//                        }
//                    } 
//                }
//            }
//            Thread.sleep(500);
//        }
    }
}
