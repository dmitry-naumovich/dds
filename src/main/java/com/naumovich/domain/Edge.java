package com.naumovich.domain;

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
		return Math.sqrt(Math.pow(left.getNodeThread().getX() - right.getNodeThread().getX(), 2) + Math.pow(left.getNodeThread().getY() - right.getNodeThread().getY(), 2));
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		long temp;
		temp = Double.doubleToLongBits(weight);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		if (Double.doubleToLongBits(weight) != Double.doubleToLongBits(other.weight))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Edge [left=" + left + ", right=" + right + ", weight=" + weight + "]";
	}
	
}
