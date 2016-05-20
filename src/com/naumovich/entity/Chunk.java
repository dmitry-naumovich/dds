package com.naumovich.entity;

import java.util.ArrayList;
import java.util.List;

import com.naumovich.abstraction.MathOperations;
import com.naumovich.abstraction.TwoTuple;
import com.naumovich.network.Field;

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
	public ArrayList<Chunk> makeCopies() {
		ArrayList<Chunk> chs = new ArrayList<Chunk>();
		chs.add(this);
		for (int i = 0; i < 4; i++) 
			chs.add(new Chunk(this.originalOwner, this.chunkSize, this.parentFileName, this.orderNum));
		return chs;
	}
	
	public TwoTuple<Node, Integer> findNodeForMe() {
		ArrayList<TwoTuple<Node, Integer>> allMetrics = new ArrayList<TwoTuple<Node, Integer>>();
		@SuppressWarnings("unchecked")
		List<Node> nodes = (ArrayList<Node>) Field.getNodes().clone();
		nodes.remove(this.originalOwner);
		for (Node n: nodes) {
			if (n.isOnline())
				allMetrics.add(new TwoTuple<Node, Integer>(n, MathOperations.findXORMetric(n.getNodeID(), this.chunkID)));
		}
		TwoTuple<Node, Integer> two = MathOperations.findMin(allMetrics);
		return new TwoTuple<Node, Integer>(two.first, two.second);
	}
	
	@Override
	public String toString() {
		return chunkName;
	}
}
