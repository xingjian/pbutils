/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.gisutils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.graph.util.geom.GeometryUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.opengis.feature.simple.SimpleFeature;

import com.promisepb.utils.gisdata.MapGrid;
import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequenceFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.linemerge.LineMerger;

/**  
 * 功能描述: geotools geometry 帮助类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年8月26日 下午9:00:30  
 */
@SuppressWarnings("all")
public class PBGTGeometryUtil {

	public static GeometryFactory gf = JTSFactoryFinder.getGeometryFactory();
    public static JSONParser jsonparser = new JSONParser();
    /**
     * create Coordinate
     * @param x
     * @param y
     * @return
     */
    public static Coordinate coordinate(double x,double y){  
        return new Coordinate(x,y);  
    }
    
    /**
     * 构造点对象 109.013388, 32.715519
     * @param x
     * @param y
     * @return
     */
    public static Point createPoint(double x,double y){  
            Coordinate coord = new Coordinate(x, y);  
            Point point = gf.createPoint( coord );  
            return point;  
    }
    
    /**
     * POINT (109.013388 32.715519)
     * @param wktString
     * @return
     * @throws ParseException
     */
    public static Point createPointByWKT(String wktString) throws ParseException{  
            WKTReader reader = new WKTReader( gf );  
            Point point = (Point) reader.read(wktString);  
            return point;  
    }
    
    /**
     * MULTIPOINT(109.013388 32.715519,119.32488 31.435678)
     * @param wktString
     * @return
     * @throws ParseException
     */
    public static MultiPoint createMulPointByWKT(String wktString) throws ParseException{  
        WKTReader reader = new WKTReader( gf );  
        MultiPoint mpoint = (MultiPoint) reader.read(wktString);  
        return mpoint;  
    }
    /**
     * 创建LineString
     * @param list
     * @xysplit 分隔符
     * item string 
     * @type 0 LineString 1 MultiLineString
     */
    public static Geometry createGeometryLine(List<String> list,String xysplit,String type){
        Coordinate[] coords = new Coordinate[list.size()];
        for(int i=0;i<list.size();i++){
            String[] str = list.get(i).split(xysplit);
            coords[i] = coordinate(Double.parseDouble(str[0]),Double.parseDouble(str[1]));
        }
        LineString ls = createLine(coords);
        if(type.trim().equals("1")){
            LineString[] lineStrings = new LineString[1];
            lineStrings[0] = ls;
            return createMLine(lineStrings);
        }else{
            return ls;
        }
    }
    
    /**
     * createLine
     * @param coords
     * @return
     */
    public static LineString createLine(Coordinate[] coords){  
        LineString line = gf.createLineString(coords);  
        return line;  
    }
    
    /**
     * createPolygon
     * @param coords
     * @return
     */
    public static Polygon createPolygon(Coordinate[] coords){  
        Polygon polygon = gf.createPolygon(coords);  
        return polygon;  
    }
    
    /**
     * LINESTRING(0 0, 2 0)
     * @param wktString
     * @return
     * @throws ParseException
     */
    public static LineString createLineByWKT(String wktString) throws ParseException{  
        WKTReader reader = new WKTReader( gf );  
        LineString line = (LineString) reader.read(wktString);  
        return line;  
    }  
    
    /**
     *  mline
     * @param lineStrings
     * @return
     */
    public static MultiLineString createMLine(LineString[] lineStrings){  
        MultiLineString ms = gf.createMultiLineString(lineStrings);  
        return ms;  
    }
    
    /**
     * MULTILINESTRING((0 0, 2 0),(1 1,2 2))
     * @return
     * @throws ParseException
     */
    public static MultiLineString createMLineByWKT(String wktString)throws ParseException{  
        WKTReader reader = new WKTReader( gf );  
        MultiLineString line = (MultiLineString) reader.read(wktString);
        return line;  
    }
    
    /**
     * POLYGON((20 10, 30 0, 40 10, 30 20, 20 10))
     * @param wktString
     * @return
     * @throws ParseException
     */
    public static Polygon createPolygonByWKT(String wktString) throws ParseException{  
        WKTReader reader = new WKTReader( gf );  
        Polygon polygon = (Polygon) reader.read(wktString);  
        return polygon;  
    }

    /**
     * 根据wkt创建几何对象
     * @param wktString
     * @return
     * @throws ParseException
     */
    public static Geometry createGeometrtyByWKT(String wktString) throws ParseException{  
        WKTReader reader = new WKTReader( gf );  
        return reader.read(wktString);  
    }
    
    /**
     * 根据JSON创建几何对象
     * @param jsonString
     * @return
     * @throws Exception
     */
    public static Geometry CreateGeometrtyByJSON(String jsonString) throws Exception{  
        //1(point) 2(multipoint) 3(line) 4(multiline) 5(polygon) 6(multipolygon)
        String geoType = GetGeoTypeByJSONString(jsonString);
        if(geoType.equals("1")){
            return createPointGeoJSON(jsonString);
        }else if(geoType.equals("2")){
            return createMultiPointGeoJSON(jsonString);
        }else if(geoType.equals("3")){
            return createLineGeoJSON(jsonString);
        }else if(geoType.equals("4")){
            return createMultiLineGeoJSON(jsonString);
        }else if(geoType.equals("5")){
            return createPolygonGeoJSON(jsonString);
        }else if(geoType.equals("6")){
            return createMultiPolygonGeoJSON(jsonString);
        }
        return null;
    }
    
