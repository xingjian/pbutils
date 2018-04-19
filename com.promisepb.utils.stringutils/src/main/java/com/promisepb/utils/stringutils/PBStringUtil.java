package com.promisepb.utils.stringutils;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**  
 * 功能描述: 字符串帮助类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年8月26日 上午10:10:38  
 */
@SuppressWarnings("all")
public class PBStringUtil {

    public static String SITETYPE_LEFT = "append_left";
    public static String SITETYPE_RIGHT = "append_right";
    public static SimpleDateFormat sdf_yMdHms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat sdf_yMd = new  SimpleDateFormat("yyyy-MM-dd" );
    /**
     * 汉字转换位汉语拼音首字母，英文字符不变
     * @param chines 汉字
     * @return 拼音
     */
    public static String ConverterToFirstSpell(String chines) {
        String pinyinName = "";
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    pinyinName += PinyinHelper.toHanyuPinyinStringArray(
                            nameChar[i], defaultFormat)[0].charAt(0);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName += nameChar[i];
            }
        }
        return pinyinName;
    }

    /**
     * 汉字转换位汉语拼音，英文字符不变
     * @param chines 汉字
     * @return 拼音
     */
    public static String ConverterToSpell(String chines) {
        String pinyinName = "";
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    pinyinName += PinyinHelper.toHanyuPinyinStringArray(
                            nameChar[i], defaultFormat)[0];
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName += nameChar[i];
            }
        }
        return pinyinName;
    }
    
    /**
     * 获取uuid方法
     * @return 唯一id字符串并且不带'-'
     */
    public static String GetUUIDString(){
        String result = UUID.randomUUID().toString().replace("-", "");
        return result;
    }
    
    /**
     * 按特定的编码格式获取长度
     * @param str 字符串
     * @param code 编码
     * @return 整型int长度
     */
    public static int GetWordCountCode(String str, String code){  
        int length = 0;
        try {
            length = str.getBytes(code).length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }  
        return length;
    }
    
    /**
     * 由于Java是基于Unicode编码的，因此，一个汉字的长度为1，而不是2。 
     * 但有时需要以字节单位获得字符串的长度。例如，“123abc长城”按字节长度计算是10，而按Unicode计算长度是8。 
     * 为了获得10，需要从头扫描根据字符的Ascii来获得具体的长度。如果是标准的字符，Ascii的范围是0至255，如果是汉字或其他全角字符，Ascii会大于255。 
     * 因此，可以编写如下的方法来获得以字节为单位的字符串长度。
     * @param s 输入字符串
     */  
    public static int GetWordCount(String s){  
        int length = 0;  
        for(int i = 0; i < s.length(); i++){  
            int ascii = Character.codePointAt(s, i);  
            if(ascii >= 0 && ascii <=255)  
                length++;  
            else  
                length += 2;  
        }  
        return length;  
          
    }  
    
    /**
     * 按字节截取字符串
     * @param orignal
     * @param bytes
     * @param encoding
     * @return 返回截取字符串
     */
    public static String SubstringByByte(String orignal, int bytes,String encoding){   
        String reStr = "";
        try {
            int count = 0;
            if (orignal == null) {
                return "";
            }
            char[] tempChar = orignal.toCharArray();
            for (int i = 0; i < tempChar.length; i++) {
                String s1 = orignal.valueOf(tempChar[i]);
                byte[] b = s1.getBytes(encoding);
                count += b.length;
                if (count <= bytes) {
                    reStr += tempChar[i];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reStr;   
    }
    
    /**
     * 判断是否是中文
     * @param c 输入字符
     * @return true是中文 false不是中文
     */
    public static boolean IsChineseChar(char c){
       return String.valueOf(c).matches("[\\u4E00-\\u9FA5]+");
    }
    
    /**
     * 随机产生字符串
     * @param length 字符串长度
     */
    public static String GetRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  
        Random random = new Random();  
        StringBuffer sb = new StringBuffer();  
        for(int i = 0 ; i < length; ++i){  
            int number = random.nextInt(62);  
            sb.append(str.charAt(number));  
        }  
        return sb.toString();  
    }

    /**
     * 随机产生字符串
     * @param baseStr 种子字符串
     * @param length   长度
     * @param position 策略方式
     * @return 生成字符串
     */
    public static String GetRandomString(String baseStr,int length,String position){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  
        Random random = new Random();  
        StringBuffer sb = new StringBuffer();
        if(position.equals(SITETYPE_LEFT)){
            sb.append(baseStr);
            for(int i = baseStr.length() ; i < length; ++i){  
                int number = random.nextInt(62);  
                sb.append(str.charAt(number));  
            } 
        }else if(position.equals(SITETYPE_RIGHT)){
            for(int i = 0 ; i < length - baseStr.length(); ++i){  
                int number = random.nextInt(62);  
                sb.append(str.charAt(number));  
            }
            sb.append(baseStr);
        }
        return sb.toString();  
    }
    
    /**
     * 获取时间字符串
     * @param formatterStr 默认yyyy-MM-dd HH:mm:ss
     * @param time 默认当前时间 可以传入Date 和 毫秒数
     * @return 字符串
     */
    public static String GetDateString(String formatterStr,Object time){
        if(null==formatterStr){
            formatterStr = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(formatterStr);
        Date date = null;
        if(null==time){
            date = new Date();
            return formatter.format(date);
        }else if(time instanceof Long){
            date = new Date((Long)time);
        }else if(time instanceof Date){
            date = (Date)time;
        }
        return formatter.format(date);
    }
    
    /**
     * 获取当前时间字符串 yyyy-MM-dd HH:mm:ss
     * @return 字符串
     */
    public static String GetCurrentDateString(){
        Date date = new Date();
        return sdf_yMdHms.format(date);
    }
    
    
    /**
     * 返回double字符串格式,建议如果double比较大的时候采用
     * @param format '0.0000' '#.0000'
     * @param d 数据
     * @return 格式化的结果
     */
    public static String FormatDoubleStr(String format,double d){
        DecimalFormat df = new DecimalFormat(format);
        return df.format(d);
    }
    
    /**
     * @param src
     * 源字符串
     * @return 字符串，将src的第一个字母转换为大写，src为空时返回null
     */
    public static String ChangeFirstUpper(String src) {
        if (src != null) {
            StringBuffer sb = new StringBuffer(src);
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            return sb.toString();
        } else {
            return null;
        }
    }
    
    /**
     * 判断传入的日期字符串是星期几
     * @param pTime 格式yyyy-MM-dd,如果为null或者空串则返回当前日期代表星期几
     * @return 1代表星期一 ,7代表星期日
     */
    public  static  int  DayForWeek(String pTime){
    	int  dayForWeek = 0 ;   
    	try {
        	 Calendar c = Calendar.getInstance(); 
			 if(null==pTime||pTime.trim().equals("")) {
				 c.setTime(new Date());
			 }else {
				 c.setTime(sdf_yMd.parse(pTime));
			 }
	    	 if (c.get(Calendar.DAY_OF_WEEK) == 1 ){  
	    		 dayForWeek = 7 ;  
	    	 }else {  
	    		 dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1 ;  
	    	 }
	    	 
		} catch (ParseException e) {
			e.printStackTrace();
		}  
    	return  dayForWeek;   
   }  
   
    /**
     * 返回字符串汉字星期几
     * @param dt ，如果为null的话，默认采取当天日期
     * @return 字符串汉字
     */
    public static String GetWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        if(null==dt) {
        	dt = new Date();
        }
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
    
    /**
     * 比较两个日期大小
     * @param date1 日期1
     * @param date2 日期2
     * @return 0 表示相等  1 (date1大于date2) -1(date1小于date2) -2表示异常
     */
    public static int CompareDate(String date1,String date2,SimpleDateFormat sdf) {
        try {
            Date dt1 = sdf.parse(date1);
            Date dt2 = sdf.parse(date2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return -2;
    }
    
    /**
     * 比较两个日期大小
     * @param date1 日期1
     * @param date2 日期2
     * @return 0 表示相等  1 (date1大于date2) -1(date1小于date2) -2表示异常
     */
    public static int CompareDate(String date1, String date2) {
        try {
            Date dt1 = sdf_yMdHms.parse(date1);
            Date dt2 = sdf_yMdHms.parse(date2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return -2;
    }
    
    
    /**
     * 判断日期是否在区间范围
     * @param time
     * @param from
     * @param to
     * @return true false
     */
    public static  boolean BelongCalendar(Date time, Date from, Date to) {
        Calendar date = Calendar.getInstance();
        date.setTime(time);
        Calendar after = Calendar.getInstance();
        after.setTime(from);

        Calendar before = Calendar.getInstance();
        before.setTime(to);

        if (date.after(after) && date.before(before)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 根据字符串返回字符串正则匹配的数字数组
     * @param str
     * @return 数组
     */
    public static String[] GetNumberArrByString(String str) {
		Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(str);
        String[] result = new String[matcher.groupCount()];
        List<String> list = new ArrayList<String>();
        int index = 0;
        while( matcher.find()) {
        	String strTemp = matcher.group();
        	list.add(index, strTemp);
        	index++;
        }
        return (String[])list.toArray(new String[index]);
    }
    
    /**
     * 日期做加减法，根据传入的日期返回计算后的结果日期。
     * @param date
     * @param days
     * @return 日期
     */
    public static Date DateCalc(Date date,int days) {
    	 Calendar c = Calendar.getInstance();  
         c.setTime(date);  
         c.add(Calendar.DAY_OF_MONTH, days);
         Date result = c.getTime();
         return result;
    }
    
    /**
     * 检查日期字符串是否标准
     * @param dateStr
     * @param sdf
     * @return
     */
    public static  boolean CheckValidDate(String dateStr,SimpleDateFormat sdf) {
    	boolean result = true;
    	try {
			sdf.parse(dateStr);
		} catch (Exception e) {
			result = false;
		}
    	return result;
    }
    
    /**
     * 获取互联网时间
     * @return 字符串
     */
    public static String GetWebTime() {
    	TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
    	String result = "";
        String webUrl2 = "http://www.baidu.com";// 百度
        String webUrl3 = "http://www.taobao.com";// 淘宝
        String webUrl4 = "http://www.ntsc.ac.cn";// 中国科学院国家授时中心
        String webUrl5 = "http://www.360.cn";// 360
        String webUrl6 = "http://www.beijing-time.org";// beijing-time
        String webUrl7 = "http://www.163.com/";// 网易
        String webUrl8 = "https://www.tmall.com/";// 天猫
        List<String> webURLList = new ArrayList<String>();
        webURLList.add(webUrl2);
        webURLList.add(webUrl3);
        webURLList.add(webUrl4);
        webURLList.add(webUrl5);
        webURLList.add(webUrl6);
        webURLList.add(webUrl7);
        webURLList.add(webUrl8);
        for(String webURLTemp : webURLList) {
        	try {
                URL url = new URL(webURLTemp);
                URLConnection conn = url.openConnection();
                conn.connect();
                long dateL = conn.getDate();
                Date date = new Date(dateL);
                result =  sdf_yMdHms.format(date);
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
