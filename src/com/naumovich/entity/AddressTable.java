package com.naumovich.entity;

import java.util.ArrayList;

import com.naumovich.abstraction.FourTuple;
import com.naumovich.abstraction.Table;

public class AddressTable implements Table {

	private ArrayList<FourTuple<Integer, Chunk, Node, Integer>> addrTable;
	private Node owner;
	public AddressTable(Node owner) {
		addrTable = new ArrayList<FourTuple<Integer, Chunk, Node, Integer>>();
		this.owner = owner;
	}
	
	public Node getOwner() {
		return owner;
	}

	public void addRow(int numOfChunk, Chunk chunk, Node node, int metrics) {
		//System.out.println("AddressTable.addRow() method");
		//System.out.println("Row: numOfChunk = " + numOfChunk + ", chunkId" + chunk.getChunkId() + ", nodeId = " + node.getNodeId() + ", metrics = " + metrics);
		addrTable.add(new FourTuple<Integer, Chunk, Node, Integer>(numOfChunk, chunk, node, metrics));
	}
	public void setRow(int rowNum, Node node, int metrics) { // to realize later
		addrTable.set(rowNum, new FourTuple<Integer, Chunk, Node, Integer>(addrTable.get(rowNum).first,
		addrTable.get(rowNum).second, node, metrics));
	}
	public FourTuple<Integer, Chunk, Node, Integer> getRow(int rowNum) {
		return addrTable.get(rowNum);
	}
	public int getRowCount() {
		return addrTable.size();
	}
	public Node getNodeByChunk(Chunk ch) {
		for (FourTuple<Integer, Chunk, Node, Integer> fTup : addrTable) {
			if (fTup.second == ch) {
				return fTup.third;
			}
		}
		return null;
	}
	
	@Override
	public void printTable() {
		System.out.println("This is Address Table of " + owner);
		for (int i = 0; i < addrTable.size(); i++) {
			System.out.println(addrTable.get(i));
		}
		
	}
	
}
