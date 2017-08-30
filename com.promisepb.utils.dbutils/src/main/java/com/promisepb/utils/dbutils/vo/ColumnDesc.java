/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.dbutils.vo;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年3月18日 下午8:19:57  
 */
public class ColumnDesc {

    private String name;
    private String comment;
    private String type;
    private String ispk;
    private String isnull;
    private String remark;
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    public ColumnDesc(String name, String comment, String type, String ispk,
            String isnull, String remark) {
        super();
        this.name = name;
        this.comment = comment;
        this.type = type;
        this.ispk = ispk;
        this.isnull = isnull;
        this.remark = remark;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }
    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    /**
     * @return the ispk
     */
    public String getIspk() {
        return ispk;
    }
    /**
     * @param ispk the ispk to set
     */
    public void setIspk(String ispk) {
        this.ispk = ispk;
    }
    /**
     * @return the isnull
     */
    public String getIsnull() {
        return isnull;
    }
    /**
     * @param isnull the isnull to set
     */
    public void setIsnull(String isnull) {
        this.isnull = isnull;
    }
    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }
    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    
    
}