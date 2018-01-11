package com.naumovich.domain;

import lombok.Data;

@Data
public class Edge {

	private Node left;
	private Node right;
	private double weight; // weight is compared with destination between left and right
	
	public Edge(Node left, Node right) {
		this.left = left;
		this.right = right;
		this.weight = countWeight();
	}
	
	private double countWeight() {
		return Math.sqrt(Math.pow(left.getNodeThread().getX() - right.getNodeThread().getX(), 2) + Math.pow(left.getNodeThread().getY() - right.getNodeThread().getY(), 2));
	}

}
