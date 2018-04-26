/**
* @Copyright@2018 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.ftputils.vo;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2018年4月19日 上午11:29:13  
 */
public class FTPFileInfo {

		public String name;
		public Long size;
		public String timestamp;
		public boolean isDirectory;
		public String ftpPath;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Long getSize() {
			return size;
		}
		public void setSize(Long size) {
			this.size = size;
		}
		public String getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(String timestamp) {
			this.timestamp = timestamp;
		}
		public boolean isDirectory() {
			return isDirectory;
		}
		public void setDirectory(boolean isDirectory) {
			this.isDirectory = isDirectory;
		}
		public String getFtpPath() {
			return ftpPath;
		}
		public void setFtpPath(String ftpPath) {
			this.ftpPath = ftpPath;
		}
		
		
}
