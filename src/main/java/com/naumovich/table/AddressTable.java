package com.naumovich.table;

import java.util.ArrayList;
import java.util.Iterator;
import com.naumovich.domain.Chunk;
import com.naumovich.domain.Node;
import com.naumovich.util.tuple.FourTuple;

public class AddressTable implements Iterable<AddressTableEntry> {

	private ArrayList<AddressTableEntry> addressTable;
	private Node owner;
	
	public AddressTable(Node owner) {
		addressTable = new ArrayList<>();
		this.owner = owner;
	}

	public void addRow(int numOfChunk, Chunk chunk, Node node, int metrics) {
		addressTable.add(new AddressTableEntry(numOfChunk, chunk, node, metrics));
	}
	public void setRow(int rowNum, Node node, int metrics) {
		addressTable.set(rowNum, new AddressTableEntry(addressTable.get(rowNum).getOrderNum(),
				addressTable.get(rowNum).getChunk(), node, metrics));
	}
	public AddressTableEntry getRow(int rowNum) {
		return addressTable.get(rowNum);
	}
	public int getRowCount() {
		return addressTable.size();
	}
	
	@Override
	public Iterator<AddressTableEntry> iterator() {
		return new AddressTableIterator();
	}
	
	private class AddressTableIterator implements Iterator<AddressTableEntry> {

		private int curIndex;
		
		public AddressTableIterator() {
			this.curIndex = 0;
		}
		@Override
		public boolean hasNext() {
			if (curIndex < addressTable.size()) {
				return true;
			}
			return false;
		}

		@Override
		public AddressTableEntry next() {
			return addressTable.get(curIndex++);
		}
		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((addressTable == null) ? 0 : addressTable.hashCode());
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
		if (addressTable == null) {
			if (other.addressTable != null)
				return false;
		} else if (!addressTable.equals(other.addressTable))
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
		for (int i = 0; i < addressTable.size(); i++) {
			sb.append("\n" + (addressTable.get(i)));
		}
		return sb.toString();
	}	
}
