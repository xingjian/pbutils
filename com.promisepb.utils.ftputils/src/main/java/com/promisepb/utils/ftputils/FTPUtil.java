/**
* @Copyright@2018 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.ftputils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.utils.ftputils.vo.FTPFileInfo;

/**
 * 功能描述: FTP工具类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>
 * 
 * @version: V1.0 日期:2018年4月18日 下午3:36:29
 */
public class FTPUtil {

	public static Logger logger = LoggerFactory.getLogger(FTPUtil.class);
	public static FTPClient ftpClient = null;
	public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	/**
	 * 初始化FTPClient
	 * @param hostName 服务器名称或者IP地址
	 * @param port 端口
	 * @param userName 用户名
	 * @param passwd 密码
	 * @param controlEncoding 编码
	 * @return boolean 是否初始化成功
	 */
	public static boolean InitFTPClient(String hostName, int port, String userName, String passwd,
			String controlEncoding) {
		boolean result = false;
		ftpClient = new FTPClient();
		try {
			ftpClient.setControlEncoding(controlEncoding);
			ftpClient.connect(hostName, port); // 连接ftp服务器
			ftpClient.login(userName, passwd); // 登录ftp服务器
			ftpClient.enterLocalPassiveMode();
			int replyCode = ftpClient.getReplyCode(); // 是否成功登录服务器
			if (!FTPReply.isPositiveCompletion(replyCode)) {
				logger.debug("init ftpclient failed.");
				result = false;
			} else {
				logger.debug("init ftpclient success.(" + hostName + ")");
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void GetFilesByPath(String filePath,List<FTPFileInfo> result){
        if(null==result){
            result  = new ArrayList<FTPFileInfo>();
        }
		try {
			List<FTPFileInfo> list = ListFiles(filePath);
			for(FTPFileInfo file:list){
	            if(file.isDirectory()){
	                GetFilesByPath(filePath+File.separator+file.getName(),result);
	            }else{
	                result.add(file);
	            }     
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }
	
	/**
	 * 获取制定文件路径文件列表
	 * @param filePath 文件目录路径
	 * @return FTPFileInfo集合列表
	 * @throws IOException 异常信息
	 */
	public static List<FTPFileInfo> ListFiles(String filePath) throws IOException {
		List<FTPFileInfo> fileList = new ArrayList<FTPFileInfo>();
		FTPFile[] ftpFiles = ftpClient.listFiles(filePath);
		int size = (ftpFiles == null) ? 0 : ftpFiles.length;
		for (int i = 0; i < size; i++) {
			FTPFile ftpFile = ftpFiles[i];
			FTPFileInfo fi = new FTPFileInfo();
			fi.setName(ftpFile.getName());
			fi.setSize(ftpFile.getSize());
			fi.setTimestamp(format.format(ftpFile.getTimestamp().getTime()));
			fi.setDirectory(ftpFile.isDirectory());
			fi.setFtpPath(filePath+File.separator+ftpFile.getName());
			fileList.add(fi);
		}
		return fileList;
	}
	
	/**
	 * 退出并关闭FTPClient
	 */
	public static void CloseFTPClient() {
		try {
			if(null!=ftpClient) {
				ftpClient.logout();
				if (ftpClient.isConnected()) {
						ftpClient.disconnect();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 上传文件到指定目录
	 * @param pathname 文件目录
	 * @param fileName 文件名称
	 * @param originfilename  原始文件路径名称
	 * @return boolean 上传结果
	 */
	public static boolean UploadFile(String pathname, String fileName, String originfilename) {
		boolean flag = false;
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(originfilename));
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			CreateDirecroty(pathname);
			ftpClient.makeDirectory(pathname);
			ftpClient.changeWorkingDirectory(pathname);
			ftpClient.storeFile(fileName, inputStream);
			inputStream.close();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	/**
	 * 创建远程FTP目录
	 * @param remote 目录
	 * @return 是否创建成功
	 * @throws IOException 异常信息
	 */
	public static boolean CreateDirecroty(String remote) throws IOException {
		boolean success = true;
		String directory = remote + "/";
		// 如果远程目录不存在，则递归创建远程服务器目录
		if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(new String(directory))) {
			int start = 0;
			int end = 0;
			if (directory.startsWith("/")) {
				start = 1;
			} else {
				start = 0;
			}
			end = directory.indexOf("/", start);
			String path = "";
			String paths = "";
			while (true) {
				String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "iso-8859-1");
				path = path + "/" + subDirectory;
				if (!ExistFile(path)) {
					if (MakeDirectory(subDirectory)) {
						changeWorkingDirectory(subDirectory);
					} else {
						logger.debug("创建目录[" + subDirectory + "]失败");
						changeWorkingDirectory(subDirectory);
					}
				} else {
					changeWorkingDirectory(subDirectory);
				}
				paths = paths + "/" + subDirectory;
				start = end + 1;
				end = directory.indexOf("/", start);
				// 检查所有目录是否创建完毕
				if (end <= start) {
					break;
				}
			}
		}
		return success;
	}

	/**
	 * 改变FTPClient目录路径
	 * @param directory 目录
	 * @return boolean 结果状态
	 */
	public static boolean changeWorkingDirectory(String directory) {
		boolean flag = true;
		try {
			flag = ftpClient.changeWorkingDirectory(directory);
			if (flag) {
				logger.debug("进入文件夹" + directory + " 成功！");
			} else {
				logger.debug("进入文件夹" + directory + " 失败！开始创建文件夹");
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return flag;
	}

	/**
	 *  判断ftp服务器文件是否存在
	 * @param path 目录
	 * @return boolean 结果状态
	 * @throws IOException 异常信息
	 */
	public static boolean ExistFile(String path) throws IOException {
		boolean flag = false;
		FTPFile[] ftpFileArr = ftpClient.listFiles(path);
		if (ftpFileArr.length > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 创建目录
	 * @param dir 目录信息
	 * @return boolan 结果状态
	 */
	public static boolean MakeDirectory(String dir) {
		boolean flag = true;
		try {
			flag = ftpClient.makeDirectory(dir);
			if (flag) {
				logger.debug("创建文件夹" + dir + " 成功！");
			} else {
				logger.debug("创建文件夹" + dir + " 失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 下载指定文件到制定目录中
	 * @param pathname FTP文件目录
	 * @param filename FTP文件名
	 * @param localpath  本地目录
	 * @return boolean 结果状态
	 */
	 public static  boolean DownloadFile(String pathname, String filename, String localpath){ 
         boolean flag = false; 
         OutputStream os=null;
         try { 
             logger.debug("开始下载文件");
             //切换FTP目录 
             ftpClient.changeWorkingDirectory(pathname); 
             FTPFile[] ftpFiles = ftpClient.listFiles(); 
             for(FTPFile file : ftpFiles){ 
                 if(filename.equalsIgnoreCase(file.getName())){ 
                     File localFile = new File(localpath + "/" + file.getName()); 
                     os = new FileOutputStream(localFile); 
                     ftpClient.retrieveFile(file.getName(), os); 
                     os.close(); 
                 } 
             } 
             flag = true; 
             logger.debug("下载文件成功");
         } catch (Exception e) { 
        	 logger.debug("下载文件失败");
             e.printStackTrace(); 
         } finally{ 
             if(null != os){
                 try {
                     os.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 } 
             } 
         } 
         return flag; 
     }
     
     /** 
      * 删除指定目录下文件 
     * @param pathname FTP服务器保存目录 
     * @param filename 要删除的文件名称 
     * @return  boolean  结果状态
     * */ 
     public static boolean DeleteFile(String pathname, String filename){ 
         boolean flag = false; 
         try { 
             logger.debug("开始删除文件......");
             //切换FTP目录 
             ftpClient.changeWorkingDirectory(pathname); 
             ftpClient.dele(filename); 
             flag = true; 
             logger.debug("删除文件成功");
         } catch (Exception e) { 
        	 logger.debug("删除文件失败");
             e.printStackTrace(); 
         } 
         return flag; 
     }
}
