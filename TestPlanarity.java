/*
 * TestPlanarity.java
 * 
 * Version:
 *      $Id$
 *      
 * Revisions:
 *      $Log$
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This class is responsible for running the planarity testing algorithm, using the
 * Graph class and the utility methods (finding a cycle, etc.).
 * 
 * @author Matthew McCarty
 */
public class TestPlanarity {

    /**
     * The main planarity testing algorithm.
     * 
     * @param args	The filename containing the graph information
     */
    public static void main(String[] args) {
	try {
	    BufferedReader inputStream = 
	        new BufferedReader(new FileReader(args[0]));
	    String line;
            Graph graph = new Graph();
			
	    // Read graph info from file and construct graph.
	    while((line = inputStream.readLine()) != null) {
		String[] vertices = line.split(" ");
				
		try {
		    graph.addEdge(Integer.parseInt(vertices[0]), 
			Integer.parseInt(vertices[1]));
		} catch(NumberFormatException ex) { 
                    break; 
                }
	    }
			
	    boolean done = false;
			
	    for(Integer vertex : graph.getVertices()) {
		// Find a cycle and compute its pieces.
		// If more than 1 piece, continue (separating cycle)
		List<Integer> cycle = GraphUtilities.FindCycle(graph, vertex);
				
		Graph copy = new Graph(graph);
				
		if(cycle == null) {
		    throw new Exception("Not biconnected.");
		}
				
		List<Graph> pieces = GraphUtilities.FindPieces(copy, cycle);

		if(pieces.size() > 0) {
					
		    // Check to see if there are more than 3n-6 edges
		    if((3 * graph.numberOfVertices() - 6) < graph.numberOfEdges()) {
			System.out.println("nonplanar");
			done = true;
			break;
		    }
					
		    boolean planar = PlanarityTesting(graph, cycle);
		    if(planar) {
			System.out.println("planar");
		    } else {
			System.out.println("nonplanar");
		    }
		    done = true;
		    break;
		}
	    }
			
	    // In case of K3 which has no separating cycle but is biconnected
	    // Also necessary for any cycle
	    if(!done) {
		System.out.println("planar");
	    }
			
	} catch(FileNotFoundException ex) {
       	    System.err.println("Filename: " + args[0] + " not found");
	} catch(IOException ex) {
	    System.err.println("I/O exception in reading file.");
	} catch(Exception ex) {
	    System.err.println(ex.getMessage());
        }
    }
	
    /**
     * The main planarity testing algorithm.  The algorithm computes the 
     * pieces of a cycle and recursively calls this function to determine
     * planarity.  It also checks the interlacement graph to determine
     * nonplanarity.  
     * 
     * @param graph	The given graph
     * @param cycle The given cycle
     * @return	True if planar, false otherwise
     */
    public static boolean PlanarityTesting(Graph graph, List<Integer> cycle) {
	if((3 * graph.numberOfVertices() - 6) < graph.numberOfEdges()) {
	    return false;
	}
		
	Graph copy = new Graph(graph);
        List<Graph> pieces = GraphUtilities.FindPieces(copy, cycle);
		
	// Deep copy the pieces list due to Java's handling of references
	List<Graph> piecesCopy = new LinkedList<Graph>();
	for( Graph g : pieces ) {
	    piecesCopy.add(new Graph(g));
	}
		
	//For each non-piece path, recursively call this algorithm
	for(Graph piece : pieces) {
	    if(!piece.IsPath()) {
				
		// Get two consecutive attachments
		List<Integer> adjAttach = new LinkedList<Integer>();
		Set<Integer> otherAttach = new HashSet<Integer>();
				
		for(int i = 0; i < cycle.size(); ++i) {
		    Integer attachVertex = cycle.get(i);
		    if(piece.containsVertex(attachVertex)) {
			if(adjAttach.size() < 2) {
			    adjAttach.add(attachVertex);
			} else {
			    otherAttach.add(attachVertex);
			}
		    }
		}
				
		List<Integer> cycleCopy1 = new LinkedList<Integer>(cycle);
		List<Integer> cycleCopy2 = new LinkedList<Integer>(cycle);
				
		//Find path between consecutive attachments through piece
		List<Integer> pPath = GraphUtilities.FindPath(piece, 
		    adjAttach.get(0), adjAttach.get(1), otherAttach);
		List<Integer> cPath = 
		    cycleCopy1.subList(cycleCopy1.indexOf(adjAttach.get(1)),
				       cycleCopy1.size());
		cPath.addAll(cycleCopy2.subList(0, 
                    cycleCopy2.indexOf(adjAttach.get(0))));
				
		if(cPath.get(0) == pPath.get(0)) {
		    Collections.reverse(cPath);
		}
		pPath.remove(pPath.size()-1);
		pPath.addAll(cPath);
	
				
		// Add cycle to graph
		for(int i = 0; i < cycle.size() - 1; ++i) {	
		    piece.addEdge(cycle.get(i), cycle.get(i+1));
		}
		piece.addEdge(cycle.get(0), cycle.get(cycle.size()-1));
				
		if(PlanarityTesting(piece, pPath) == false) {
		    return false;
		}
	    }	
	}
				
	// Compute interlacement graph.
	Graph interlace = GraphUtilities.MakeInterlacementGraph(piecesCopy, cycle);	
		
	// Determine if it's bipartite, if not return nonplanar
	if(interlace.numberOfVertices() > 0 &&
	   !GraphUtilities.IsBipartite(interlace, interlace.getVertex())) {
	    return false;
	}	
				
	return true;
    }

}
