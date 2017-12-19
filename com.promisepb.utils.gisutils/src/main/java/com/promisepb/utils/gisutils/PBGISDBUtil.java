/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.gisutils;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.arcsde.ArcSDEDataStoreFactory;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.mysql.MySQLDataStoreFactory;
import org.geotools.data.oracle.OracleNGDataStoreFactory;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapeType;
import org.geotools.data.shapefile.shp.ShapefileHeader;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.feature.type.GeometryTypeImpl;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年9月4日 下午10:17:55  
 */
@SuppressWarnings("all")
public class PBGISDBUtil {

	/**
     * GetDataStoreFromPostGIS("localhost", 5432, "postgis", "postgres", "root","public");
     * @param host
     * @param port
     * @param database
     * @param userName
     * @param password
     * @param schema
     */
    public static DataStore GetDataStoreFromPostGIS(String host, String port,String database, String userName, String password,String schema) {
        DataStore pgDatastore = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PostgisNGDataStoreFactory.DBTYPE.key, "postgis");
        params.put(PostgisNGDataStoreFactory.HOST.key, host);
        params.put(PostgisNGDataStoreFactory.PORT.key, new Integer(port));
        params.put(PostgisNGDataStoreFactory.DATABASE.key, database);
        params.put(PostgisNGDataStoreFactory.SCHEMA.key, schema);
        params.put(PostgisNGDataStoreFactory.USER.key, userName);
        params.put(PostgisNGDataStoreFactory.PASSWD.key, password);
        try {
            pgDatastore = DataStoreFinder.getDataStore(params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pgDatastore;
    }
    
    public static DataStore GetDataStoreFromWFS(String url) throws Exception {
        String getCapabilities = url;
        if (url.contains("?")) {
            getCapabilities += "&request=GetCapabilities";
        } else {
            getCapabilities += "?request=GetCapabilities";
        }
        URL endPoint = new URL(getCapabilities);
        HashMap<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(WFSDataStoreFactory.URL.key, endPoint);
        WFSDataStoreFactory dsFactory = new WFSDataStoreFactory();
        DataStore ds = dsFactory.createDataStore(params);
        return ds;
    }
    
    public static DataStore GetDataStoreFromMySQL(String host, int port, String user, String passwd, String database) throws Exception {
        HashMap<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(MySQLDataStoreFactory.DBTYPE.key, "mysql");
        params.put(MySQLDataStoreFactory.HOST.key, host);
        params.put(MySQLDataStoreFactory.PORT.key, port);
        params.put(MySQLDataStoreFactory.USER.key, user);
        params.put(MySQLDataStoreFactory.PASSWD.key, passwd);
        params.put(MySQLDataStoreFactory.DATABASE.key, database);
        MySQLDataStoreFactory dsFactory = new MySQLDataStoreFactory();
        DataStore ds = dsFactory.createDataStore(params);
        return ds;
    }

    public static DataStore GetDataStoreFromArcSDE(String server, int port, String instance, String user, String passwd) throws Exception {
        HashMap<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(ArcSDEDataStoreFactory.DBTYPE_PARAM.key, "arcsde");
        params.put(ArcSDEDataStoreFactory.SERVER_PARAM.key, server);
        params.put(ArcSDEDataStoreFactory.PORT_PARAM.key, port);
        params.put(ArcSDEDataStoreFactory.INSTANCE_PARAM.key, instance);
        params.put(ArcSDEDataStoreFactory.USER_PARAM.key, user);
        params.put(ArcSDEDataStoreFactory.PASSWORD_PARAM.key, passwd);
        ArcSDEDataStoreFactory dsFactory = new ArcSDEDataStoreFactory();
        DataStore ds = dsFactory.createDataStore(params);
        return ds;
    }

    public static DataStore GetDataStoreFromOracle(String host, int port, String user, String passwd, String instance) throws Exception {
        HashMap<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("dbtype", "oracle");
        params.put("host", host);
        params.put("port", port);
        params.put("user", user);
        params.put("passwd", passwd);
        params.put("instance", instance);
        OracleNGDataStoreFactory dsFactory = new OracleNGDataStoreFactory();
        DataStore ds = dsFactory.createDataStore(params);
        return ds;
    }
    
    
    /**
     * 获取DataStore中所有的空间表
     * @param ds
     */
    public static String[] getAllLayers(DataStore ds){
        try {
            String[] typeNames=ds.getTypeNames();
            return typeNames;
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 根据shape创建表
     * 目前支持WGS84坐标 表名用小写,大写的话表名带引号,列名也是同样
     * 默认已经将列名和表名转换成小写
     * @param tableName 表名
     * @param shapePath shape路径
     * @param dataStore 
     * @param charSet 读取shape文件编码
     * @return 结果状态
     */
    public static String CreateTableSchema(String tableName,String shapePath,DataStore dataStore,String charSet) {  
        String result = "success";
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();  
        builder.setName(tableName.toLowerCase());  
        builder.setCRS(DefaultGeographicCRS.WGS84);  
        DbaseFileReader reader = null;
        try {
            reader = new DbaseFileReader(new ShpFiles(shapePath),false,Charset.forName(charSet));
            DbaseFileHeader header = reader.getHeader();
            int numFields = header.getNumFields();
            for (int i=0; i<numFields; i++) {
                String title = header.getFieldName(i);
                builder.add(title, header.getFieldClass(i));
            }
            ShpFiles sf = new ShpFiles(shapePath);    
            ShapefileReader sfr = new ShapefileReader(sf,false, false, new GeometryFactory());
            ShapefileHeader sfh = sfr.getHeader();
            builder.add("the_geom",GetClassByShapeType(sfh.getShapeType()));
            SimpleFeatureType sft = builder.buildFeatureType();
            dataStore.createSchema(sft); 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {reader.close();} catch (Exception e) {}
            }
        }
        return result;  
    }  
    
    /**
     * 根据shape创建表
     * 目前支持WGS84坐标 表名用小写,大写的话表名带引号,列名也是同样
     * 默认已经将列名和表名转换成小写
     * @param tableName 表名
     * @param shapePath shape路径
     * @param dataStore 
     * @param charSet 读取shape文件编码
     * @return 结果状态
     */
    public static String CreateTableSchema(String tableName,String shapePath,DataStore dataStore,String charSet,Class classzs,String crs) {  
        String result = "success";
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();  
        builder.setName(tableName);  
        if(null==crs||crs.trim().equals("")){
            builder.setCRS(DefaultGeographicCRS.WGS84);
        }else{
            try {
                builder.setCRS(CRS.decode(crs));
            } catch (NoSuchAuthorityCodeException e) {
                e.printStackTrace();
            } catch (FactoryException e) {
                e.printStackTrace();
            }
        }
        DbaseFileReader reader = null;
        try {
            reader = new DbaseFileReader(new ShpFiles(shapePath),false,Charset.forName(charSet));
            DbaseFileHeader header = reader.getHeader();
            int numFields = header.getNumFields();
            for (int i=0; i<numFields; i++) {
                String title = header.getFieldName(i);
                builder.add(title, header.getFieldClass(i));
            }
            if(null!=classzs){
                builder.add("the_geom",classzs);
            }
            SimpleFeatureType sft = builder.buildFeatureType();
            dataStore.createSchema(sft); 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {reader.close();} catch (Exception e) {}
            }
        }
        return result;  
    }
    
    
    /**
     * 通过ShapeType获取集合对象Class
     * @param st
     * @return Class字节码
     */
    @SuppressWarnings("all")
    public static Class GetClassByShapeType(ShapeType st){
        Class typeClass=null;
        String type = st.name;
        if(type.toLowerCase().equals("point")){  
            typeClass=Point.class; 
        }else if(type.toLowerCase().equals("pointm")){  
            typeClass=MultiPoint.class;
        }else if(type.toLowerCase().equals("arc")){  
            typeClass=LineString.class;
        }else if(type.toLowerCase().equals("arcm")){  
            typeClass=MultiLineString.class;
        }else if(type.toLowerCase().equals("polygon")){
            typeClass=Polygon.class;
        }else if(type.toLowerCase().equals("polygonm")){
            typeClass=MultiPolygon.class;  
        }
        return typeClass;
    }
    
    /**
     * 使用dataStore创建表空间 默认坐标系为WGS84
     * @param tableName 表名称
     * @param dataStore dataStore对象 请参考ConnPostGis方法获取
     * @param columns 列名
     * @return 运行状态
     */
    public static String CreateTableSchema(String tableName,DataStore dataStore,Map<String,Class> columns) {  
        String result = "success";
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();  
        builder.setName(tableName);  
        builder.setCRS(DefaultGeographicCRS.WGS84);  
        for (Map.Entry<String, Class> entry : columns.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }                             
        SimpleFeatureType sft = builder.buildFeatureType();
        try {
            dataStore.createSchema(sft);
        } catch (IOException e) {
            e.printStackTrace();
            result = "fail";
        }
        return result;  
    }
    
    /**
     * 获取指定表的矢量数据
     * @param dataStore
     * @param tableName
     * @return 集合SimpleFeature对象
     */
    public static List<SimpleFeature> GetFeaturesByTableName(DataStore dataStore,String tableName){
        List<SimpleFeature> retList = new ArrayList<SimpleFeature>();
        try {
            SimpleFeatureSource fs = dataStore.getFeatureSource(tableName);
            SimpleFeatureCollection fc = fs.getFeatures();
            SimpleFeatureIterator fi = fc.features();
            while (fi.hasNext()) {
                SimpleFeature sf = fi.next();
                retList.add(sf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            dataStore.dispose();
        }
        return retList;
    }
    
    /**
     * 获取指定空间数据范围 会有一定的误差，但是比较快
     * 这个调用的是select ST_AsText(ST_force_2d(ST_Envelope(ST_EstimatedExtent('表名','几何字段名'))))
     * @param dataStore
     * @param tableName
     * @return ReferencedEnvelope对象
     * 这个方法抛出异常，但不影响使用
     */
    public static ReferencedEnvelope GetBoundsByTableName(DataStore dataStore,String tableName){
        ReferencedEnvelope bounds = null;
        try {
            SimpleFeatureSource fs = dataStore.getFeatureSource(tableName);
            bounds =  fs.getBounds();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bounds;
    }
    
    /**
     * 返回postgis空间表的范围
     * double顺序存放 minx miny  maxx maxy
     * @param host
     * @param port
     * @param database
     * @param userName
     * @param password
     * @param schema
     * @param tableName
     * @return
     */
    public static double[] GetPostGISFeaturesRange(String host, String port, String database, String userName, String password, String schema,String tableName) {
    	double[] result = new double[4];
    	DataStore ds = PBGISDBUtil.GetDataStoreFromPostGIS(host, port, database, userName, password, schema);
		ReferencedEnvelope re = GetBoundsByTableName(ds,tableName);
		result[0] = re.getMinX();
		result[1] = re.getMinY();
		result[2] = re.getMaxX();
		result[3] = re.getMaxY();
		ds.dispose();
    	return result;
    }
    
    
   /**
    * 返回postgis空间表的范围
    * @param connection
    * @param tableName
    * @param geoColumnName
    * @return
    * @throws Exception
    */
    public static double[] GetPostGISTableBounds(Connection connection,String tableName,String geoColumnName) throws Exception {
    	double[] result = new double[4];
    	String sql = "select st_astext(ST_Envelope(st_collect("+geoColumnName+"))) from "+tableName;
    	Statement statement = connection.createStatement();
    	ResultSet rs = statement.executeQuery(sql);
    	rs.next();
    	Polygon polygon = PBGTGeometryUtil.createPolygonByWKT(rs.getString(1));
    	Envelope envelope = polygon.getEnvelopeInternal();
    	result[0] = envelope.getMinX();
		result[1] = envelope.getMinY();
		result[2] = envelope.getMaxX();
		result[3] = envelope.getMaxY();
    	rs.close();
    	statement.close();
    	return result;
    }
    
    
    /**
     *  获取空间表的空间字段信息
     * @param dataStore
     * @param tableName
     * @return
     */
    public static List<String> GetGeometryNameByTableName(DataStore dataStore,String tableName){
        List<String> retList = new ArrayList<String>();
        try {
            SimpleFeatureSource fs = dataStore.getFeatureSource(tableName);
            SimpleFeatureType ft=fs.getSchema();
            for (int i = 0; i < ft.getAttributeCount(); i++) {
                AttributeType at = ft.getType(i);
                if(at instanceof GeometryTypeImpl) {
                	//String name = at.getBinding().getName();com.vividsolutions.jts.geom.MultiLineString
                	//String simpleName = at.getBinding().getSimpleName();MultiLineString
                	retList.add(at.getName().toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            dataStore.dispose();
        }
        return retList;
    }
    
    
    /**
     * 获取指定空间表的属性信息
     * @param dataStore
     * @param tableName
     * @return 
     */
    public static List<String> GetAttributeByTableName(DataStore dataStore,String tableName){
        List<String> retList = new ArrayList<String>();
        try {
            SimpleFeatureSource fs = dataStore.getFeatureSource(tableName);
            SimpleFeatureType ft=fs.getSchema();
            for (int i = 0; i < ft.getAttributeCount(); i++) {
                AttributeType at = ft.getType(i);
                retList.add(at.getName().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            dataStore.dispose();
        }
        return retList;
    }
}