    /**
     * 绘制矩形
     * @param xmin
     * @param ymin
     * @param xmax
     * @param ymax
     * @return
     */
    public static Polygon createSquare(double xmin,double ymin,double xmax,double ymax){
        Coordinate coords[] = new Coordinate[5];
        coords[0] = new Coordinate(xmin,ymax);
        coords[1] = new Coordinate(xmax,ymax);
        coords[2] = new Coordinate(xmax,ymin);
        coords[3] = new Coordinate(xmin,ymin);
        coords[4] = coords[0];
        LinearRing ring = gf.createLinearRing( coords );
        Polygon polygon = gf.createPolygon( ring, null );
        return polygon;
    }
    
    
    /**
     * 创建一个园
     * @param x
     * @param y
     * @param RADIUS
     * @param sides 圆上面的点个数
     * @return
     */
    public static Polygon createCircle(double x, double y, final double RADIUS,int sides){  
        Coordinate coords[] = new Coordinate[sides+1];  
        for( int i = 0; i < sides; i++){  
            double angle = ((double) i / (double) sides) * Math.PI * 2.0;  
            double dx = Math.cos( angle ) * RADIUS;
            double dy = Math.sin( angle ) * RADIUS;
            coords[i] = new Coordinate( (double) x + dx, (double) y + dy );  
        }
        coords[sides] = coords[0];
        LinearRing ring = gf.createLinearRing( coords );
        Polygon polygon = gf.createPolygon( ring, null );
        return polygon;
    }
    
    /**
     * 创建一个园
     * @param x
     * @param y
     * @param RADIUS
     * @param sides 圆上面的点个数
     * @param initAngle 初始点的角度
     * @return
     */
    public static Polygon createCircle(double x, double y, final double RADIUS,int sides,double initAngle){  
        Coordinate coords[] = new Coordinate[sides+1];  
        for( int i = 0; i < sides; i++){  
            double angle = ((double) i / (double) sides) * Math.PI * 2.0+initAngle;  
            double dx = Math.cos( angle ) * RADIUS;
            double dy = Math.sin( angle ) * RADIUS;
            coords[i] = new Coordinate( (double) x + dx, (double) y + dy );  
        }
        coords[sides] = coords[0];
        LinearRing ring = gf.createLinearRing( coords );
        Polygon polygon = gf.createPolygon( ring, null );
        return polygon;
    }
    
    /**
     * MULTIPOLYGON(((40 10, 30 0, 40 10, 30 20, 40 10),(30 10, 30 0, 40 10, 30 20, 30 10)))
     * @return
     * @throws ParseException
     */
    public static MultiPolygon createMulPolygonByWKT(String wktString) throws ParseException{  
        WKTReader reader = new WKTReader( gf );  
        MultiPolygon mpolygon = (MultiPolygon) reader.read(wktString);  
        return mpolygon;  
    }
    
    /**
     * 至少一个公共点(相交)
     * 几何形状至少有一个共有点（区别于脱节）
     * @param g1
     * @param g2
     * @return
     */
    public static boolean isIntersects(Geometry g1,Geometry g2){
        return g1.intersects(g2);
    }
    
    /**
     * 几何形状没有共有的点。
     * @param g1
     * @param g2
     * @return
     */
    public static boolean isDisjoint(Geometry g1,Geometry g2){
        return g1.disjoint(g2);
    }
    
    /**
     * 几何形状共享一些但不是所有的内部点。
     * @param g1
     * @param g2
     * @return
     */
    public static boolean isCrosses(Geometry g1,Geometry g2){
        return g1.crosses(g2);
    }
    
    /**
     * 几何形状g1的线都在几何形状g2内部。
     * @param g1
     * @param g2
     * @return
     */
    public static boolean isWithin(Geometry g1,Geometry g2){
        return g1.within(g2);
    }
    
    /**
     * 几何形状g1是否包含g2
     * @param g1
     * @param g2
     * @return
     */
    public static boolean isContains(Geometry g1,Geometry g2){
        return g1.contains(g2);
    }
    
    /**
     * 计算wkt格式有多少对xy
     * @param wkt
     * @return
     */
    public static int getWktXYCount(String wkt){
        int retCount = 0;
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(wkt);
        while(matcher.find()){
            retCount++;
        }
        return retCount;
    }
    
    /**
     * 返回几何对象的质心点
     * @param geom
     * @return
     */
    public static Point GetCentroid(Geometry geom){
        return geom.getCentroid();
    }
    
