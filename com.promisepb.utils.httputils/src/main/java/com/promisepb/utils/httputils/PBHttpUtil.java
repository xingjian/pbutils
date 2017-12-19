package com.promisepb.utils.httputils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**  
 * 功能描述: 小爬虫工具
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年6月13日 下午12:59:13  
 */
public class PBHttpUtil {

    /**
     * 通过指定的路径抓取数据
     * @param url
     * @throws Exception
     */
    public final static String GetByString(String url) throws Exception {
        String returnStr = "";
        CloseableHttpClient httpclient = HttpClients.createDefault();
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
        } finally {
            httpclient.close();
        }
        return returnStr;
    }
    
    /**
     * 通过url获取图片
     * @param filePath 文件夹路径
     * @param fileName 文件名称
     * @param uri
     * @return 结果状态
     */
    public static String GetImageByURI(String filePath,String fileName,String uri) throws Exception{
        String result = "success";
        File f = new File(filePath);
        if(!f.exists()){
            f.mkdirs();
        }
        File f1 = new File(filePath+"//"+fileName);
        FileOutputStream fos = new FileOutputStream(f1);
        BufferedOutputStream fbo = new BufferedOutputStream(fos);
        URL url = new URL(uri);
        URLConnection urlc = url.openConnection();
        urlc.setConnectTimeout(3000);
        urlc.setReadTimeout(10000);
        byte[] data = new byte[1024];
        BufferedInputStream input = new BufferedInputStream(urlc.getInputStream());
        int c = -1;
        while ((c=input.read(data))!=-1){
            fbo.write(data,0,c);
        }
        fbo.flush();
        fbo.close();
        input.close();
        return result;
    }
    
    public static String DoPost(String url,Map<String,String> map,String charset){  
        HttpClient httpClient = null;  
        HttpPost httpPost = null;  
        String result = null;  
        try{  
        	httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(url);  
            //设置参数  
            List<NameValuePair> list = new ArrayList<NameValuePair>();  
            Iterator iterator = map.entrySet().iterator();  
            while(iterator.hasNext()){  
                Entry<String,String> elem = (Entry<String, String>) iterator.next();  
                list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));  
            }  
            if(list.size() > 0){  
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);  
                httpPost.setEntity(entity);  
            }  
            HttpResponse response = httpClient.execute(httpPost);  
            if(response != null){  
                HttpEntity resEntity = response.getEntity();  
                if(resEntity != null){  
                    result = EntityUtils.toString(resEntity,charset);  
                }  
            }  
        }catch(Exception ex){  
            ex.printStackTrace();  
        }  
        return result;  
    }  
    
}
