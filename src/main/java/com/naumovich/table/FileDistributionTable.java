package com.naumovich.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.naumovich.domain.Node;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class FileDistributionTable implements Iterable<FDTEntry> {

	private Node owner;
	private List<FDTEntry> fdtTable = new ArrayList<>();

	public FileDistributionTable(Node owner) {
		this.owner = owner;
	}

	public void addRow(int numOfChunk, String chunkId, String nodeLogin, int metrics) {
		fdtTable.add(new FDTEntry(numOfChunk, chunkId, nodeLogin, metrics));
	}

	public List<FDTEntry> getEntriesByNode(String node) {
	    return fdtTable.stream().filter(entry -> entry.getNode().equals(node)).collect(Collectors.toList());
	}

	public FDTEntry getAnotherEntryWithChunkCopy(int orderNum, String node) {
        List<FDTEntry> entries = getEntriesByOrderNum(orderNum);
	    return entries.stream().filter(entry -> !entry.getNode().equals(node)).findFirst().orElse(null);
	}

	private List<FDTEntry> getEntriesByOrderNum(int orderNum) {
		return fdtTable.stream().filter(entry -> entry.getOrderNum() == orderNum).collect(Collectors.toList());
	}
	
	@Override
	public Iterator<FDTEntry> iterator() {
		return new FDTIterator();
	}
	
	private class FDTIterator implements Iterator<FDTEntry> {

		private int curIndex;
		
		FDTIterator() {
			this.curIndex = 0;
		}

		@Override
		public boolean hasNext() {
			return curIndex < fdtTable.size();
		}

		@Override
		public FDTEntry next() {
			return fdtTable.get(curIndex++);
		}
	}
}
