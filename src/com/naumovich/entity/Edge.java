package com.naumovich.entity;

public class Edge {

	//private final String id;
	private Node left;
	private Node right;
	private double weight; // weight is compared with destination between left and right
	
	public Edge(Node left, Node right) {
		this.left = left;
		this.right = right;
		this.weight = countWeight();
	}
	
	private double countWeight() {
		return Math.sqrt(Math.pow(left.getX() - right.getX(), 2) + Math.pow(left.getY() - right.getY(), 2));
	}

	public Node getLeft() {
		return left;
	}

	public Node getRight() {
		return right;
	}

	public double getWeight() {
		return weight;
	}
}
