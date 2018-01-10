package com.naumovich.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.naumovich.domain.Node;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class FileDistributionTable implements Iterable<FDTEntry> {

	private Node owner;
	private List<FDTEntry> fdtTable;

	public FileDistributionTable(Node owner) {
		this.owner = owner;
		fdtTable = new ArrayList<>();
	}

	public void addRow(int numOfChunk, String chunkId, String nodeLogin, int metrics) {
		fdtTable.add(new FDTEntry(numOfChunk, chunkId, nodeLogin, metrics));
	}
	public void setRow(int rowNum, Node node, int metrics) {
		fdtTable.set(rowNum, new FDTEntry(fdtTable.get(rowNum).getOrderNum(),
				fdtTable.get(rowNum).getChunk(), node.getLogin(), metrics));
	}
	public FDTEntry getRow(int rowNum) {
		return fdtTable.get(rowNum);
	}

	public int getTableSize() {
		return fdtTable.size();
	}

	public List<FDTEntry> getEntriesByNode(String node) {
		List<FDTEntry> entries = new ArrayList<>();
		for (FDTEntry entry : fdtTable) {
			if (entry.getNode().equals(node)) {
				entries.add(entry);
			}
		}
		return entries;
	}

	public FDTEntry getAnotherEntryWithChunkCopy(int orderNum, String node) {
		for (FDTEntry entry : getEntriesByOrderNum(orderNum)) {
			if (!entry.getNode().equals(node)) {
				return entry;
			}
		}
		return null;
	}

	public List<FDTEntry> getEntriesByOrderNum(int orderNum) {
		List<FDTEntry> entries = new ArrayList<>();
		for (FDTEntry entry : fdtTable) {
			if (entry.getOrderNum() == orderNum) {
				entries.add(entry);
			}
		}
		return entries;
	}
	
	@Override
	public Iterator<FDTEntry> iterator() {
		return new FDTIterator();
	}
	
	private class FDTIterator implements Iterator<FDTEntry> {

		private int curIndex;
		
		public FDTIterator() {
			this.curIndex = 0;
		}
		@Override
		public boolean hasNext() {
			if (curIndex < fdtTable.size()) {
				return true;
			}
			return false;
		}

		@Override
		public FDTEntry next() {
			return fdtTable.get(curIndex++);
		}
		
	}
}
