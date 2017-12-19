/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.pathfind;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.utils.gisdata.MapGrid;
import com.promisepb.utils.gisutils.PBGTGeometryUtil;
import com.promisepb.utils.gisutils.PBGeoShapeUtil;
import com.promisepb.utils.poiutils.PBPOIExcelUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Polygon;

/**  
 * 功能描述: 拓扑文件生成帮助类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年11月23日 下午6:02:40  
 */
public class PBTopyUtil {

	public  Logger logger = LoggerFactory.getLogger(PBTopyUtil.class);
	//空间数据集合范围
	private   double[]  bounds = null;
	//网格大小单位米
	private  int gridSize;
	//网格空间数据集合
	public  Map<String,Polygon> gridMap = new HashMap<String,Polygon>();
	private static PBTopyUtil pBTopyUtil;
	
	public static PBTopyUtil newInstance() {
		if(null==pBTopyUtil) {
			pBTopyUtil = new PBTopyUtil();
		}
		return pBTopyUtil;
	}
	
	/**
	 * 根据网格获取网格索引
	 * @param x
	 * @param y
	 * @return
	 */
	private int[] getGridIndex(double x,double y) {
		int[] result = new int[] {-1,-1};
		double xmin = bounds[0];
		double ymin = bounds[1];
		double xmax = bounds[2];
		double ymax = bounds[3];
		double step = (double)gridSize/1852/60;
		if(x>=xmin&&x<=xmax&&y>=ymin&&y<=ymax) {
			result[0] = (int)((x-xmin)/step);
			result[1] = (int)((ymax-y)/step);
		}
		return result;
	}
	
	/**
	 * 获取相关联网格集合
	 * @param swLink
	 * @return
	 */
	public String getSWLinkGridIndexs(LineString  swLink) {
		String result = "";
		double[] coorArray = PBGTGeometryUtil.GetGeometryBounds(swLink);
		int[] left_up_index = getGridIndex(coorArray[0],coorArray[3]);
		int[] right_down_index = getGridIndex(coorArray[2],coorArray[1]);
		int xStartIndex = left_up_index[0];
		int yStartIndex = left_up_index[1];
		int xEndIndex = right_down_index[0];
		int yEndIndex = right_down_index[1];
		for(int i=xStartIndex;i<=xEndIndex;i++) {
			for(int j=yStartIndex;j<=yEndIndex;j++) {
				if(gridMap.get(i+":"+j).intersects(swLink)) {
					result+=i+"-"+j+":";
				}
			}
		}
		return result.substring(0, result.length()-1);
	}
	
	/**
	 * 创建拓扑文件
	 * @param shpFilePath
	 * @param gridSize
	 * @param shpEncoding
	 * @param topyFilePath
	 * @throws Exception
	 */
	public  void createSWTopyFile(String shpFilePath,int gridSize,String shpEncoding,String topyFilePath) throws Exception{
		this.gridSize = gridSize;
		initBounds(shpFilePath,shpEncoding);
		initMapGrid();
		List<String> rowStrList = new ArrayList<String>();
		BufferedWriter csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(topyFilePath)), shpEncoding), 10240);
		SimpleFeatureCollection sfc = PBGeoShapeUtil.ReadShapeFileFeatures(shpFilePath,shpEncoding);
		SimpleFeatureIterator sfi = sfc.features();
		while(sfi.hasNext()) {
			SimpleFeature sf = sfi.next();
			String id = sf.getAttribute("ID").toString();
			String direction = sf.getAttribute("DIRECTION").toString();
			String snodeID = sf.getAttribute("SNODEID").toString();
			String enodeID = sf.getAttribute("ENODEID").toString();
			double length = Double.valueOf(sf.getAttribute("LENGTH").toString());
			LineString line = (LineString)((MultiLineString)sf.getDefaultGeometry()).getGeometryN(0);
			CoordinateSequence  coorSequence = line.getCoordinateSequence();
			Coordinate coorFirst = coorSequence.getCoordinate(0);
			Coordinate coorLast = coorSequence.getCoordinate(coorSequence.size()-1);
			String gridIndexStr = getSWLinkGridIndexs(line);
			String rowStr = id+"#"+direction+"#"+length+"#"+coorFirst.x+":"+coorFirst.y+"#"+coorLast.x+":"+coorLast.y+"#"+gridIndexStr+"#"+handleNodeID(snodeID)+"#"+handleNodeID(enodeID);
			rowStrList.add(rowStr);
		}
		PBPOIExcelUtil.WriteRow(rowStrList, csvWtriter);
		csvWtriter.flush();
		csvWtriter.close();
	}
	
	/**
	 * 获取图层范围
	 * @param shpFilePath
	 * @param shpEncoding
	 */
	public  void initBounds(String shpFilePath,String shpEncoding) {
		bounds = PBGeoShapeUtil.GetShapeFileBounds(shpFilePath,shpEncoding);
	}
	
	public String handleNodeID(String nodeID) {
		int nodeStrLength = nodeID.length();
		if(nodeStrLength==11) {
			if(nodeID.startsWith("10000") || nodeID.startsWith("20000")) {
				nodeID = nodeID.substring(nodeStrLength-6);
			}else if(nodeID.startsWith("1000") || nodeID.startsWith("2000")) {
				nodeID = nodeID.substring(nodeStrLength-7);
			}else if(nodeID.startsWith("100") || nodeID.startsWith("200")) {
				nodeID = nodeID.substring(nodeStrLength-8);
			}
		}
		return nodeID;
	}
	
	/**
	 * 初始化mapgrid
	 */
	private  void initMapGrid() {
		double xmin = bounds[0];
		double ymin = bounds[1];
		double xmax = bounds[2];
		double ymax = bounds[3];
		double step = (double)gridSize/1852/60;
		try {
			List<MapGrid> listMapGrid = PBGTGeometryUtil.GetGridSquareByExtents(xmin, ymin, xmax, ymax, step);
			for(MapGrid mgTemp : listMapGrid) {
				gridMap.put(mgTemp.x+":"+mgTemp.getY(), mgTemp.polygon);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