    /**
     * 抽取wkt当中的xy对
     * @param wkt
     * @return
     */
    public static List<String> getXYByWkt(String wkt){
        List<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?) ([-\\+]?\\d+(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(wkt);
        int i=0;
        while(matcher.find()){
            result.add(i, matcher.group());
            i++;
        }
        return result;
    }
    
    /**
     * 计算Geojson格式有多少对xy
     * @param wkt
     * @return
     */
    public static int getGeoJSONXYCount(String json){
        int retCount = 0;
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?),([-\\+]?\\d+(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(json);
        while(matcher.find()){
            retCount++;
        }
        return retCount;
    }
    
    /**
     * 抽取Geojson当中的xy对
     * @param wkt
     * @return
     */
    public static List<String> getXYByGeoJSON(String json){
        List<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile("([-\\+]?\\d+(\\.\\d+)?),([-\\+]?\\d+(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(json);
        int i=0;
        while(matcher.find()){
            result.add(i, matcher.group());
            i++;
        }
        return result;
    }
    
    /**
     * 返回(A)与(B)中距离最近的两个点的距离 
     * @param a
     * @param b
     * @return
     */
    public static double distanceGeo(Geometry a,Geometry b){  
        return a.distance(b);  
    }
    
    /**
     * 缓冲区(如果负责参考BufferOp)
     * @param geo
     * @param radius
     * @return
     */
    public static Geometry buffer(Geometry geo,double radius){
        return geo.buffer(radius);
    }
    
    /**
     * 缓冲区(如果负责参考BufferOp)
     * @param geo
     * @param radius
     * @param quadrantSegments 边数目默认是8
     * @return
     */
    public static Geometry buffer(Geometry geo,double radius,int quadrantSegments){
        return geo.buffer(radius,quadrantSegments);
    }
    
    /**
     * 缓冲区(如果负责参考BufferOp)
     * @param geo
     * @param radius
     * @param quadrantSegments 边数目默认是8
     * BufferOp.CAP_ROUND - (default) a semi-circle 
     * BufferOp.CAP_BUTT - a straight line perpendicular to the end segment 
     * BufferOp.CAP_SQUARE - a half-square 
     * @return
     */
    public static Geometry buffer(Geometry geo,double radius,int quadrantSegments,int endCapStyle){
        return geo.buffer(radius,quadrantSegments,endCapStyle);
    }
    
    
    /**
     * 判断是否重叠
     * 几何形状拓扑上相等。
     * @param geo1
     * @param geo2
     * @return
     */
    public static boolean isOverlap(Geometry geo1,Geometry geo2){
        return geo1.equals(geo2);
    }
    
    /**
     * 判断是否接触
     * 几何形状有至少一个公共的边界点，但是没有内部点。
     * @param geo1
     * @param geo2
     * @return
     */
    public static boolean isTouchs(Geometry geo1,Geometry geo2){
        return geo1.touches(geo2);
    }
    
    /**
     * 返回两个几何对象的交集
     * @param geo1
     * @param geo2
     * @return
     */
    public static Geometry intersection(Geometry geo1,Geometry geo2){
        return geo1.intersection(geo2);
    }
    
    /**
     * geo1,geo2形状的对称差异分析就是位于geo1中或者geo2中但不同时在geo1,geo2中的所有点的集合 
     * (相当于交集之外的)
     * @param geo1
     * @param geo2
     * @return
     */
    public static Geometry symDifference(Geometry geo1,Geometry geo2){
        return geo1.symDifference(geo2);
    }
    
    /**
     * 几何对象合并
     * @param geo1
     * @param geo2
     * @return
     */
    public static Geometry union(Geometry geo1,Geometry geo2){
        return geo1.union(geo2);
    }
    
    /**
     * 根据wkt返回geometry类型 1(point) 2(multipoint) 3(line) 4(multiline) 5(polygon) 6(multipolygon)
     * @param wktString
     * @return
     */
    public static String GetGeoTypeByWKTString(String wktString){
        if(wktString.toLowerCase().indexOf("multipoint")!=-1){
            return "2";
        }else if(wktString.toLowerCase().indexOf("point")!=-1){
            return "1";
        }else if(wktString.toLowerCase().indexOf("multilinestring")!=-1){
            return "4";
        }else if(wktString.toLowerCase().indexOf("linestring")!=-1){
            return "3";
        }else if(wktString.toLowerCase().indexOf("multipolygon")!=-1){
            return "6";
        }else if(wktString.toLowerCase().indexOf("polygon")!=-1){
            return "5";
        }
        return null;
    }
    
    /**
     * 根据json返回geometry类型 1(point) 2(multipoint) 3(line) 4(multiline) 5(polygon) 6(multipolygon)
     * @param wktString
     * @return
     */
    public static String GetGeoTypeByJSONString(String jsonString){
        if(jsonString.toLowerCase().indexOf("multipoint")!=-1){
            return "2";
        }else if(jsonString.toLowerCase().indexOf("point")!=-1){
            return "1";
        }else if(jsonString.toLowerCase().indexOf("multilinestring")!=-1){
            return "4";
        }else if(jsonString.toLowerCase().indexOf("linestring")!=-1){
            return "3";
        }else if(jsonString.toLowerCase().indexOf("multipolygon")!=-1){
            return "6";
        }else if(jsonString.toLowerCase().indexOf("polygon")!=-1){
            return "5";
        }
        return null;
    }
    
    
    /**
     * 多个几何对象合并
     * 传入的的格式为wkt格式集合
     * @param wktList
     * @return
     */
    public static Geometry UnionManyGeo(List<String> wktList) throws Exception{
        Geometry geo = null;
        for(String wkt : wktList){
            String geoType = GetGeoTypeByWKTString(wkt);
            Geometry geo1 = null;
            if(geoType.equals("1")){
                geo1 = createPointByWKT(wkt);
            }else if(geoType.equals("2")){
                geo1 = createMulPointByWKT(wkt);
            }else if(geoType.equals("3")){
                geo1 = createLineByWKT(wkt);
            }else if(geoType.equals("4")){
                geo1 = createMLineByWKT(wkt);
            }else if(geoType.equals("5")){
                geo1 = createPolygonByWKT(wkt);
            }else if(geoType.equals("6")){
                geo1 = createMulPolygonByWKT(wkt);
            }
            if(null==geo){
                geo = geo1;
            }else{
                geo = union(geo, geo1);
            }
        }
        return geo;
    }
    
    /**
     * 在geo1几何对象中有的，但是geo2几何对象中没有
     * @param geo1
     * @param 
     * @return
     */
    public static Geometry difference(Geometry geo1,Geometry geo2){
        return geo1.difference(geo2);
    }
    
    /**
     * 包含几何形体的所有点的最小凸壳多边形（外包多边形）
     * @param geo
     * @return
     */
    public static Geometry convexHull(Geometry geo){
        return geo.convexHull();
    }
    
    /**
     * 合并线 线路合并，线路之间不产生有交点
     * union会产生交点
     * @param wkts
     * @return
     */
    public static Collection<Geometry> mergerLines(List<String> wkts){
        Collection<Geometry> collect = null;
        try {
            LineMerger lineMerger = new LineMerger();
            List<Geometry> geoList = new ArrayList<Geometry>();
            WKTReader reader = new WKTReader(gf);  
            for(int i=0;i<wkts.size();i++){
                geoList.add(i, reader.read(wkts.get(i)));
            }
            lineMerger.add(geoList);
            collect = lineMerger.getMergedLineStrings();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return collect;
    }
    
    /**
     * geojson 转换点
     * @param geojson
     * @return
     * @throws IOException
     */
    public static Point createPointGeoJSON(String geojson) throws IOException{
        GeometryJSON gjson = new GeometryJSON();
        Point point = gjson.readPoint(geojson); 
        return point;
    }
    
    /**
     * geojson 转换line
     * @param geojson
     * @return
     * @throws IOException
     */
    public static LineString createLineGeoJSON(String geojson) throws IOException{
        GeometryJSON gjson = new GeometryJSON();
        LineString lineString = gjson.readLine(geojson); 
        return lineString;
    }
    
    /**
     * geojson 转换Polygon
     * @param geojson
     * @return
     * @throws IOException
     */
    public static Polygon createPolygonGeoJSON(String geojson) throws IOException{
        GeometryJSON gjson = new GeometryJSON();
        Polygon polygon = gjson.readPolygon(geojson); 
        return polygon;
    }
    
    /**
     * geojson 转换multiPoint
     * @param geojson
     * @return
     * @throws IOException
     */
    public static MultiPoint createMultiPointGeoJSON(String geojson) throws IOException{
        GeometryJSON gjson = new GeometryJSON();
        MultiPoint multiPoint = gjson.readMultiPoint(geojson); 
        return multiPoint;
    }
    
    /**
     * geojson 转换MultiLineString
     * @param geojson
     * @return
     * @throws IOException
     */
    public static MultiLineString createMultiLineGeoJSON(String geojson) throws IOException{
        GeometryJSON gjson = new GeometryJSON();
        MultiLineString multiLine = gjson.readMultiLine(geojson); 
        return multiLine;
    }
    
    /**
     * 创建MultiPolygon
     * @param polygons
     * @return
     */
    public static MultiPolygon createMultiPolygon(Polygon[] polygons){
       return  gf.createMultiPolygon(polygons);
    }
    
    /**
     * geojson 转换MultiPolygon
     * @param geojson
     * @return
     * @throws IOException
     */
    public static MultiPolygon createMultiPolygonGeoJSON(String geojson) throws IOException{
        GeometryJSON gjson = new GeometryJSON();
        MultiPolygon multiPolygon = gjson.readMultiPolygon(geojson); 
        return multiPolygon;
    }
    
    /**
     * Feature对象转换成GeoJSON
     * @param feature
     * @return
     * @throws IOException
     */
    public static String FeatureToJSON(SimpleFeature feature) throws IOException{
        FeatureJSON fjson = new FeatureJSON();
        String json = fjson.toString(feature);
        return json;
    }
    
    
    /**
     * Features对象转换成GeoJSON 也可使用ConversionUtil WriteGeoJSONFile
     * @param feature
     * @return
     * @throws IOException
     */
    public static String FeatureToJSON(FeatureCollection features) throws Exception{
        FeatureJSON fjson = new FeatureJSON();
        String json = fjson.toString(features);
        return json;
    }
    
    /**
     * 根据arcgis格式的json转换成Geometry
     * @param agjson
     * @return
     */
    public static Geometry ArcgisJSONToGeometry(String agjson){
        Geometry geom = null;
        try {
            JSONObject jsonObject = (JSONObject)jsonparser.parse(agjson);
            int srid = 0;
            JSONObject wktObject = (JSONObject)jsonObject.get("spatialReference");
            if(null!=wktObject&&null!=wktObject.get("wkid")){
                srid = Integer.parseInt(wktObject.get("wkid").toString());
            }
            if(null!=jsonObject.get("rings")){//面要素
                JSONArray ringsArr = (JSONArray)(jsonObject.get("rings"));
                Polygon[] polygonArr = new Polygon[ringsArr.size()];
                for(int i=0;i<ringsArr.size();i++){
                    JSONArray subringsArr = (JSONArray)ringsArr.get(i);
                    Coordinate[] coords = new Coordinate[subringsArr.size()];
                    for(int j=0;j<subringsArr.size();j++){
                        JSONArray coorArr = (JSONArray)subringsArr.get(j);
                        coords[j] = new Coordinate(Double.parseDouble(coorArr.get(0).toString()),Double.parseDouble(coorArr.get(1).toString()));
                    }
                    LinearRing ring = gf.createLinearRing(coords);
                    LinearRing holes[] = null;
                    Polygon polygon = gf.createPolygon(ring,holes);
                    polygonArr[i] = polygon;
                }
                geom = gf.createMultiPolygon(polygonArr);
                geom.setSRID(srid);
            }else if(null!=jsonObject.get("paths")){//线
                JSONArray pathsArr = (JSONArray)(jsonObject.get("paths"));
                LineString[] lineStringArr = new LineString[pathsArr.size()];
                for(int i=0;i<pathsArr.size();i++){
                    JSONArray subPathArr = (JSONArray)pathsArr.get(i);
                    Coordinate[] coords = new Coordinate[subPathArr.size()];
                    for(int j=0;j<subPathArr.size();j++){
                        JSONArray coorArr = (JSONArray)subPathArr.get(j);
                        coords[j] = new Coordinate(Double.parseDouble(coorArr.get(0).toString()),Double.parseDouble(coorArr.get(1).toString()));
                    }
                    LineString lineString = gf.createLineString(coords);
                    lineStringArr[i] = lineString;
                }
                geom = gf.createMultiLineString(lineStringArr);
                geom.setSRID(srid);
            }else if(null!=jsonObject.get("x")&&null!=jsonObject.get("y")){//点
                geom = createPoint(Double.parseDouble(jsonObject.get("x").toString()),Double.parseDouble(jsonObject.get("y").toString()));
                geom.setSRID(srid);
            }else if (null!=jsonObject.get("xmin") && null!=jsonObject.get("yin")&& null!=jsonObject.get("xmax")&& null!=jsonObject.get("ymax")) {
                double xmin = Double.parseDouble(jsonObject.get("xmin").toString());
                double ymin = Double.parseDouble(jsonObject.get("ymin").toString());
                double xmax = Double.parseDouble(jsonObject.get("xmax").toString());
                double ymax = Double.parseDouble(jsonObject.get("ymax").toString());
                Coordinate[] coordinates = new Coordinate[5];
                coordinates[0] = new Coordinate(xmin, ymin);
                coordinates[1] = new Coordinate(xmin, ymax);
                coordinates[2] = new Coordinate(xmax, ymax);
                coordinates[3] = new Coordinate(xmax, ymin);
                coordinates[4] = new Coordinate(xmin, ymin);
                LinearRing shell = GeometryUtil.gf().createLinearRing(coordinates);
                geom = GeometryUtil.gf().createPolygon(shell, null);
                geom.setSRID(srid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return geom;
    }
    
    /**
     * GeoJSON对象转换成Feature
     * @param GeoJSON
     * @return
     * @throws IOException
     */
    public static SimpleFeature JSONToFeature(String json) throws IOException{
        FeatureJSON fjson = new FeatureJSON();
        return fjson.readFeature(json);
    }
    
    /**
     * 读取GeoJSON文件
     * @param jsonFilePath
     * @return 返回SimpleFeature集合
     */
    public static List<SimpleFeature> ReadJSONFile(String jsonFilePath){
        FeatureJSON io = new FeatureJSON();
        FeatureIterator<SimpleFeature> features;
        List<SimpleFeature> sfList = new ArrayList<SimpleFeature>();
        try {
            features = io.streamFeatureCollection(jsonFilePath);
            SimpleFeature sfTemp = null;
            while(features.hasNext()) {
                sfTemp = features.next();
                sfList.add(sfTemp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sfList;
    }
    
    /**
     * 计算84坐标系两点之间的距离
     * @param x1 经度
     * @param y1 纬度
     * @param x2 经度
     * @param y2 纬度
     * @return 两点直接的距离米
     */
    public static double GetDistance84(double x1,double y1,double x2,double y2){
        double lon1 = x1;
        double lat1 = y1;
        double lon2 = x2;
        double lat2 = y2;
        double a = 6378137;
        double b = 6356752.3142;
        double f = 1 / 298.257223563;
        double L = Math.toRadians(lon2 - lon1);
        double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
        double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));
        double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);
        double lambda = L;
        double lambdaP = 0.0;
        double iterLimit = 100;
        double cosSqAlpha = 0.0;
        double cos2SigmaM = 0.0;
        double sinSigma = 0.0;
        double sinLambda = 0.0;
        double cosLambda = 0.0;
        double cosSigma = 0.0;
        double sigma = 0.0;
        do {
            sinLambda = Math.sin(lambda);
            cosLambda = Math.cos(lambda);
            sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda) + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
            if(sinSigma == 0)
                return 0;
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda = L + (1 - C) * f * sinAlpha * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
        }while (Math.abs(lambda-lambdaP) > (1e-12) && --iterLimit>0);
        if(iterLimit == 0) {
            return -1.0;
        }
        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
        double  A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double  B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double  deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
        double  s = b * A * (sigma - deltaSigma);
        return s;        
    }
    
    /**
     * 点投影到线 返回投影的坐标点
     * @param point 原始点
     * @param line 参考线
     * @param check 是否检查点的合理性 
     * @return
     */
    public static Point PointPojectLine(Point point,LineString line,boolean check) {
    	Point result = null;
    	
    	return result;
    }
    
    /**
     * 平行线生成
     * @param line 原始线
     * @param orientation
     * @param distance
     * @return
     */
	public static LineString createParallelLineString(final LineString line, final int orientation,final double distance) {
		GeometryFactory factory = line.getFactory();
		CoordinateSequence coordinates = line.getCoordinateSequence();
		List<Coordinate> newCoordinates = new ArrayList<Coordinate>();
		Coordinate coordinate = coordinates.getCoordinate(0);
		LineSegment lastLineSegment = null;
		int coordinateCount = coordinates.size();
		for (int i = 0; i < coordinateCount; i++) {
			Coordinate nextCoordinate = null;
			LineSegment lineSegment = null;
			if (i < coordinateCount - 1) {
				nextCoordinate = coordinates.getCoordinate(i + 1);
				lineSegment = new LineSegment(coordinate, nextCoordinate);
				lineSegment = offset(lineSegment, distance, orientation);
			}
			if (lineSegment == null) {
				newCoordinates.add(lastLineSegment.p1);
			} else if (lastLineSegment == null) {
				newCoordinates.add(lineSegment.p0);
			} else {
				Coordinate intersection = lastLineSegment.intersection(lineSegment);
				if (intersection != null) {
					newCoordinates.add(intersection);
				} else {
					newCoordinates.add(lineSegment.p0);
				}
			}
			coordinate = nextCoordinate;
			lastLineSegment = lineSegment;
		}
		CoordinateSequence newCoords = PackedCoordinateSequenceFactory.DOUBLE_FACTORY.create(newCoordinates.toArray(new Coordinate[0]));
		return factory.createLineString(newCoords);
	}

	/**
	 * LineSegment 偏移
	 * @param line
	 * @param distance
	 * @param orientation
	 * @return
	 */
	public static LineSegment offset(final LineSegment line, final double distance, final int orientation) {
		double angle = line.angle();
		if (orientation == Angle.CLOCKWISE) {
			angle -= Angle.PI_OVER_2;
		} else {
			angle += Angle.PI_OVER_2;
		}
		Coordinate c1 = offset(line.p0, angle, distance);
		Coordinate c2 = offset(line.p1, angle, distance);
		return new LineSegment(c1, c2);
	}

	/**
	 * Coordinate 偏移
	 * @param coordinate
	 * @param angle
	 * @param distance
	 * @return
	 */
	public static Coordinate offset(final Coordinate coordinate, final double angle, final double distance) {
		double newX = coordinate.x + distance * Math.cos(angle);
		double newY = coordinate.y + distance * Math.sin(angle);
		Coordinate newCoordinate = new Coordinate(newX, newY);
		return newCoordinate;
	}
	
	/**
	 * LineSegment 变长
	 * @param line
	 * @param startDistance
	 * @param endDistance
	 * @return
	 */
	public static LineSegment addLength(final LineSegment line, final double startDistance, final double endDistance) {
		    double angle = line.angle();
		    Coordinate c1 = offset(line.p0, angle, -startDistance);
		    Coordinate c2 = offset(line.p1, angle, endDistance);
		    return new LineSegment(c1, c2);
	}
	
	/**
	 * 获取geom的范围 xmin ymin xmax ymax
	 * @param geom
	 * @return
	 */
	public static double[] GetGeometryBounds(Geometry geom) {
		double[] result = new double[4];
		Coordinate[] coorArray = geom.getCoordinates();
		result[0] = coorArray[0].x;
		result[1] = coorArray[0].y;
		result[2] = coorArray[0].x;
		result[3] = coorArray[0].y;
		for(int i=1;i<coorArray.length;i++) {
			Coordinate coorTemp = coorArray[i];
			if(result[0]>coorTemp.x) {
				result[0] = coorTemp.x;
			}
			if(result[2]<coorTemp.x) {
				result[2] = coorTemp.x;
			}
			if(result[1]>coorTemp.y) {
				result[1] = coorTemp.y;
			}
			if(result[3]<coorTemp.y) {
				result[3] = coorTemp.y;
			}
		}
		return result;
	}
	
	
	
	/**
	 * 分割线
	 * @param line
	 * @param coordinate
	 * @return
	 */
	public static List<LineString> SplitLineString(final LineString line, final Coordinate coordinate) {
		int[] indexes = findClosestSegmentAndCoordinate(line, coordinate);
		int segmentIndex = indexes[0];
		if (segmentIndex != -1) {
			int coordinateIndex = indexes[1];
			boolean exactMatch = coordinateIndex == 1;
			if (coordinateIndex == 0) {
				if (exactMatch) {
					return Collections.singletonList(line);
				} else {
					Coordinate c0 = line.getCoordinateN(0);
					Coordinate c1;
					int i = 1;
					do {
						c1 = line.getCoordinateN(i);
						i++;
					} while (c1.equals(c0));

					if (Angle.isAcute(c1, c0, coordinate)) {
						Coordinate projectedCoordinate = new LineSegment(c0, c1).project(coordinate);
						return split(line, 1, projectedCoordinate);
					} else {
						return Collections.singletonList(line);
					}
				}
			} else if (coordinateIndex == line.getNumPoints() - 1) {
				if (exactMatch) {
					return Collections.singletonList(line);
				} else {
					Coordinate cn = line.getCoordinateN(line.getNumPoints() - 1);
					Coordinate cn1;
					int i = line.getNumPoints() - 2;
					do {
						cn1 = line.getCoordinateN(i);
						i++;
					} while (cn1.equals(cn));
					if (Angle.isAcute(cn1, cn, coordinate)) {
						Coordinate projectedCoordinate = new LineSegment(cn, cn1).project(coordinate);
						return split(line, line.getNumPoints() - 1, projectedCoordinate);
					} else {
						return Collections.singletonList(line);
					}
				}
			} else {
				Coordinate c = line.getCoordinateN(segmentIndex);
				Coordinate c1;
				int i = segmentIndex + 1;
				do {
					c1 = line.getCoordinateN(i);
					i++;
				} while (c.equals(c1));
				Coordinate projectedCoordinate = new LineSegment(c, c1).project(coordinate);
				return split(line, segmentIndex, projectedCoordinate);
			}
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * findClosestSegmentAndCoordinate
	 * @param line
	 * @param coordinate -1代表没有匹配上
	 * @return index 2 0 代表是中间，1代表是节点 
	 */
	public static int[] findClosestSegmentAndCoordinate(final LineString line, final Coordinate coordinate) {
		int[] closest = new int[] { -1, -1, 0 };
		double closestDistance = Double.MAX_VALUE;
		CoordinateSequence coordinates = line.getCoordinateSequence();
		Coordinate previousCoord = coordinates.getCoordinate(0);
		double previousCoordinateDistance = previousCoord.distance(coordinate);
		if (previousCoordinateDistance == 0) {
			closest[0] = 0;
			closest[1] = 0;
			closest[2] = 1;
		} else {
			for (int i = 1; i < coordinates.size(); i++) {
				Coordinate currentCoordinate = coordinates.getCoordinate(i);
				double currentCoordinateDistance = currentCoordinate.distance(coordinate);
				if (currentCoordinateDistance == 0) {
					closest[0] = i;
					closest[1] = i;
					closest[2] = 1;
					return closest;
				}
				LineSegment lineSegment = new LineSegment(previousCoord, currentCoordinate);
				double distance = lineSegment.distance(coordinate);
				if (distance == 0) {
					closest[0] = i - 1;
					if (previousCoordinateDistance < currentCoordinateDistance) {
						closest[1] = i - 1;
					} else {
						closest[1] = i;
					}
					return closest;
				} else if (distance < closestDistance) {
					closestDistance = distance;
					closest[0] = i - 1;
					if (previousCoordinateDistance < currentCoordinateDistance) {
						closest[1] = i - 1;
					} else {
						closest[1] = i;
					}
				}
				previousCoord = currentCoordinate;
			}
		}
		return closest;
	}
	
	/**
	 * split
	 * @param line
	 * @param index
	 * @param coordinate
	 * @return
	 */
	public static List<LineString> split(final LineString line, final int index, final Coordinate coordinate) {
		List<LineString> lines = new ArrayList<LineString>();
		boolean containsCoordinate = coordinate.equals(line.getCoordinateN(index));
		CoordinateSequence coords = line.getCoordinateSequence();
		int dimension = coords.getDimension();
		int coords1Size;
		int coords2Size = coords.size() - index;
		if (containsCoordinate) {
			coords1Size = index + 1;
			coords2Size = coords.size() - index;
		} else {
			coords1Size = index + 2;
			coords2Size = coords.size() - index;
		}
		CoordinateSequence coords1 = PackedCoordinateSequenceFactory.DOUBLE_FACTORY.create(coords1Size, dimension);
		copyCoords(coords, 0, coords1, 0, index + 1);
		if (!containsCoordinate) {
			setCoordinate(coords1, coords1Size - 1, coordinate);
		}
		CoordinateSequence coords2 = PackedCoordinateSequenceFactory.DOUBLE_FACTORY.create(coords2Size, dimension);
		if (!containsCoordinate) {
			setCoordinate(coords2, 0, coordinate);
			copyCoords(coords, index + 1, coords2, 1, coords2.size() - 1);
		} else {
			copyCoords(coords, index, coords2, 0, coords2.size());
		}
		GeometryFactory geometryFactory = line.getFactory();
		if (coords1Size > 1) {
			LineString line1 = geometryFactory.createLineString(coords1);
			if (line1.getLength() > 0) {
				lines.add(line1);
			}
		}
		if (coords2Size > 1) {
			LineString line2 = geometryFactory.createLineString(coords2);
			if (line2.getLength() > 0) {
				lines.add(line2);
			}
		}
		return lines;
	}

	/**
	 * copyCoords
	 * @param src
	 * @param srcPos
	 * @param dest
	 * @param destPos
	 * @param length
	 */
	public static void copyCoords(final CoordinateSequence src, final int srcPos, final CoordinateSequence dest,
			final int destPos, final int length) {
		int dimension = Math.min(src.getDimension(), dest.getDimension());
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < dimension; j++) {
				double ordinate = src.getOrdinate(srcPos + i, j);
				dest.setOrdinate(destPos + i, j, ordinate);
			}
		}
	}

	/**
	 * setCoordinate
	 * @param coordinates
	 * @param i
	 * @param coordinate
	 */
	public static void setCoordinate(final CoordinateSequence coordinates, final int i, final Coordinate coordinate) {
		coordinates.setOrdinate(i, 0, coordinate.x);
		coordinates.setOrdinate(i, 1, coordinate.y);
		if (coordinates.getDimension() > 2) {
			coordinates.setOrdinate(i, 2, coordinate.z);
		}
	}
	
	/**
	 * 根据范围创建网格数据
	 * @param xmin
	 * @param ymin
	 * @param xmax
	 * @param ymax
	 * @param radius
	 * @return
	 */
	public static List<Polygon> CreateSquareByExtents(double xmin,double ymin,double xmax,double ymax,double radius){
        int index = 0;
        List<Polygon> retList = new ArrayList<Polygon>();
        double xTemp = 0;
        double yTemp = 0;
        int yInt = (int)((ymax-ymin)/(radius))+1;
        int xInt = (int)((xmax-xmin)/(radius))+1;
        for(int i=0;i<xInt;i++){
            xTemp = xmin + (i)*radius;
            for(int j=0;j<yInt;j++){
                yTemp = ymax - (j)*radius;
                Polygon polygon = createSquare(xTemp,yTemp-radius,xTemp+radius,yTemp);
                retList.add(index, polygon);
                index++;
            }
        }
        return retList;
    }
	
	/**
	 * 根据范围创建网格数据 以字符串方式返回 indexX:indexY:wkt
	 * @param xmin
	 * @param ymin
	 * @param xmax
	 * @param ymax
	 * @param radius
	 * @return
	 */
	public static List<String> GetStringSquareByExtents(double xmin,double ymin,double xmax,double ymax,double radius){
        int index = 0;
        List<String> retList = new ArrayList<String>();
        double xTemp = 0;
        double yTemp = 0;
        int yInt = (int)((ymax-ymin)/(radius))+1;
        int xInt = (int)((xmax-xmin)/(radius))+1;
        for(int i=0;i<xInt;i++){
            xTemp = xmin + (i)*radius;
            for(int j=0;j<yInt;j++){
                yTemp = ymax - (j)*radius;
                Polygon polygon = createSquare(xTemp,yTemp-radius,xTemp+radius,yTemp);
                retList.add(index, i+":"+j+":"+polygon.toText());
                index++;
            }
        }
        return retList;
    }
	
	/**
	 * 根据范围创建网格数据 以字符串方式返回 indexX:indexY:wkt
	 * @param xmin
	 * @param ymin
	 * @param xmax
	 * @param ymax
	 * @param radius
	 * @return
	 */
	public static List<MapGrid> GetGridSquareByExtents(double xmin,double ymin,double xmax,double ymax,double radius){
        int index = 0;
        List<MapGrid> retList = new ArrayList<MapGrid>();
        double xTemp = 0;
        double yTemp = 0;
        int yInt = (int)((ymax-ymin)/(radius))+1;
        int xInt = (int)((xmax-xmin)/(radius))+1;
        for(int i=0;i<xInt;i++){
            xTemp = xmin + (i)*radius;
            for(int j=0;j<yInt;j++){
                yTemp = ymax - (j)*radius;
                Polygon polygon = createSquare(xTemp,yTemp-radius,xTemp+radius,yTemp);
                MapGrid mg = new MapGrid();
                mg.setIndex(index+"");
                mg.polygon=polygon;
                mg.setX(i);
                mg.setY(j);
                mg.setWkt(polygon.toText());
                retList.add(index, mg);
                index++;
            }
        }
        return retList;
    }
}
