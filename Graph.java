/*
 * Graph.java
 * 
 * Version:
 *     $Id$
 *     
 * Revisions:
 *     $Log$
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is responsible for the representation of a graph by using 
 * an adjacency list.  The vertices themselves are not stored anywhere,
 * since they are easily obtained from the adjacency list.  The class
 * also includes a variety of useful functions necessary for the 
 * GraphUtilities algorithms.
 * 
 * @author Matthew McCarty
 */

public class Graph {
    private HashMap<Integer,Set<Integer>> adjList;
    private int edgeCount;
	
    /**
     * The default constructor.
     */
    public Graph() {
	adjList = new HashMap<Integer,Set<Integer>>();
	edgeCount = 0;
    }
	
    public Graph(Graph other) {
        adjList = (HashMap<Integer,Set<Integer>>) other.adjList.clone();
	edgeCount = other.edgeCount;
    }
	
    /**
     * Adds an edge to the adjacency list.  Since the graph is undirected,
     * this function adds two entries to the adjacency list for both
     * directions.
     * 
     * @param vertex1	The first vertex.
     * @param vertex2	The second vertex.
     */
    public void addEdge(Integer vertex1, Integer vertex2) {
		
	// Add first directed edge.
	if(adjList.containsKey(vertex1)) {
	    if(!adjList.get(vertex1).add(vertex2)) {
		--edgeCount;
	    }
	}
	else {
	    Set<Integer> v1List = new HashSet<Integer>();
	    v1List.add(vertex2);
	    adjList.put(vertex1, v1List);
	}
		
	// Add second directed edge.
	if(adjList.containsKey(vertex2)) {
	    adjList.get(vertex2).add(vertex1);
	}
	else {
	    Set<Integer> v2List = new HashSet<Integer>();
	    v2List.add(vertex1);
	    adjList.put(vertex2, v2List);
	}
		
	// Increment edge count.
	++edgeCount;
    }
	
    /**
     * Removes an edge from the graph.
     * 
     * @param vertex1	The first vertex
     * @param vertex2	The second vertex
     */
    public void removeEdge(Integer vertex1, Integer vertex2) {
	adjList.get(vertex1).remove(vertex2);
	adjList.get(vertex2).remove(vertex1);
	--edgeCount;
    }
	
    /**
     * A diagnostic method to inspect adjacency list data structure.
     */
    public void printEdges() {
	for(Integer key: adjList.keySet()) {
	    for(Integer secondVertex: adjList.get(key)){
		System.out.println(key + "-> " + secondVertex);
	    }
	}
    }
	
    /**
     * Returns the neighboring vertices of the given vertex.
     * 
     * @param vertex  The vertex in question
     * @return	      The neighboring vertices
     */
    public Set<Integer> getNeighbors(Integer vertex) {
	return adjList.get(vertex);
    }
	
    /**
     * Returns a vertex from the graph.  Useful for getting a start node for
     * finding a cycle, etc.
     * 
     * @return	A vertex from the graph.
     */
    public Integer getVertex() {
	return adjList.keySet().iterator().next();
    }
	
    /**
     * Gets a set containing the vertices.
     * 
     * @return	The set containing all the vertices in this graph.
     */
    public Set<Integer> getVertices() {
	return adjList.keySet();
    }
	
    /**
     * Determines whether an edge exists between two vertices.
     * 
     * @param vertex1  The first vertex
     * @param vertex2  The second vertex
     * @return	       True if an edge exists between the two vertices, false o/w.
     */
    public boolean containsEdge(Integer vertex1, Integer vertex2) {
	Set<Integer> n;
	if((n = adjList.get(vertex1)) != null) {
	    return n.contains(vertex2);
	} else {
	    return false;
	}
    }
	
    /**
     * Determines whether the vertex is in the graph.
     * 
     * @param vertex	The given vertex
     * @return		True if graph contains vertex, false o/w
     */
    public boolean containsVertex(Integer vertex) {
	return adjList.containsKey(vertex);
    }
	
    /**
     * Returns the number of vertices are in the graph.
     * 
     * @return	The number of vertices in the graph
     */
    public int numberOfVertices() {
	return adjList.keySet().size();
    }
	
    /**
     * Returns the number of edges in the graph.
     * 
     * @return	The number of edges in the graph
     */
    public int numberOfEdges() {
    	return edgeCount;
    }
	
    /**
     * Determines whether a graph is a path or not. Notice that
     * a cycle is a special type of s-t path where s=t, so this 
     * algorithm will return true if given a cycle.
     * 
     * @return	True if graph is a path, false o/w
     */
    public boolean IsPath() {
	for(Integer vertex : adjList.keySet()) {
	    if(adjList.get(vertex).size() > 2) {
		return false;
	    }
	}
	return true;
    }
}
