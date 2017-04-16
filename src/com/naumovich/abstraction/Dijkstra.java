package com.naumovich.abstraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.naumovich.entity.Edge;
import com.naumovich.entity.NodeThread;
import com.naumovich.network.Field;

public class Dijkstra {
	
	  private static List<NodeThread> nodes;
	  private static List<Edge> edges;
	  private Set<NodeThread> settledNodes;
	  private Set<NodeThread> unSettledNodes;
	  private Map<NodeThread, NodeThread> predecessors;
	  private Map<NodeThread, Double> distance;
	  private static ArrayList<ArrayList<Integer>> edgesMatrix;

	  public Dijkstra() {
		  nodes = Field.getNodes();
		  edgesMatrix = Field.getEdgesMatrix();
		  edges = resolveEdges();
	  }
	  public static List<Edge> resolveEdges() {
		  List<Edge> allEdges = new ArrayList<Edge>();
		  for (int i = 0 ; i < Field.NNUM - 1; i++) {
			  for (int j = i + 1; j < Field.NNUM; j++) {
				  if (edgesMatrix.get(i).get(j) == 1) {
					 allEdges.add(new Edge(nodes.get(i), nodes.get(j)));
				  }
			  }
		  }
		  return allEdges;
	  }
	  public void execute(NodeThread source) {
	    settledNodes = new HashSet<NodeThread>();
	    unSettledNodes = new HashSet<NodeThread>();
	    distance = new HashMap<NodeThread, Double>();
	    predecessors = new HashMap<NodeThread, NodeThread>();
	    distance.put(source, 0d);
	    unSettledNodes.add(source);
	    while (unSettledNodes.size() > 0) {
	      NodeThread node = getMinimum(unSettledNodes);
	      settledNodes.add(node);
	      unSettledNodes.remove(node);
	      findMinimalDistances(node);
	    }
	  }

	  private void findMinimalDistances(NodeThread node) {
	    List<NodeThread> adjacentNodes = getNeighbors(node);
	    for (NodeThread target : adjacentNodes) {
	      if (getShortestDistance(target) > getShortestDistance(node)
	          + getDistance(node, target)) {
	        distance.put(target, getShortestDistance(node)
	            + getDistance(node, target));
	        predecessors.put(target, node);
	        unSettledNodes.add(target);
	      }
	    }

	  }

	  private double getDistance(NodeThread node, NodeThread target) {
	    for (Edge edge : edges) {
	      if (edge.getLeft().equals(node) && edge.getRight().equals(target) ||
	    		  edge.getRight().equals(node) && edge.getLeft().equals(target)) {
	        return edge.getWeight();
	      }
	    }
	    throw new RuntimeException("Should not happen");
	  }

	  private List<NodeThread> getNeighbors(NodeThread node) {
	    List<NodeThread> neighbors = new ArrayList<NodeThread>();
	    for (Edge edge : edges) {
	      if (edge.getLeft().equals(node) && !isSettled(edge.getRight()) )
	    	  neighbors.add(edge.getRight());
	      else if (edge.getRight().equals(node) && !isSettled(edge.getLeft())) {
	        neighbors.add(edge.getLeft());
	      }
	    }
	    return neighbors;
	  }

	  private NodeThread getMinimum(Set<NodeThread> nodes) {
	    NodeThread minimum = null;
	    for (NodeThread n : nodes) {
	      if (minimum == null) {
	        minimum = n;
	      } else {
	        if (getShortestDistance(n) < getShortestDistance(minimum)) {
	          minimum = n;
	        }
	      }
	    }
	    return minimum;
	  }

	  private boolean isSettled(NodeThread Node) {
	    return settledNodes.contains(Node);
	  }

	  private double getShortestDistance(NodeThread destination) {
	    Double d = distance.get(destination);
	    if (d == null) {
	      return Integer.MAX_VALUE;
	    } else {
	      return d;
	    }
	  }

	  /*
	   * This method returns the path from the source to the selected target and
	   * NULL if no path exists
	   */
	  public LinkedList<NodeThread> getPath(NodeThread target) {
	    LinkedList<NodeThread> path = new LinkedList<NodeThread>();
	    NodeThread step = target;
	    // check if a path exists
	    if (predecessors.get(step) == null) {
	      return null;
	    }
	    path.add(step);
	    while (predecessors.get(step) != null) {
	      step = predecessors.get(step);
	      path.add(step);
	    }
	    // Put it into the correct order
	    Collections.reverse(path);
	    return path;
	  }
}
