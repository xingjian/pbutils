/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.dbutils.vo;

import java.util.List;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年3月18日 下午8:19:37  
 */
public class TableDesc {
    private String tableName;
    private String comment;
    private List<ColumnDesc> columns;
    public TableDesc(String tableName, String comment, List<ColumnDesc> columns) {
        super();
        this.tableName = tableName;
        this.comment = comment;
        this.columns = columns;
    }
    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }
    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
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
     * @return the columns
     */
    public List<ColumnDesc> getColumns() {
        return columns;
    }
    /**
     * @param columns the columns to set
     */
    public void setColumns(List<ColumnDesc> columns) {
        this.columns = columns;
    }
    
    
    
}
