
/**
 * @author Vamsi Katepalli
 * This is the driver class which starts the process
 * to find number of shortest paths from source to d
 * estination
 *
 */
public class NumberOfSPaths {
	/**
	 * queue of nodes initially empty.
	 */
	private static Queue<GraphNode> queue;   
	/**
	 * check for negative cycle
	 */
	private static boolean isNegCycle;

	/**
	 * main method of the project
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception{
		GraphAdjList graph = new GraphAdjList();
		/**
		 * load graph with input stream
		 */
		graph.loadGraph();
		/**
		 * initialize queue
		 */
		queue = new Queue<GraphNode>();
		/**
		 * initialize graph with property d as infinity and for source vertex 0.
		 */
		init(graph);
		/**
		 * Main method to find shortest paths using bellman ford algorithm
		 */
		long b = System.currentTimeMillis();
		findShortestPaths(graph);
		long t = System.currentTimeMillis()-b;
		/**
		 * print the paths discovered in above call.
		 */
		printPaths(graph, graph.getAdjList()[graph.getS()], graph.getAdjList()[graph.getT()],t);
	}

	/**
	 * find shortest paths of the graph. 
	 * Bellman-Ford algorithm improved version.
	 * 
	 * @param graph
	 * @throws Exception 
	 */
	private static void findShortestPaths(GraphAdjList graph) throws Exception {
		/**
		 * insert root in to queue.
		 */
		queue.enqueue(graph.getAdjList()[graph.getS()]);
		graph.getAdjList()[graph.getS()].inQ = true;
		/**
		 * while loop until Q is empty
		 */
		while(!queue.isEmpty()){
			GraphNode u = queue.dequeue();
			u.inQ = false;
			u.count = u.count + 1;
			if(u.count >= graph.getV()){
				isNegCycle=true;
				break;
			}else if(u.zeroCount >= graph.getV()){
				isNegCycle = true;
				break;
			}
			else{
				for(Edge edge:u.edges){
					relax(edge,graph);
				}
			}
		}
	}
	
	/**
	 * This method relaxes edges. Also when edge.v.d == edge.u.d + edge.w
	 * ie node is visited again, we increase the count by updating with 
	 * parent + current value.
	 * @param edge
	 */
	private static void relax(Edge edge,GraphAdjList graph){
		if(edge.u.d != Long.MAX_VALUE && edge.v.d > edge.u.d + edge.w){
			edge.v.d = edge.u.d + edge.w;
			edge.v.parent = edge.u;
			if(!edge.v.inQ){
				queue.enqueue(edge.v);
				edge.v.inQ = true;
			}
			edge.v.pathCount = edge.u.pathCount;
		}else if(edge.v.d == edge.u.d + edge.w){
			edge.v.pathCount = edge.v.pathCount + edge.u.pathCount;
			edge.v.zeroCount = edge.v.zeroCount + 1;
			if(!edge.v.inQ){
				queue.enqueue(edge.v);
				edge.v.inQ = true;
			}
			
		}
	}
	
	
	/**
	 * Print paths in graph
	 * @param graph
	 * @param s
	 * @param v
	 */
	private static void printPaths(GraphAdjList graph,GraphNode s,GraphNode v,long t){
		if(isNegCycle){
			System.out.println("Non-positive cycle in graph.  DAC is not applicable");
			return;
		}
		System.out.println(v.d+" "+v.pathCount+" "+t);
		if(graph.getV() <=100){
			for(GraphNode node:graph){
				if(node.d == Long.MAX_VALUE)
					System.out.println(node.v+" "+ "INF"+" "+"-"+ " "+ node.pathCount);
				else{
					if(node.parent==null)
						System.out.println(node.v+" "+ node.d+" "+"-"+ " "+ node.pathCount);
					else
						System.out.println(node.v+" "+ node.d+" "+node.parent+ " "+ node.pathCount);
				}
			}
		}
		
	}
	/**
	* initialize the graph.
	*/
	private static void init(GraphAdjList graph){
		for(GraphNode node:graph){
			node.d=Long.MAX_VALUE;
			node.parent=null;
		}
		graph.getAdjList()[graph.getS()].d = 0;
		graph.getAdjList()[graph.getS()].pathCount = 1;
	}
	
}
