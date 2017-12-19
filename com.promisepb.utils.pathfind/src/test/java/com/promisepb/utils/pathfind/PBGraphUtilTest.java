/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.utils.pathfind;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.geotools.graph.path.AStarShortestPathFinder;
import org.geotools.graph.path.DijkstraShortestPathFinder;
import org.geotools.graph.path.Path;
import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Graph;
import org.geotools.graph.structure.Node;
import org.geotools.graph.structure.basic.BasicDirectedEdge;
import org.geotools.graph.structure.basic.BasicDirectedNode;
import org.geotools.graph.traverse.standard.AStarIterator;
import org.geotools.graph.traverse.standard.DijkstraIterator;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年11月22日 下午5:26:32  
 */
public class PBGraphUtilTest {

	@Test
	public void testBuildGraphByShape() {
		String shapePath = "F:\\toccworkspace\\lpfgps\\all_road.shp";
		try {
			Graph graph = PBGraphUtil.BuildGraphByShape(shapePath, "GBK");
			DijkstraIterator.EdgeWeighter weighter = new DijkstraIterator.EdgeWeighter(){
			    @Override
			    public double getWeight(Edge edge) {
			    	//这个方法返回的值就是权重，这里使用的最简单的线的长度
			    	//如果有路况、限速等信息，可以做的更复杂一些
			        SimpleFeature feature = (SimpleFeature)edge.getObject();
			        Geometry geometry = (Geometry)feature.getDefaultGeometry();
			        return geometry.getLength();
			    }
			};
			Date startT = new Date();
			Iterator<Node>  iter = graph.getNodes().iterator();
			List<Node> list = new ArrayList<Node>();
			while(iter.hasNext()) {
				list.add(iter.next());
			}
			//初始化查找器
			DijkstraShortestPathFinder pf = new DijkstraShortestPathFinder(graph,list.get(0),weighter);
			pf.calculate();
			//传入终点，得到最短路径
			Path path = pf.getPath(list.get(10));
			Date end = new Date();
			System.out.println("迪杰斯特拉算法耗时：" +(end.getTime() - startT.getTime()));
			System.out.println(list.get(10).getID()+"----"+list.get(0).getID());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testBuildGraphByShape1() {
		String filePath = "F:\\toccworkspace\\pathfind\\bj_topy.csv";
		try {
			Graph graph = PBGraphUtil.BuildGraphBySWTopyFile(filePath, "GBK");
			DijkstraIterator.EdgeWeighter weighter = new DijkstraIterator.EdgeWeighter(){
			    @Override
			    public double getWeight(Edge edge) {
			    	String[] resultTemp = edge.getObject().toString().split("#");
			        return Double.valueOf(resultTemp[2]);
			    }
			};
			Date startT = new Date();
			Iterator<BasicDirectedNode>  iter = graph.getNodes().iterator();
			List<BasicDirectedNode> list = new ArrayList<BasicDirectedNode>();
			while(iter.hasNext()) {
				list.add(iter.next());
			}
			BasicDirectedNode sNode = (BasicDirectedNode)list.get(0);
			BasicDirectedNode eNode = (BasicDirectedNode)list.get(2);
			//初始化查找器
			DijkstraShortestPathFinder pf = new DijkstraShortestPathFinder(graph,sNode,weighter);
			pf.calculate();
			//传入终点，得到最短路径
			Path path = pf.getPath(eNode);
			List<BasicDirectedEdge>  pathList = path.getEdges();
			for(BasicDirectedEdge edge : pathList) {
				String resultTemp = edge.getObject().toString();
				System.out.println("'"+resultTemp.split("#")[0]+"',");
			}
			Date end = new Date();
			System.out.println("迪杰斯特拉算法耗时：" +(end.getTime() - startT.getTime()));
			BasicDirectedEdge sbe = (BasicDirectedEdge)(sNode.getEdges().get(0));
			BasicDirectedEdge ebe = (BasicDirectedEdge)(eNode.getEdges().get(0));
			System.out.println(sbe.getObject());
			System.out.println(ebe.getObject());
			System.out.println("succcess");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testBuildGraphByShape2() {
		String filePath = "F:\\toccworkspace\\pathfind\\bj_topy.csv";
		try {
			Graph graph = PBGraphUtil.BuildGraphBySWTopyFile(filePath, "GBK");
			Date startT = new Date();
			Iterator<BasicDirectedNode>  iter = graph.getNodes().iterator();
			List<BasicDirectedNode> list = new ArrayList<BasicDirectedNode>();
			while(iter.hasNext()) {
				list.add(iter.next());
			}
			BasicDirectedNode sNode = (BasicDirectedNode)list.get(0);
			BasicDirectedNode eNode = (BasicDirectedNode)list.get(2);
			AStarIterator.AStarFunctions aStarFunction = new  AStarIterator.AStarFunctions(eNode){
			        @Override
			        public double cost(AStarIterator.AStarNode aStarNode, AStarIterator.AStarNode aStarNode1) {
			           Edge edge = aStarNode.getNode().getEdge(aStarNode1.getNode());
			           String[] resultTemp = edge.getObject().toString().split("#");
				       return Double.valueOf(resultTemp[2]);
			        }

			        @Override
			        public double h(Node node) {
			            return -10;
			        }
			    };
			//初始化查找器
			AStarShortestPathFinder pf = new AStarShortestPathFinder(graph,sNode,eNode,aStarFunction);
			pf.calculate();
			//传入终点，得到最短路径
			pf.calculate();
			List<BasicDirectedEdge>  pathList = pf.getPath().getEdges();
			for(BasicDirectedEdge edge : pathList) {
				String resultTemp = edge.getObject().toString();
				System.out.println("'"+resultTemp.split("#")[0]+"',");
			}
			Date end = new Date();
			System.out.println("A*算法耗时：" +(end.getTime() - startT.getTime()));
			BasicDirectedEdge sbe = (BasicDirectedEdge)(sNode.getEdges().get(0));
			BasicDirectedEdge ebe = (BasicDirectedEdge)(eNode.getEdges().get(0));
			System.out.println(sbe.getObject());
			System.out.println(ebe.getObject());
			System.out.println("succcess");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
