package com.promisepb.utils.fileutils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**  
 * 功能描述: 文件帮助类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年8月27日 下午1:04:52  
 */
public class PBFileUtil {

	
	 /**
     * 读取文件按行读取去除两边空格之后,将每行加入到集合list当中
     * 目前支持txt文档
     * @param filePath 文件路径
     * @param charSet 读取文件编码
     * @return 字符串集合
     */
	public static List<String> ReadFileByLine(String filePath,String charSet){
        List<String> list = new ArrayList<String>();
        try {
            File file = new File(filePath);
            if(null==charSet) {
            	charSet = "UTF-8";
            }
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), charSet);
        	BufferedReader br = new BufferedReader(isr);
            String s = null;
            while((s = br.readLine())!=null){
                if(s.trim()!=""){
                    list.add(s);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * 读取文件按行读取去除两边空格之后,将每行加入到集合list当中
     * 目前支持txt文档
     * @param filePath 文件路径
     * @return 文件集合
     */
	@Deprecated
    public static List<String> ReadFileByLine(String filePath){
        List<String> list = new ArrayList<String>();
        try {
            File file = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = null;
            while((s = br.readLine())!=null){
                if(s.trim()!=""){
                    list.add(s);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * 读取文件按行读取去除两边空格之后,将每行加入到集合list当中
     * 目前支持txt文档
     * @param filePath 文件路径
     * @return 文件集合
     */
    public static String ReadFile(String filePath){
        StringBuffer result = new StringBuffer();
        try {
            File file = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = null;
            while((s = br.readLine())!=null){
                result.append(s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }
    
    /**
     * 将字符串写入文本文件
     * @param content 字符串内容
     * @param filePath 文件路径
     * @return
     */
    public static String WriteStringToTxt(String content,String filePath){
        String result = "";
        //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件  
        FileWriter writer;
        try {
            writer = new FileWriter(filePath, true);
            writer.write(content);  
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }  
        return result;
    }
    
    /**
     * 写字符串信息到指定文件
     * @param content
     * @param filePath
     */
    public static String WriteStringToFile(String content,String filePath){
        try {
             File resultFile = new File(filePath);
             if(!resultFile.exists()){
                 resultFile.createNewFile();
             }
             DataOutputStream out = new DataOutputStream(new FileOutputStream(resultFile));
             out.writeBytes(content);
             out.flush();
             out.close();
           }catch (IOException e) {
                 e.printStackTrace();
        }
        return "success";
    }
    
    /**
     * 将字符串写入文本文件
     * @param content 字符串内容
     * @param filePath 文件路径
     * @param appendEnter 是否追加回车换行
     * @return
     */
    public static String WriteListToTxt(List<String> list,String filePath,boolean appendEnter){
        String result = "";
        //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件  
        FileWriter writer;
        try {
            writer = new FileWriter(filePath, true);
            for(String content:list){
                if(appendEnter){
                    writer.write(content+"\n");
                }else{
                    writer.write(content);
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }  
        return result;
    }
    
    /**
     * 将指定Properties文件转换到Map<String,String>
     * @param filePath 文件路径
     * @return Map<String,String>
     */
    public static Map<String,String> ReadPropertiesFile(String filePath){
        Map<String,String> result = new HashMap<String,String>();
        Properties prop = new Properties();
        try{
            InputStream in = new BufferedInputStream (new FileInputStream(filePath));
            //解决读取中文问题
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            prop.load(bf);
            Iterator<String> it=prop.stringPropertyNames().iterator();
            while(it.hasNext()){
                String key=it.next();
                result.put(key,prop.getProperty(key));
            }
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 读取csv文件，返回List<String>集合
     * @param filePath csv文件路径
     * @param encoding 读取文件的编码
     * @return
     */
    public static List<String> ReadCSVFile(String filePath,String encoding){
        List<String> result = new ArrayList<String>();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(fis,encoding);
            BufferedReader br = new BufferedReader(isr);
            String s = null;
            while((s = br.readLine())!=null){
                if(s.trim()!=""){
                    result.add(s);
                }
            }
            br.close();
            isr.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
   
   
    /**
     * 查询指定目录下以endName结尾的文件
     * @param findPath
     * @param endName
     * @return 返回结果,每个结果为文件的完整路径
     */
    public static List<String> FindFilesByEndName(String findPath,String endName){
       List<String> result = new ArrayList<String>();
       File file = new File(findPath);
       if(file.isFile()){
           String fileNameTemp = file.getName();
           if(fileNameTemp.endsWith(endName)){
               result.add(file.getAbsolutePath());
           }
           return result;
       }else{
           File[] fileArray= file.listFiles();
           if(null==fileArray){
             return result;
           }
           for (int i = 0; i < fileArray.length; i++) {// 如果是个目录
                if (fileArray[i].isDirectory()) {
                    //递归调用
                    List<String> resultTemp = FindFilesByEndName(fileArray[i].getAbsolutePath(), endName);
                    result.addAll(resultTemp);
                    //如果是文件
                } else if (fileArray[i].isFile()) {
                    //如果是以endName结尾的文件
                    if (fileArray[i].getName().endsWith(endName)) {
                        //保存文件完整路径
                        result.add(fileArray[i].getAbsolutePath());
                    }
                }
           }
       }
       return result;
    } 
    
    /**
     * 通过sql导出数据到txt
     * @param sql
     * @param conect
     * @param splitStr
     * @param appendEnter
     * @return
     */
    public static String ExportDataBySQL(String sql,Connection connect,String txtPath,String splitStr,boolean appendEnter){
        String result = "success";
        try{
            Statement statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int nColumn = rsmd.getColumnCount();
            FileWriter writer;
            try {
                writer = new FileWriter(txtPath, true);
                while(resultSet.next()){
                    String content = "";
                    for(int i=1;i<=nColumn;i++){
                        if(i==nColumn){
                            content = content + resultSet.getString(i);
                        }else{
                            content = content + resultSet.getString(i)+splitStr;
                        }
                    }
                    if(appendEnter){
                        writer.write(content+"\r\n");
                    }else{
                        writer.write(content);
                    }
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }  
          resultSet.close();
          statement.close();
        }catch(Exception e){
            result = "error";
            e.printStackTrace();
        }
        return result;
    }
    
    
    /**
     * 关闭流
     * @param closeables
     */
    public static void CloseQuietly(Closeable... closeables) {
        if(closeables != null) {
            for(Closeable closeable : closeables) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 解压zip文件到指定的目录
     * 默认和当前zip同级
     * @param zipFilePath
     * @param exportPath
     * @return
     */
    public static String ExtractZipFiles(String zipFilePath,String exportPath) {
        String ret = "success";
        try {
            if(null==exportPath||exportPath.trim().equals("")){
                exportPath = zipFilePath.substring(0, zipFilePath.lastIndexOf(".zip"))+File.separator;
            }else{
                if(!exportPath.endsWith(File.separator) ){
                    exportPath+=File.separator;
                }
                exportPath += zipFilePath.substring(zipFilePath.lastIndexOf(File.separator), zipFilePath.lastIndexOf(".zip"));
                exportPath+=File.separator;
            }
            
            int size = 2048;
            byte[] buf = new byte[size];
            ZipInputStream zipinputstream = null;
            ZipEntry zipentry;
            zipinputstream = new ZipInputStream(new FileInputStream(zipFilePath));
            zipentry = zipinputstream.getNextEntry();
            while (zipentry != null) {
                String entryName = zipentry.getName();
                int n;
                FileOutputStream fileoutputstream;
                File newFile = new File(entryName);
                String directory = newFile.getParent();
                if (directory == null) {
                    if (newFile.isDirectory()) {
                        break;
                    }
                }else{
                    new File(exportPath+directory).mkdirs();
                }
                if (!zipentry.isDirectory()) {
                    fileoutputstream = new FileOutputStream(exportPath+entryName);
                    while ((n = zipinputstream.read(buf, 0, size)) > -1) {
                        fileoutputstream.write(buf, 0, n);
                    }
                    fileoutputstream.close();
                }
 
                zipinputstream.closeEntry();
                zipentry = zipinputstream.getNextEntry();
            }
            zipinputstream.close();
        } catch (Exception e) {
            ret = "error";
            e.printStackTrace();
        }
        return ret;
        
    }
    
    /**
     * 获取指定目录下面所有的文件
     * @param filePath
     * @return
     */
    public static void GetFilesByPath(String filePath,List<File> result){
        File root = new File(filePath);
        File[] files = root.listFiles();
        if(null==result){
            result  = new ArrayList<File>();
        }
        for(File file:files){     
            if(file.isDirectory()){
                GetFilesByPath(file.getAbsolutePath(),result);
            }else{
                result.add(file);
            }     
        }
    }
    
    /**
     * 获取系统的默认编码
     * @return
     */
    public static String GetLocalEncoding() {  
        String encoding = System.getProperty("file.encoding");  
        return encoding;  
    }
    
    /**
     * 在某个路径下创建一个文件
     * @param filePath
     */
    public static void CreateFile(String filePath){  
        File f = new File(filePath);  
        try{  
            if (!f.exists()){
                f.createNewFile();  
            }  
        }catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
    
    /**
     * 创建一个文件夹
     * @param filePath
     */
    public static void CreateDir(String filePath){  
        File f = new File(filePath);  
        try {  
            if (!f.exists()) {  
                f.mkdirs();  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    } 
    
    /**
     * 拷贝文件
     * @param srcFile
     * @param desFile
     * @return
     */
    public static boolean copyToFile(String srcFile, String desFile)  {  
        File scrfile = new File(srcFile);  
        if (scrfile.isFile() == true)  {  
            int length;  
            FileInputStream fis = null;  
            try {  
                fis = new FileInputStream(scrfile);  
            }catch (FileNotFoundException ex)  {  
                ex.printStackTrace();  
            }  
            File desfile = new File(desFile);  
            FileOutputStream fos = null;  
            try{  
                fos = new FileOutputStream(desfile, false);  
            } catch (FileNotFoundException ex){  
                ex.printStackTrace();  
            }  
            desfile = null;  
            length = (int)scrfile.length();  
            byte[] b = new byte[length];  
            try{  
                fis.read(b);  
                fis.close();  
                fos.write(b);  
                fos.close();  
            }catch (IOException e){  
                e.printStackTrace();  
            }  
        }else{  
            scrfile = null;  
            return false;  
        }  
        scrfile = null;  
        return true;  
    }  
    
    /**
     * 拷贝文件夹
     * @param sourceDir
     * @param destDir
     * @return
     */
    public static boolean copyDir(String sourceDir, String destDir){  
        File sourceFile = new File(sourceDir);  
        String tempSource;  
        String tempDest;  
        String fileName;  
        File[] files = sourceFile.listFiles();  
        for (int i = 0; i < files.length; i++)  {  
            fileName = files[i].getName();  
            tempSource = sourceDir + File.separator + fileName;  
            tempDest = destDir + File.separator + fileName;  
            if (files[i].isFile()){  
                copyToFile(tempSource, tempDest);  
            }else{  
                copyDir(tempSource, tempDest);  
            }  
        }  
        sourceFile = null;  
        return true;  
    }  
    
    /**
     * 格式化文件大小字符串
     * @param fileS
     * @return
     */
    public static String FormatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }
    
    /**
     * 某个路径下如果存在文件，则删除
     * @param filePath
     */
    public static void deleteFile(String filePath){  
        File f = new File(filePath);  
        try {  
            if (f.exists()) {  
                f.delete();  
            }  
        }catch (Exception e){  
            e.printStackTrace();  
        }  
    }
    
    /**
     * 
     * 删除文件夹
     * @param strDir
     * @return 成功true;否则false
     */
    public static boolean removeDir(String strDir){  
        File rmDir = new File(strDir);  
        if (rmDir.isDirectory() && rmDir.exists()){  
            String[] fileList = rmDir.list();  
            for (int i = 0; i < fileList.length; i++){  
                String subFile = strDir + File.separator + fileList[i];  
                File tmp = new File(subFile);  
                if (tmp.isFile())  
                    tmp.delete();  
                else if (tmp.isDirectory())  
                    removeDir(subFile);  
            }  
            rmDir.delete();  
        }else{  
            return false;  
        }  
        return true;  
    }
    
    /**
     * 将遍历得到的文件夹及子文件夹中的全部目录去除前面全部,仅保留文件名
     * @param pathandname
     * @return
     */
    public static String GetFileName(String pathandname) {  
        /** 
        * 仅保留文件名不保留后缀 
        */  
        int start = pathandname.lastIndexOf(File.separator);  
        int end = pathandname.lastIndexOf(".");  
        if (start != -1 && end != -1) {  
            return pathandname.substring(start + 1, end);  
        } else {  
            return null;  
        }         
    }
    
    /** 
     * 将遍历得到的文件夹及子文件夹中的全部目录去除前面全部,保留文件名及后缀 
     */  
    public static String GetFileNameWithSuffix(String pathandname) {
        int start = pathandname.lastIndexOf("/");  
        if (start != -1 ) {  
            return pathandname.substring(start + 1);  
        } else {  
            return null;  
        }         
    }
    
    public static void InputstreamToFile(InputStream ins,File file){
    	try {
    		OutputStream os = new FileOutputStream(file);
        	int bytesRead = 0;
        	byte[] buffer = new byte[8192];
        	while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
        	os.write(buffer, 0, bytesRead);
        	}
        	os.close();
        	ins.close();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    	
}
