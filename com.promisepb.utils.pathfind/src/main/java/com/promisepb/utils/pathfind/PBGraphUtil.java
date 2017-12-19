/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.pathfind;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.graph.build.basic.BasicDirectedGraphBuilder;
import org.geotools.graph.build.feature.FeatureGraphGenerator;
import org.geotools.graph.build.line.LineStringGraphGenerator;
import org.geotools.graph.structure.Graph;
import org.geotools.graph.structure.Node;
import org.geotools.graph.structure.basic.BasicDirectedEdge;
import org.geotools.graph.structure.basic.BasicDirectedNode;
import org.opengis.feature.Feature;

import com.promisepb.utils.fileutils.PBFileUtil;
import com.promisepb.utils.gisutils.PBGeoShapeUtil;
import com.vividsolutions.jts.geom.Coordinate;

/**  
 * 功能描述: geotools Graph 帮助类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年11月22日 下午5:16:40  
 */
public class PBGraphUtil {

	/**
	 * 通过shape构建Graph
	 * @param shapePath
	 * @param charSet
	 * @return
	 * @throws IOException 
	 */
	public static Graph  BuildGraphByShape(String shapePath,String charSet) throws IOException {
		SimpleFeatureCollection fc = PBGeoShapeUtil.ReadShapeFileFeatures(shapePath, charSet);
		LineStringGraphGenerator lineStringGen = new LineStringGraphGenerator();
		FeatureGraphGenerator featureGen = new FeatureGraphGenerator( lineStringGen );
		FeatureIterator<?> iter = fc.features();
		try {
			while(iter.hasNext()){
				Feature feature = iter.next();
				featureGen.add( feature );
			}
		} finally {
			iter.close();
		}
		Graph graph = featureGen.getGraph();
	    return graph;
	}
	
	/**
	 * 根据四维生成的拓扑文件构建Graph
	 * 调用PBTopyUtil createSWTopyFile可以生成文件
	 * 15265978#2#0.145#116.66367647999999:40.211021880000004#116.66538197999999:40.210978950000005#636-259:637-259#12587447#408100
	 * @param filePath
	 * @param charSet
	 * @return
	 */
	public static Graph BuildGraphBySWTopyFile(String filePath,String charSet) {
		List<String> list = PBFileUtil.ReadCSVFile(filePath, charSet);
		//格式：id#方向#长度#起点x:起点y#终点x:终点y#网格x:网格y:......#snode#enode
		BasicDirectedGraphBuilder builder = new BasicDirectedGraphBuilder();
		Map<String,Node> nodeMap = new HashMap<String,Node>();
		int idIndex = 1;
		int edgeIndex=1;
		for(String strTemp : list) {
			String[] strArrTemp = strTemp.split("#");
			String id = strArrTemp[0];
			String fx = strArrTemp[1];
			double length = Double.valueOf(strArrTemp[2]);
			String[] startXY = strArrTemp[3].split(":");
			double startX = Double.valueOf(startXY[0]);
			double startY = Double.valueOf(startXY[1]);
			Coordinate spoint = new Coordinate(startX,startY);
			String[] endXY = strArrTemp[4].split(":");
			double endX = Double.valueOf(endXY[0]);
			double endY = Double.valueOf(endXY[1]);
			Coordinate epoint = new Coordinate(endX,endY);
			String gridIndexs = strArrTemp[5];
			String snode = strArrTemp[6];
			String enode = strArrTemp[7];
			BasicDirectedNode optNode1 = null;
			BasicDirectedNode optNode2 = null;
			if(null==nodeMap.get(snode)) {
				optNode1 = new BasicDirectedNode();
				optNode1.setID(idIndex);
				optNode1.setObject(spoint);
				builder.addNode(optNode1);
				nodeMap.put(snode, optNode1);
				idIndex++;
			}else {
				optNode1 = (BasicDirectedNode)nodeMap.get(snode);
			}
			if(null==nodeMap.get(enode)) {
				optNode2 = new BasicDirectedNode();
				optNode2.setID(idIndex);
				optNode2.setObject(epoint);
				builder.addNode(optNode2);
				nodeMap.put(enode, optNode2);
				idIndex++;
			}else {
				optNode2 = (BasicDirectedNode)nodeMap.get(enode);
			}
			//方向 0:未调查(默认为双向)  1:双向  2:顺方向(单向通行，通行方向为起点到终点方向) 3:逆方向(单向通行，通行方向为终点到起点方向)
			if(fx.trim().equals("0")||fx.trim().equals("1")) {
				BasicDirectedEdge directedEdge1 = new BasicDirectedEdge(optNode1,optNode2);
				directedEdge1.setObject(strTemp);
				BasicDirectedEdge directedEdge2 = new BasicDirectedEdge(optNode2,optNode1);
				directedEdge1.setID(edgeIndex);
				builder.addEdge(directedEdge1);
				edgeIndex++;
				directedEdge2.setID(edgeIndex);
				directedEdge2.setObject(strTemp);
				builder.addEdge(directedEdge2);
				edgeIndex++;
			}else if(fx.trim().equals("2")) {
				BasicDirectedEdge directedEdge3 = new BasicDirectedEdge(optNode1,optNode2);
				directedEdge3.setID(edgeIndex);
				directedEdge3.setObject(strTemp);
				builder.addEdge(directedEdge3);
				edgeIndex++;
			}else if(fx.trim().equals("3")) {
				BasicDirectedEdge directedEdge4 = new BasicDirectedEdge(optNode2,optNode1);
				directedEdge4.setID(edgeIndex);
				directedEdge4.setObject(strTemp);
				builder.addEdge(directedEdge4);
				edgeIndex++;
			}
		}
		return builder.getGraph();
	}
}
