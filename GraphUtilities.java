
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * This class is a collection of useful algorithms used by the planarity
 * testing algorithm.  
 * 
 * @author Matthew McCarty
 */

public class GraphUtilities {
	
    /**
     * This function finds a cycle given a start vertex.  
     * Uses depth-first search.
     * 
     * @param graph		The given graph.
     * @param vertex	The start vertex for the cycle.
     * @return			A hash map representing the parents of the 
     * 					vertices in the DFS tree.
     */
    public static List<Integer> FindCycle(Graph graph, Integer vertex) {
	Stack<Integer> seenVertices = new Stack<Integer>();
	List<Integer> explored = new LinkedList<Integer>();
	HashMap<Integer,Integer> parent = new HashMap<Integer,Integer>();
		
	seenVertices.add(vertex);
		
	// Run DFS.
	while(!seenVertices.empty()) {
	    Integer currentVertex = seenVertices.pop();
	    if(!explored.contains(currentVertex)) {
		explored.add(currentVertex);
		for(Integer adjVertex: graph.getNeighbors(currentVertex)) {
		    seenVertices.push(adjVertex);
					
		    if(!explored.contains(adjVertex) || adjVertex.equals(vertex)) {
			parent.put(adjVertex, currentVertex);
		    }
					
		    // If we return to the start vertex, return.
		    // The cycle length must be greater than 2.
		    if(adjVertex.equals(vertex) && explored.size() > 2) { 
			Integer current = vertex;
			boolean pathDone = false;
			List<Integer> cycle = new LinkedList<Integer>();
						
			// Construct the cycle as a list from the parent structure.
			while(!pathDone) {
			    cycle.add(current);
			    current = parent.get(current);
			    if(current.equals(vertex)) {
				pathDone = true;
			    }
			}
						
			return cycle;	
		    }
		}
	    }
	}
		
	return null;	
    }
	 
    /**
     * Find a path between two vertices.  Used in recursive step to find
     * a path in a piece between two attachment vertices without going 
     * through any other attachments.
     * 
     * @param graph		    The graph containing 2 vertices
     * @param vertex1   	The first vertex
     * @param vertex2		The second vertex
     * @param otherAttach	The attachment vertices (to avoid going through)
     * @return				The path of vertices, starting with vertex1
     */
    public static List<Integer> FindPath(Graph graph,
					 Integer vertex1,
					 Integer vertex2,
					 Set<Integer> otherAttach) {
	Stack<Integer> seenVertices = new Stack<Integer>();
	List<Integer> explored = new LinkedList<Integer>();
	HashMap<Integer,Integer> parent = new HashMap<Integer,Integer>();
		
	seenVertices.add(vertex1);
		
	// Run DFS.
	while(!seenVertices.empty()) {
	    Integer currentVertex = seenVertices.pop();
	    if(!explored.contains(currentVertex) && 
	       !otherAttach.contains(currentVertex)) { 
		explored.add(currentVertex);
		for(Integer adjVertex: graph.getNeighbors(currentVertex)) {
		    seenVertices.push(adjVertex);
					
		    if(!explored.contains(adjVertex) && 
		       !otherAttach.contains(adjVertex)) {   
			parent.put(adjVertex, currentVertex);
		    }
					
		    // If we find the last vertex, return
		    if(adjVertex.equals(vertex2)) { 
			Integer current = vertex2;
			boolean pathDone = false;
			List<Integer> cycle = new LinkedList<Integer>();
						
			// Create the cycle as a list from the parent structure
			while(!pathDone) {
			    cycle.add(current);
			    current = parent.get(current);
			    if(current.equals(vertex1)) {
				pathDone = true;
			    }
			}
			// Add the first vertex and reverse the list.
			cycle.add(current);
			Collections.reverse(cycle);
			return cycle;	
		    }
		}
	    }
	}
		
	// If no cycle found, return null
	return null;	
    }
	
