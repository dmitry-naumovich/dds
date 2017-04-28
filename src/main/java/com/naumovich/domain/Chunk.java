package com.naumovich.domain;

import java.util.ArrayList;
import java.util.List;

import com.naumovich.network.Field;
import com.naumovich.util.MathOperations;
import com.naumovich.util.tuple.TwoTuple;

public class Chunk {

	private String chunkID;
	private long chunkSize;
	private String parentFileName;
	private Node originalOwner;
	private int orderNum;
	private String chunkName;
	private static int counter = 0;
	
	public Chunk(Node originalOwner, long chunkSize, String parentFileName, int orderNum) {
		this.originalOwner = originalOwner;
		this.chunkID = MathOperations.getRandomHexString(40);
		this.chunkSize = chunkSize;
		this.parentFileName = parentFileName;
		this.orderNum = orderNum;
		this.chunkName = "Chunk" + orderNum + counter++;
	}
	public int getOrderNum() {
		return orderNum;
	}
	public String getChunkID() {
		return chunkID;
	}
	public String getChunkName() {
		return chunkName;
	}
	public List<Chunk> makeCopies() {
		List<Chunk> chs = new ArrayList<>();
		chs.add(this);
		for (int i = 0; i < 4; i++) 
			chs.add(new Chunk(this.originalOwner, this.chunkSize, this.parentFileName, this.orderNum));
		return chs;
	}
	
	public TwoTuple<Node, Integer> findNodeForMe() {
		List<TwoTuple<Node, Integer>> allMetrics = new ArrayList<>();
		@SuppressWarnings("unchecked")
		List<Node> nodes = (ArrayList<Node>) Field.getNodes().clone(); // ????????????????? clone???????? not implemented?
		nodes.remove(this.originalOwner);
		for (Node n: nodes) {
			if (n.isOnline())
				allMetrics.add(new TwoTuple<>(n, MathOperations.findXORMetric(n.getNodeID(), this.chunkID)));
		}
		TwoTuple<Node, Integer> two = MathOperations.findMin(allMetrics);
		return new TwoTuple<>(two.first, two.second);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chunkID == null) ? 0 : chunkID.hashCode());
		result = prime * result + ((chunkName == null) ? 0 : chunkName.hashCode());
		result = prime * result + (int) (chunkSize ^ (chunkSize >>> 32));
		result = prime * result + orderNum;
		result = prime * result + ((originalOwner == null) ? 0 : originalOwner.hashCode());
		result = prime * result + ((parentFileName == null) ? 0 : parentFileName.hashCode());
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
		Chunk other = (Chunk) obj;
		if (chunkID == null) {
			if (other.chunkID != null)
				return false;
		} else if (!chunkID.equals(other.chunkID))
			return false;
		if (chunkName == null) {
			if (other.chunkName != null)
				return false;
		} else if (!chunkName.equals(other.chunkName))
			return false;
		if (chunkSize != other.chunkSize)
			return false;
		if (orderNum != other.orderNum)
			return false;
		if (originalOwner == null) {
			if (other.originalOwner != null)
				return false;
		} else if (!originalOwner.equals(other.originalOwner))
			return false;
		if (parentFileName == null) {
			if (other.parentFileName != null)
				return false;
		} else if (!parentFileName.equals(other.parentFileName))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Chunk [chunkID=" + chunkID + ", chunkSize=" + chunkSize + ", parentFileName=" + parentFileName
				+ ", originalOwner=" + originalOwner + ", orderNum=" + orderNum + ", chunkName=" + chunkName + "]";
	}
	
}
