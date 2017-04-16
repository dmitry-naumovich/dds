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
	private NodeThread originalOwner;
	private int orderNum;
	private String chunkName;
	private static int counter = 0;
	
	public Chunk(NodeThread originalOwner, long chunkSize, String parentFileName, int orderNum) {
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
	
	public TwoTuple<NodeThread, Integer> findNodeForMe() {
		ArrayList<TwoTuple<NodeThread, Integer>> allMetrics = new ArrayList<TwoTuple<NodeThread, Integer>>();
		@SuppressWarnings("unchecked")
		List<NodeThread> nodes = (ArrayList<NodeThread>) Field.getNodes().clone();
		nodes.remove(this.originalOwner);
		for (NodeThread n: nodes) {
			if (n.isOnline())
				allMetrics.add(new TwoTuple<NodeThread, Integer>(n, MathOperations.findXORMetric(n.getNodeID(), this.chunkID)));
		}
		TwoTuple<NodeThread, Integer> two = MathOperations.findMin(allMetrics);
		return new TwoTuple<NodeThread, Integer>(two.first, two.second);
	}
	
	@Override
	public String toString() {
		return chunkName;
	}
}
