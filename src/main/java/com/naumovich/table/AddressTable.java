package com.naumovich.table;

import java.util.ArrayList;
import java.util.Iterator;
import com.naumovich.domain.Chunk;
import com.naumovich.domain.Node;
import com.naumovich.util.tuple.FourTuple;

public class AddressTable implements Iterable<FourTuple<Integer, Chunk, Node, Integer>> {

	//private Map<Integer, Set<ThreeTuple<String, String, Integer>>> locationTable;
	private ArrayList<FourTuple<Integer, Chunk, Node, Integer>> addrTable;
	private Node owner;
	
	public AddressTable(Node owner) {
		addrTable = new ArrayList<>();
		this.owner = owner;
	}
	
	public Node getOwner() {
		return owner;
	}

	public void addRow(int numOfChunk, Chunk chunk, Node node, int metrics) {
		//System.out.println("AddressTable.addRow() method");
		//System.out.println("Row: numOfChunk = " + numOfChunk + ", chunkId" + chunk.getChunkId() + ", nodeId = " + node.getNodeId() + ", metrics = " + metrics);
		addrTable.add(new FourTuple<>(numOfChunk, chunk, node, metrics));
	}
	public void setRow(int rowNum, Node node, int metrics) {
		addrTable.set(rowNum, new FourTuple<>(addrTable.get(rowNum).first,
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
	public Iterator<FourTuple<Integer, Chunk, Node, Integer>> iterator() {
		return new AddressTableIterator();
	}
	
	private class AddressTableIterator implements Iterator<FourTuple<Integer, Chunk, Node, Integer>> {

		private int curIndex;
		
		public AddressTableIterator() {
			this.curIndex = 0;
		}
		@Override
		public boolean hasNext() {
			if (curIndex < addrTable.size()) {
				return true;
			}
			return false;
		}

		@Override
		public FourTuple<Integer, Chunk, Node, Integer> next() {
			return addrTable.get(curIndex++);
		}
		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((addrTable == null) ? 0 : addrTable.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
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
		AddressTable other = (AddressTable) obj;
		if (addrTable == null) {
			if (other.addrTable != null)
				return false;
		} else if (!addrTable.equals(other.addrTable))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("This is Address Table of " + owner.getLogin());
		for (int i = 0; i < addrTable.size(); i++) {
			sb.append("\n" + (addrTable.get(i)));
		}
		return sb.toString();
	}	
}
