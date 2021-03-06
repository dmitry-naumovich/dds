package com.naumovich.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.naumovich.domain.Edge;
import com.naumovich.domain.Node;
import com.naumovich.network.Field;

public class Dijkstra {

    private List<Node> nodes;
    private List<Edge> edges;
    private Set<Node> settledNodes = new HashSet<>();
    private Set<Node> unSettledNodes = new HashSet<>();
    private Map<Node, Node> predecessors = new HashMap<>();
    private Map<Node, Double> distance = new HashMap<>();
    private int[][] edgesMatrix;

    private Dijkstra() {
        nodes = Field.getNodes();
        edgesMatrix = Field.getEdgesMatrix();
        edges = resolveEdges();
    }

    public synchronized static List<Node> findPathWithDijkstra(Node source, Node destination) {
        Dijkstra dijAlg = new Dijkstra();
        dijAlg.execute(source);
        List<Node> path = dijAlg.getPath(destination);
        source.incrementAmountOfFindingPath();
        return path;
    }

    private void execute(Node source) {
        distance.put(source, 0d);
        unSettledNodes.add(source);
        while (unSettledNodes.size() > 0) {
            Node node = getMinimum(unSettledNodes);
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    private List<Edge> resolveEdges() {
        List<Edge> allEdges = new ArrayList<>();
        for (int i = 0; i < nodes.size() - 1; i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                if (edgesMatrix[i][j] == 1) {
                    allEdges.add(new Edge(nodes.get(i), nodes.get(j)));
                }
            }
        }
        return allEdges;
    }

    private void findMinimalDistances(Node node) {
        List<Node> adjacentNodes = getNeighbors(node);
        for (Node target : adjacentNodes) {
            if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
                distance.put(target, getShortestDistance(node) + getDistance(node, target));
                predecessors.put(target, node);
                unSettledNodes.add(target);
            }
        }
    }

    private double getDistance(Node node, Node target) {
        for (Edge edge : edges) {
            if (edge.getLeft().equals(node) && edge.getRight().equals(target) ||
                    edge.getRight().equals(node) && edge.getLeft().equals(target)) {
                return edge.getWeight();
            }
        }
        throw new RuntimeException("Should not happen");
    }

    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getLeft().equals(node) && !isSettled(edge.getRight()))
                neighbors.add(edge.getRight());
            else if (edge.getRight().equals(node) && !isSettled(edge.getLeft())) {
                neighbors.add(edge.getLeft());
            }
        }
        return neighbors;
    }

    private Node getMinimum(Set<Node> nodes) {
        Node minimum = null;
        for (Node n : nodes) {
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

    private boolean isSettled(Node Node) {
        return settledNodes.contains(Node);
    }

    private double getShortestDistance(Node destination) {
        Double d = distance.get(destination);
        return d == null ? Integer.MAX_VALUE : d;
    }

    /*
     * This method returns the path from the source to the selected target and
     * NULL if no path exists
     */
    private LinkedList<Node> getPath(Node target) {
        LinkedList<Node> path = new LinkedList<>();
        Node step = target;
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
