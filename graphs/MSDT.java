import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This source calculates MST for a directed graph 
 * for a given graph by using the methods discussed in class. 
 * It uses GraphAdjList.java, Node.java,LinkedList.java
 * 
 * @author Vamsi Katepalli 
 *
 */
public class MSDT {

	static GraphAdjList graph; //graph as adjacency list.
	static GraphNode s; // root vertex
	static long weightReduced = 0; //weight reduced as global variable
	static SortedSet<OutEdge> set = new TreeSet<OutEdge>(); // to print in sorted way.
	static long b =0; // to calculate time
	
	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * Initialize graph and call load graph method
		 * */
		graph = new GraphAdjList();
		graph.loadGraph();
		//set the root node.
		s=graph.getAdjList()[1];
		//initialize start time
		b = System.currentTimeMillis();
		/* This is the main call for the main method. It calls findMST after
		 * loading the graph*/
		findMST();
		//integer to print only 50 lines for output.
		int i = 50;
		
		/*Loop to print the output edges.*/
	      Iterator it = set.iterator();
	      while (it.hasNext()) {
	         Object element = it.next();
	         if(i>1)
	        	 System.out.println(element.toString());
	         else
	        	 break;
	         i--;
	      }
	}
	
	/**
	 * This method finds MST for the input graph.
	 * First it reduces the weight of the graph
	 * by calling  transformWeights method. Then
	 * a DFS is called. If every node is reachable from source
	 * MST is returned else there would be a zero weight
	 * cycle which needs to be shrinked to get the new MST.
	 * @return void
	 */
	static void findMST(){
		transformWeights(); 
		//perform 1st step.
		GraphNode graphNode = DFS(); 
		//second step running DFS
		if(graphNode!=null){
			graphNode = findCycle(graphNode); 
			//find the cycle.
			if(graphNode != null){ 
				//there is cycle and the node which is not reachable on 0 weight path is graphNode.
				shrinkCycle(graphNode);  
				//shrink the nodes.
				reset(); 
				// reset some parameters
				findMST(); 
				//recursive call to find MST in resultant graph.
			}
		}
		else{
			System.out.println(weightReduced+ " "+ (System.currentTimeMillis()-b));
			reset(); // reset
			DFS2(); // run DFS again and print all nodes in the 0 weight path and cycles.
			return;
		}
	}
	
	/**
	 * This method returns a graphnode which is the
	 * starting point of the cycle, and we can loop this
	 * cycle using this node.
	 * @param node
	 * @return GraphNode
	 */
	private static GraphNode findCycle(GraphNode node) {
		GraphNode temp = node.getMinEdge().u; 
		GraphNode startVertex = null;
		node.cycleChecked=true;
		while(temp != node){
			//backtracking
			if(temp.cycleChecked){
				startVertex = temp;
				break;
			}
			temp.cycleChecked = true;
			temp = temp.getMinEdge().u;
		}
		if(temp.cycleChecked){
			startVertex = temp;
		}
		//now mark the nodes which are in cycle.
		if(startVertex!=null){
			temp = startVertex.getMinEdge().u;
			while(temp!=startVertex){
				temp.inCycle = true;
				temp = temp.getMinEdge().u;
			}
			startVertex.inCycle =true;
		}
		return startVertex;
	}

	/**
	 * This method resets the node properties.
	 */
	private static void reset() {
		for(GraphNode node:graph.getAdjList()){
			if(node!=null){
				node.color=0;
				node.cycleChecked=false;
			}
		}
	}

	/**
	 * This method adds the nodes encountered in final DFS
	 * 
	 * @param root
	 */
	static void printCycleNodes(GraphNode root){
		GraphNode temp = root.getMinEdge().u;
		while(temp!=root){
			if(temp.isCycleNode)
				printCycleNodes(temp.root);
			else{
				set.add(new OutEdge(temp.oldMinEdge.u.v, temp.v));
			}
			temp = temp.getMinEdge().u;
		}
		if(temp.isCycleNode)
			printCycleNodes(temp.root);
		else
			set.add(new OutEdge(temp.oldMinEdge.u.v, temp.v));
	}
	
	
	/**
	 * This method shrinks nodes to a single node.
	 * For every new node added all the edges of old nodes
	 * are also added by preserving the old edges.
	 * 
	 * @param node
	 * @return
	 */
	private static GraphNode shrinkCycle(GraphNode node) {
		//taking care of incoming and outgoing edges.
		//create a super node here.
		GraphNode cycleNode = new GraphNode(node.v);
		graph.getAdjList()[node.v] = cycleNode;
		cycleNode.isCycleNode = true;
		GraphNode temp = node.getMinEdge().u; 
		GraphNode prev = node;
		while(temp != node){
			//backtracking
			for(Edge edge:temp.getIncomingEdges()){
				//add all edges except the one in 0 weight cycle.
				if(!edge.u.inCycle){
					graph.addRootEdge(edge.u.v, cycleNode.v, edge.w,false);
				}
				edge.ignore = true;
			}
			for(Edge edge:temp.edges){
				if(!edge.v.inCycle){
					if(edge.v.getMinEdge().u == edge.u){
						//saving old references
						//edge.v.oldMinEdge =edge.v.getMinEdge();  
						graph.addRootEdge(cycleNode.v, edge.v.v, edge.w,true);
					}
					else
						graph.addRootEdge(cycleNode.v, edge.v.v, edge.w,false);
				}
				edge.ignore = true;
			}
			//temp.inCycle=true;
			//graph.getAdjList()[temp.v] = new GraphNode(temp.v);
			prev = temp;
			temp = temp.getMinEdge().u;
		}
		//doing it for start node.
		for(Edge edge:node.getIncomingEdges()){
			//add all edges except the one in 0 weight cycle.
			if(!edge.u.inCycle){
				graph.addRootEdge(edge.u.v, cycleNode.v, edge.w,false);
			}
			edge.ignore = true;
		}
		for(Edge edge:node.edges){
			if(!edge.v.inCycle){
				//edge.v.oldMinEdge =edge.v.getMinEdge();  
				graph.addRootEdge(cycleNode.v, edge.v.v, edge.w,false);
			}
			edge.ignore = true;
		}
		
		cycleNode.root = node;
		//Now we have added all the edges to the new node in the graph.
		//done working now.
		
		
		//graph.printGraph();
		cycleNode.inCycle = false;
		return cycleNode;
	}

	/**
	 * 1. Transform weights so that every node except s has an incoming edge
   		  of weight 0:
		   for each u in V-{s} do
		      Let d_u be the weight of a minimum weight edge into u
		      for each p in V do
		         w(p,u) <-- w(p,u) - d_u
		   Reduction in weight of MST by above step = sum of d_u, over u in V-{s}
	 */
	private static void transformWeights() {
		for(GraphNode u:graph){
			//check source vertex.
			if(u == s) //skipping the root node.
				continue;
			for(Edge edge:u.getIncomingEdges()){
				if(!edge.ignore)
					edge.w = edge.w - u.getMinWeightEdge();
			}
			
			weightReduced = weightReduced + u.getMinWeightEdge();
			u.setMinWeightEdge(0); 
		}
		
	}
	

	/**
	 * @param Graph
	 * DFS algorithm.
	 */
	public static GraphNode DFS(){
		GraphNode result = DFSVisit(s);
		if(result != null){
			boolean checkAll=true;
			for(GraphNode node:graph){
				if(node.color == 0 && !node.inCycle){
					checkAll = false;
				}
			}
			if(!checkAll)
				result=graph.getAdjList()[((Edge)result.edges.getHead().next.value).v.v];
			else
				result = null;
			return result;
			
		}
		return null;
	}

	/**
	 * DFSVisit method recursive call for DFS.
	 * @param graphNode
	 * @return GraphNode
	 */
	private static GraphNode DFSVisit(GraphNode graphNode) {
		graphNode.color = 1;
		boolean isZeroWeightPath = false;
		GraphNode result = null;
		for(Edge edge:graphNode.edges){
			if(edge.v.color == 0 && edge.w == 0 && !edge.v.inCycle && !edge.ignore){ // checking nodes with weight 0.
				edge.v.parent = graphNode;
				graphNode.child = edge.v;
				isZeroWeightPath=true;
				result =  DFSVisit(edge.v);
			}
		}
		graphNode.color = 2;
		if(result!=null)
			return result;
		else if(!isZeroWeightPath)
			return graphNode;
		else
			return null;
	}
	
	
	/**
	 * @param Graph
	 * DFS algorithm.
	 */
	public static void DFS2(){
			for(GraphNode node:graph){
				if(node.color == 0 && !node.inCycle){
					DFSVisit2(s);
				}
			}
	}

	/**
	 * DFS2 method to run final DFS.
	 * @param graphNode
	 */
	private static void DFSVisit2(GraphNode graphNode) {
		graphNode.color = 1;
		if(graphNode.isCycleNode){
			printCycleNodes(graphNode.root);
		}
		for(Edge edge:graphNode.edges){
			if(edge.v.color == 0 && edge.w == 0 && !edge.v.inCycle && !edge.ignore){ // checking nodes with weight 0.
				if(!edge.u.isCycleNode){
					set.add(new OutEdge(edge.u.v, edge.v.v));
				}
				else{
					set.add(new OutEdge(edge.v.oldMinEdge.u.v, edge.v.v));
				}
				DFSVisit2(edge.v);
			}
		}
		
	}
	
	

}
