package com.promisepb.utils.poiutils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;

import com.promisepb.utils.stringutils.PBStringUtil;

/**  
 * 功能描述: poiexcel帮助类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2015年8月27日 下午1:01:52  
 */
@SuppressWarnings("all")
public class PBPOIExcelUtil {
    
    /**
     * 读取excel文件,将每行数据按照分隔符拼接成字符组存放到集合当中
     * 目前支持2003版本
     * @param separator 分隔符
     * @param filePath 文件路径
     * @return 字符串集合
     */
    public static List<String> ReadXLS(String filePath,String separator,int rowSNum,int rowENum,int columnSNum,int columnENum){
        List<String> retList = new ArrayList<String>();
        try{
            File file = new File(filePath);
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            POIFSFileSystem fs = new POIFSFileSystem(in);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            for(int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++){
                HSSFSheet st = wb.getSheetAt(sheetIndex);
                for (int rowIndex = rowSNum-1; rowIndex < rowENum; rowIndex++) {
                   HSSFRow row = st.getRow(rowIndex);
                   if (row == null) {
                       continue;
                   }
                   String rowStr="";
                   for(int columnIndex=columnSNum-1;columnIndex<columnENum;columnIndex++){
                       Cell cell = row.getCell(columnIndex);
                       String cellValue = GetCellValue(cell);
                       if(cellValue.trim().equals("")){
                           cellValue="0";
                       }
                       if(columnIndex==columnENum-1){
                           rowStr +=cellValue;
                       }else{
                           rowStr +=cellValue+separator;    
                       }
                       
                   }
                   retList.add(rowStr);
                }
            }
            wb.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return retList;
    }
    
    /**
     * 读取excel指定行，并转换拼音
     * 为了创建表用
     * @param filePath 路径
     * @param row 指定行
     * @param column 列数
     * @return
     */
    public static List<String> GetExcelRowPinYin(String filePath,int sheetIndex,int rowIndex){
        List<String> retList = new ArrayList<String>();
        try{
            File file = new File(filePath);
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            POIFSFileSystem fs = new POIFSFileSystem(in);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet st = wb.getSheetAt(sheetIndex);
            HSSFRow row = st.getRow(rowIndex);
            if (row == null) {
                wb.close();
                return retList;
            }
            Iterator<Cell> iter = row.cellIterator();
            while(iter.hasNext()){
                Cell cell = iter.next();
                String cellValue = GetCellValue(cell);
                retList.add(PBStringUtil.ConverterToSpell(cellValue));
            }
            wb.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return retList;
    }
    
    /**
     * 根据集合导出excel
     * 标准的domain写法
     * @param list
     * @param excelPath
     * @return 导出结果
     */
    public static <T> String ExportDataByList(List<T> list, String excelPath) {
        String result = "success";
        try {
            Field[] fds = null;
            Class clazz = null;
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet();
            int iRow=1;
            //遍历集合
            for (Object object : list) {
                //获取集合中的对象类型
                if (null == clazz) {
                    clazz = object.getClass();
                    workbook.setSheetName(0,clazz.getName());
                    //获取他的字段数组
                    fds = clazz.getDeclaredFields();
                    HSSFRow row= sheet.createRow(0); 
                    HSSFCell cell;
                    for(int i=0;i<fds.length;i++){  
                        cell = row.createCell(i);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);  
                        cell.setCellValue(fds[i].getName());  
                    }
                }
               //遍历该数组
               HSSFRow row = sheet.createRow(iRow);
               for(int i=0;i<fds.length;i++){
                   HSSFCell cell = row.createCell(i);
                   cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                   //得到字段名
                   String fdname = fds[i].getName();
                   //根据字段名找到对应的get方法，null表示无参数
                   Method metd = clazz.getMethod("get"+PBStringUtil.ChangeFirstUpper(fdname), new  Class[0]);
                   if (null != metd) {
                       //调用该字段的get方法
                       Object name = metd.invoke(object, new  Object[]{});
                       cell.setCellValue(name.toString());
                   }else{
                       cell.setCellValue("no method");
                   }
               } 
               iRow++;
            }
            FileOutputStream fOut = new FileOutputStream(excelPath);
            workbook.write(fOut);
            fOut.flush();
            fOut.close();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            result = "fail";
        }
        return result;
    }
    
    /**
     * 通过sql导出数据到excel 2007版本
     * 根据之前的方法现在支持2007的版本
     * ExportDataBySQLNew基础之上增加了每个sheet页最大row,默认是1000000
     * @param sql
     * @param conect
     * @return
     */
    public static String ExportDataBySQL(String sql,Connection connect,String excelPath,int sheetMaxRow){
        if(sheetMaxRow==0){
            sheetMaxRow = 1000000;
        }
        String result = "success";
        try{
            Statement statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int nColumn = rsmd.getColumnCount();
            String[] columnStrArr = new String[nColumn];
            for(int i=0;i<columnStrArr.length;i++){
                columnStrArr[i] = rsmd.getColumnLabel(i+1);
            }
            int index=0;
            int sheetIndex=0;
            int pageIndex=1;
            //解决数据量大访问慢的问题
            Workbook workBook = new SXSSFWorkbook();
            Sheet sheet = null;
            Row row = null;
            Cell cell = null;
            while(resultSet.next()){
                //初始化表头
                if(index%sheetMaxRow==0){
                    sheet = workBook.createSheet();  
                    workBook.setSheetName(sheetIndex,"export"+sheetIndex);  
                    row= sheet.createRow(0); 
                    for(int i=0;i<columnStrArr.length;i++){  
                        cell = row.createCell(i);
                        cell.setCellType(XSSFCell.CELL_TYPE_STRING);  
                        cell.setCellValue(columnStrArr[i]);  
                    }
                    pageIndex=1;
                    sheetIndex++;
                }
                row= sheet.createRow(pageIndex);
                for(int j=1;j<=nColumn;j++){
                    cell = row.createCell(j-1);
                    cell.setCellType(XSSFCell.CELL_TYPE_STRING);
                    if(null==resultSet.getObject(j)){
                        cell.setCellValue("");
                    }else{
                        cell.setCellValue(resultSet.getObject(j).toString());
                    }
                }
                pageIndex++;
                index++;
            }
            
          FileOutputStream fOut = new FileOutputStream(excelPath);
          workBook.write(fOut);
          fOut.flush();
          fOut.close();
          resultSet.close();
          statement.close();
          workBook.close();
        }catch(Exception e){
            result = "error";
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 导入csv文件到指定数据库
     * @param csvPath csv文件路径
     * @param connection 数据库连接
     * @param createTableSQL 创建表语句
     * @param prepaSQL 执行插入sql语句
     * @param batchMaxValue 每天提交最大数量
     * @param charSet 读取csv文件的编码
     * @param rowSNum 从第几行开始
     * @param rowENum 到第几行结束
     * @param columnSNum 从第几列开始
     * @param columnENum 到第几列结束
     * @return
     */
    public static boolean ImportCSVToDataBase(String csvPath,Connection connection,String createTableSQL,String prepaSQL,int batchMaxValue,String charSet,int rowSNum,int rowENum,int columnSNum,int columnENum){
        boolean result = false;
        
        //connection.createStatement().execute(createTableSQL);
        return result;
    }
    
    /**
     * 读取Cell内容
     * @param cell
     * @return cell内容
     */
    public static String GetCellValue(Cell cell){
        if(null==cell){
            return "NULL";
        }
        switch (cell.getCellType()){
            case Cell.CELL_TYPE_BLANK:
                return "";
            case Cell.CELL_TYPE_BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case Cell.CELL_TYPE_ERROR:
                break;
            case Cell.CELL_TYPE_FORMULA:
                return cell.getRichStringCellValue().getString();
            case Cell.CELL_TYPE_NUMERIC:
                return NumberToTextConverter.toText(cell.getNumericCellValue());
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            default :
                return cell.getStringCellValue();
        }
        return cell.getStringCellValue();
    }
}