    /**
     * This functions determines whether a graph is bipartite or not.  It
     * does so by using BFS and gives the vertices a 'color' depending on 
     * the layer (even layers are given 'false', odd layers are given 
     * 'true').  Once BFS is done, the algorithm goes through all of the
     * edges to make sure no two neighboring vertices have the same color.
     * 
     * @param graph		The given graph
     * @param vertex	The specified start vertex
     * @return			True if graph bipartite, false o/w
     */
    public static boolean IsBipartite(Graph graph, Integer vertex) {
		
	Set<Integer> vertices = graph.getVertices();
	Integer v = vertex;

	while(true) {
	    Set<Integer> discovered = new HashSet<Integer>();
	    HashMap<Integer,Boolean> color = new HashMap<Integer,Boolean>();
	    HashMap<Integer,List<Integer>> layers = 
		new HashMap<Integer,List<Integer>>();
	    List<Integer> layer1 = new LinkedList<Integer>();
	    layer1.add(v);
	    discovered.add(v);
	    layers.put(new Integer(0), layer1);
	    color.put(v, true);
	    int layerCounter = 0;
			
			
	    // Run BFS and assign colors.
	    while(!layers.get(layerCounter).isEmpty()) {
		List<Integer> layer = new LinkedList<Integer>();
				
		for(Integer currentVertex : layers.get(layerCounter)) {
		    for(Integer neighbor : graph.getNeighbors(currentVertex)) {
								
			if(!discovered.contains(neighbor)) {
			    discovered.add(neighbor);
			    layer.add(neighbor);
			    if(layerCounter % 2 == 0) {
				color.put(neighbor, false);
			    } else {
				color.put(neighbor, true);
			    }
			}
		    }
		}

		layerCounter++;
		layers.put(layerCounter, layer);
	    }
			
	    // Check if it is bipartite, i.e., the colors never are the same
	    // for adjacent vertices.
	    for(Integer currentVertex : graph.getVertices()) {
		for(Integer neighbor : graph.getNeighbors(currentVertex)) {
		    if(color.get(currentVertex) == color.get(neighbor) &&
			(color.containsKey(currentVertex) && 
			 color.containsKey(neighbor))) {
		        return false;
		    }
		}
	    }
	    vertices.removeAll(color.keySet());
	    if(vertices.isEmpty()) {
		break;
	    } else {
		v = vertices.iterator().next();
	    }
	}

	return true;
    }
	
    /**
     * This function makes the interlacement graph by considering every pair
     * of pieces and testing whether they interlace.  This is done by 'walking'
     * around the cycle and counting the number of alternations of attachments.
     * 
     * @param pieces  The pieces with respect to the cycle
     * @param cycle	  The given separating cycle
     * @return		  The interlacement graph
     */
    public static Graph MakeInterlacementGraph(List<Graph> pieces, 
											List<Integer> cycle) {
	Graph interlace = new Graph();
		
	// For every two pieces, find out whether they interlace.
	for(int i = 0; i < pieces.size(); ++i) {
	    for(int j = i+1; j < pieces.size(); ++j) {
				
		// Get pieces and sets of attachment vertices.
	        Graph piece1 = pieces.get(i);
	        Graph piece2 = pieces.get(j);
		Set<Integer> attach1 = piece1.getVertices();
		attach1.retainAll(cycle);
	        Set<Integer> attach2 = piece2.getVertices();
		attach2.retainAll(cycle);
				
		// Necessary counters and booleans.
		int alternate = 0;
		int numberOfBoth = 0;
	        boolean seen1 = false;
		boolean seen2 = false;
				
		// While we aren't done traversing the cycle
		for(int lc = 0; lc < cycle.size(); ++lc) {
					
		    // Visit the next vertex
		    Integer cycleVertex = cycle.get(lc);
					
	      	    // Case 1:  Both pieces attach here
		    if(attach1.contains(cycleVertex) && 
		       attach2.contains(cycleVertex)) {
						
			// If no attach vertex seen yet, set 
			// both to 'seen' and don't increment 
			// alternations counter
			if(!seen1 && !seen2) {
			    seen1 = seen2 = true;
							
			    // If only seen 'Both' attachment vertices so
			    // far, just increment # of alternations
			} else if(seen1 && seen2) {
			    ++alternate;
							
			    // If last seen attachment is just first piece,
			    // increment # of alternations and set the 'seen' 
			    // booleans.
			} else if(seen1 && !seen2) {
			    ++alternate;
			    seen1 = false;
			    seen2 = true;
							
			    // If last seen attachment is just first piece,
			    // increment # of alternations and set the 'seen' 
			    // booleans.
			} else if(!seen1 && seen2) {
			    ++alternate;
			    seen1 = true;
			    seen2 = false;
			}
						
			// Increment # of both attachments counter
			++numberOfBoth;
		    } 
		    // Case 2: Only first piece attaches here 
		    else if(attach1.contains(cycleVertex) && 
			    !attach2.contains(cycleVertex)) {
						
			// If last seen piece 2, increment alternations counter
			if(seen2) {
			    ++alternate;
			}
						
			// Set the appropriate booleans.
			seen1 = true;
			seen2 = false;
		    }
		    // Case 3: Only second piece attaches here
		    else if(!attach1.contains(cycleVertex) && 
			    attach2.contains(cycleVertex)) {
						
			// If last seen piece 1, increment alternations counter
			if(seen1) {
			    ++alternate;
			}
						
			// Set the appropriate booleans.
			seen1 = false;
			seen2 = true;
		    }
		    // If the pieces interlace, add edge to graph
		    if(alternate >= 3 || numberOfBoth >= 3) {
			interlace.addEdge(i, j);
			break;
		    }
		}
	    }
	}
	return interlace;
    }
	
