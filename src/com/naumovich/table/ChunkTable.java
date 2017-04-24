package com.naumovich.table;

import java.util.ArrayList;

import com.naumovich.domain.Chunk;

public class ChunkTable {

	private ArrayList<ArrayList<Chunk>> chunkTable;
	private static final int colNum = 5;
	private int rowNum;
	
	public ChunkTable(int rowNum) {
		this.rowNum = rowNum;
		chunkTable = new ArrayList<ArrayList<Chunk>>();
		for (int i = 0; i < this.rowNum; i++) {
			ArrayList<Chunk> ar = new ArrayList<>();
			for (int j = 0; j < colNum; j++)
				ar.add(null);
			chunkTable.add(ar);
		}
	}
	public void setRow(ArrayList<Chunk> alc, int currRow) {
		for (int i = 0; i < colNum; i++) {
			chunkTable.get(currRow).set(i, alc.get(i)); 
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chunkTable == null) ? 0 : chunkTable.hashCode());
		result = prime * result + rowNum;
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
		ChunkTable other = (ChunkTable) obj;
		if (chunkTable == null) {
			if (other.chunkTable != null)
				return false;
		} else if (!chunkTable.equals(other.chunkTable))
			return false;
		if (rowNum != other.rowNum)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("This is Chunk Table");
		for (int i = 0; i < chunkTable.size(); i++) {
			sb.append("\n" + (chunkTable.get(i)));
		}
		return sb.toString();
	}
}
