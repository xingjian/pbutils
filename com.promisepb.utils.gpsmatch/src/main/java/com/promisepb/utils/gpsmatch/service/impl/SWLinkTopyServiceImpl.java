/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.gpsmatch.service.impl;

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
import com.promisepb.utils.gisdata.SWLink;
import com.promisepb.utils.gisutils.PBGTGeometryUtil;
import com.promisepb.utils.gisutils.PBGeoShapeUtil;
import com.promisepb.utils.gpsmatch.service.SWLinkTopyService;
import com.promisepb.utils.mathutils.PBMathUtil;
import com.promisepb.utils.poiutils.PBPOIExcelUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Polygon;
/**
 * 
 * 功能描述: 四维路链拓扑关系生成接口实现类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年11月8日 下午7:11:30
 */
public class SWLinkTopyServiceImpl implements SWLinkTopyService {

	public static final Logger logger = LoggerFactory.getLogger(SWLinkTopyServiceImpl.class);
	//栅格大小
	public int gridSize;
	//路链数据名称
	public String shapeFileName;
	//工作空间
	public String workspacePath;
	//拓扑文件名称
	public String topyFileName;
	//shp文件编码
	public String shapeFileCharSet;
	public List<SWLink> swLinkList;
	public Map<String,SWLink> swLinkMap = new HashMap<String,SWLink>();
	public Map<String,String> sNodeMap = new HashMap<String,String>();
	public Map<String,String> eNodeMap = new HashMap<String,String>();
	public Map<String,Polygon> gridMap = new HashMap<String,Polygon>();
	public BufferedWriter csvWtriter = null;
	public boolean exportMapGridFile;
	public double[] bounds;
	@Override
	public void createTopyFile(){
		logger.info("gridSize : "+gridSize);
		logger.info("shapeFileName : "+shapeFileName+"("+shapeFileCharSet+")");
		logger.info("workspacePath : "+workspacePath);
		logger.info("topyFileName : "+topyFileName);
		try {
			bounds = getLinksBounds();
			initMapGrid();
			initSWLinkVOList();
			List<String> rowStrList = new ArrayList<String>();
			for(SWLink swLinkTemp : swLinkList) {
				LineString line = (LineString)(swLinkTemp.getmLine()).getGeometryN(0);
				CoordinateSequence  coorSequence = line.getCoordinateSequence();
				Coordinate coorFirst = coorSequence.getCoordinate(0);
				Coordinate coorLast = coorSequence.getCoordinate(coorSequence.size()-1);
				String gridIndexStr = getSWLinkGridIndexs(swLinkTemp);
				double angleTemp = 0;
	        	if(swLinkTemp.getDirection().equals("3")) {
	        		angleTemp = PBMathUtil.getVectorAngle(coorLast.x, coorLast.y,coorFirst.x, coorFirst.y );
	        	}else {
	        		angleTemp = PBMathUtil.getVectorAngle(coorFirst.x, coorFirst.y, coorLast.x, coorLast.y);
	        	}
				String rowStr = swLinkTemp.getLinkid()+"#"+swLinkTemp.getDirection()+"#"+swLinkTemp.getLength()+"#"+coorFirst.x+":"+coorFirst.y+
						"#"+coorLast.x+":"+coorLast.y+"#"+gridIndexStr+"#"+swLinkTemp.getSnodeID()+"#"+swLinkTemp.getEnodeID()+"#"+angleTemp+"#"+swLinkTemp.getmLine().toText();
				rowStrList.add(rowStr);
			}
			PBPOIExcelUtil.WriteRow(rowStrList, csvWtriter);
			csvWtriter.flush();
			csvWtriter.close();
			logger.info(topyFileName+".csv生成。");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化mapgrid
	 */
	public void initMapGrid() {
		double xmin = bounds[0];
		double ymin = bounds[1];
		double xmax = bounds[2];
		double ymax = bounds[3];
		double step = (double)gridSize/1852/60;
		logger.info("init map grid.....");
		try {
			List<MapGrid> listMapGrid = PBGTGeometryUtil.GetGridSquareByExtents(xmin, ymin, xmax, ymax, step);
			for(MapGrid mgTemp : listMapGrid) {
				gridMap.put(mgTemp.x+":"+mgTemp.getY(), mgTemp.polygon);
			}
			if(exportMapGridFile) {
				BufferedWriter wktWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(workspacePath+File.separator+"map_grid.csv")), shapeFileCharSet), 10240);
				PBPOIExcelUtil.WriteRow(listMapGrid, wktWtriter);
				wktWtriter.flush();
				wktWtriter.close();
				logger.info("export map grid csv file success.");
				PBGeoShapeUtil.ListObjectToShapeFile(listMapGrid, workspacePath+File.separator+"map_grid.shp", shapeFileCharSet, 5+"", "wkt","EPSG:4326");
				logger.info("export all map grid shp file success.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 获取路链相关的网格
	 * @return
	 */
	public String getSWLinkGridIndexs(SWLink  swLink) {
		String result = "";
		double[] coorArray = PBGTGeometryUtil.GetGeometryBounds(swLink.getmLine());
		int[] left_up_index = getGridIndex(coorArray[0],coorArray[3]);
		int[] right_down_index = getGridIndex(coorArray[2],coorArray[1]);
		int xStartIndex = left_up_index[0];
		int yStartIndex = left_up_index[1];
		int xEndIndex = right_down_index[0];
		int yEndIndex = right_down_index[1];
		for(int i=xStartIndex;i<=xEndIndex;i++) {
			for(int j=yStartIndex;j<=yEndIndex;j++) {
				if(gridMap.get(i+":"+j).intersects(swLink.getmLine())) {
					result+=i+"-"+j+":";
				}
			}
		}
		return result.substring(0, result.length()-1);
	}
	
	/**
	 * 获取点坐标格子的坐标索引
	 * @param x
	 * @param y
	 * @return
	 */
	public int[] getGridIndex(double x,double y) {
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
	 * 处理路链连通性ID
	 * @param nodeID
	 * @return
	 */
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
	 * 获取路链集合对象
	 * @return
	 */
	public void initSWLinkVOList() throws Exception{
		csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(workspacePath+File.separator+topyFileName+".csv")), shapeFileCharSet), 10240);
		swLinkList = new ArrayList<SWLink>();
		SimpleFeatureCollection sfc = PBGeoShapeUtil.ReadShapeFileFeatures(workspacePath+File.separator+shapeFileName+".shp",shapeFileCharSet);
		SimpleFeatureIterator sfi = sfc.features();
		while(sfi.hasNext()) {
			SimpleFeature sf = sfi.next();
			String id = sf.getAttribute("ID").toString();
			String mapID = sf.getAttribute("MAPID").toString();
			String direction = sf.getAttribute("DIRECTION").toString();
			String snodeID = sf.getAttribute("SNODEID").toString();
			String enodeID = sf.getAttribute("ENODEID").toString();
			String kind = sf.getAttribute("KIND").toString().substring(0, 4);
			double length = Double.valueOf(sf.getAttribute("LENGTH").toString());
			SWLink  swLink = new SWLink();
			swLink.setDirection(direction);
			swLink.setMapid(mapID);
			swLink.setEnodeID(handleNodeID(enodeID));
			swLink.setKind(kind);
			swLink.setLength(length);
			swLink.setLinkid(id);
			swLink.setSnodeID(handleNodeID(snodeID));
			swLink.setmLine((MultiLineString)(sf.getDefaultGeometry()));
			swLinkList.add(swLink);
			swLinkMap.put(id, swLink);
			if(null!=swLink.getSnodeID()) {
				if(null==sNodeMap.get(swLink.getSnodeID())) {
					sNodeMap.put(swLink.getSnodeID(), swLink.getLinkid());
				}else {
					sNodeMap.put(swLink.getSnodeID(), sNodeMap.get(swLink.getSnodeID())+","+swLink.getLinkid());
				}
			}
			if(null!=swLink.getEnodeID()) {
				if(null==eNodeMap.get(swLink.getEnodeID())) {
					eNodeMap.put(swLink.getEnodeID(), swLink.getLinkid());
				}else {
					eNodeMap.put(swLink.getEnodeID(), eNodeMap.get(swLink.getEnodeID())+","+swLink.getLinkid());
				}
			}
		}
		logger.info("共计加载"+swLinkList.size()+"条路链数据！");
	}
	
	/**
	 * 获取路链数据的空间范围
	 * @return
	 */
	public double[] getLinksBounds() {
		double[]  result = PBGeoShapeUtil.GetShapeFileBounds(workspacePath+File.separator+shapeFileName+".shp",shapeFileCharSet);
		logger.info("link bounds [minx :"+result[0]+" miny :"+result[1]+" maxx :"+result[2]+" maxy : "+result[3]+"]");
		return result;
	}
	
	public int getGridSize() {
		return gridSize;
	}
	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}
	public String getShapeFileName() {
		return shapeFileName;
	}
	public void setShapeFileName(String shapeFileName) {
		this.shapeFileName = shapeFileName;
	}
	public String getWorkspacePath() {
		return workspacePath;
	}
	public void setWorkspacePath(String workspacePath) {
		this.workspacePath = workspacePath;
	}

	public String getTopyFileName() {
		return topyFileName;
	}

	public void setTopyFileName(String topyFileName) {
		this.topyFileName = topyFileName;
	}

	public String getShapeFileCharSet() {
		return shapeFileCharSet;
	}

	public void setShapeFileCharSet(String shapeFileCharSet) {
		this.shapeFileCharSet = shapeFileCharSet;
	}

	public boolean isExportMapGridFile() {
		return exportMapGridFile;
	}

	public void setExportMapGridFile(boolean exportMapGridFile) {
		this.exportMapGridFile = exportMapGridFile;
	}

	
}