    /**
     * This function takes a graph and a cycle and returns the pieces
     * according to the cycle.  For the non-chord pieces, the function
     * uses a modified DFS that stops when it hits any vertex on the cycle.
     * For chordal pieces, the graph simply checks each pair of vertices to
     * see if a non-cycle edge exists between them.
     * 
     * Note:  Due to the way Java handles objects and argument passing, 
     * you must make a copy of the graph before the function is called,
     * since the algorithm modifies the graph.
     * 
     * @param graph	The given graph.
     * @param cycle
     * @return
     */
    public static List<Graph> FindPieces(Graph graph, List<Integer> cycle) {
	List<Graph> pieces = new LinkedList<Graph>();

	// Check each pair of vertices for edges for chord pieces.
	for(int i = 0; i < cycle.size(); ++i) {
	    for(int j = 0; j < cycle.size(); ++j) {
		Integer vertex1 = cycle.get(i);
		Integer vertex2 = cycle.get(j);
				
		if(!vertex1.equals(vertex2) && 
		    graph.containsEdge(vertex1, vertex2) &&
					Math.abs(i - j) != 1 &&
					Math.abs(i - j) != cycle.size() - 1) {
					
		    Graph chord = new Graph();
		    chord.addEdge(vertex1, vertex2);
		    graph.removeEdge(vertex1, vertex2);
		    pieces.add(chord);
		}
	    }
			
	}

	Set<Integer> pieceVertices = new HashSet<Integer>(graph.getVertices());
	    pieceVertices.removeAll(cycle);

	// While there are still vertices left in V-C
	while(!pieceVertices.isEmpty()) {
	    Object[] pvArray = pieceVertices.toArray();
	    Graph piece = new Graph();
	    Integer vertex = (Integer) pvArray[0];
	    Set<Integer> explored = new HashSet<Integer>();
	    Stack<Integer> seen = new Stack<Integer>();
	    seen.push(vertex);
			
	    // Create the non-chord piece graphs (uses DFS)
	    while(!seen.isEmpty()) {
		Integer u = seen.pop();
		if(!explored.contains(u)) {
		    explored.add(u);
		    for(Integer neighbor : graph.getNeighbors(u)) {
			piece.addEdge(u, neighbor);
			if(!cycle.contains(neighbor)) {
			    seen.push(neighbor);
			}
		    }
					
		}
	    }
	    // Add the non-chord piece to the list
	    pieces.add(piece);
			
	    // Remove the vertices needed to search for non-chord pieces.
	    pieceVertices.removeAll(piece.getVertices());
	}
	return pieces;
    }  
}
