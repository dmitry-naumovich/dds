package com.naumovich.entity;

public class Edge {

	//private final String id;
	private NodeThread left;
	private NodeThread right;
	private double weight; // weight is compared with destination between left and right
	
	public Edge(NodeThread left, NodeThread right) {
		this.left = left;
		this.right = right;
		this.weight = countWeight();
	}
	
	private double countWeight() {
		return Math.sqrt(Math.pow(left.getX() - right.getX(), 2) + Math.pow(left.getY() - right.getY(), 2));
	}

	public NodeThread getLeft() {
		return left;
	}

	public NodeThread getRight() {
		return right;
	}

	public double getWeight() {
		return weight;
	}
}
