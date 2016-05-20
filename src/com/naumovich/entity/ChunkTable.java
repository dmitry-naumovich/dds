package com.naumovich.entity;

import java.util.ArrayList;

import com.naumovich.abstraction.Table;

public class ChunkTable implements Table {

		private ArrayList<ArrayList<Chunk>> chunkTable;
		private static final int colNum = 5;
		private int rowNum;
		public ChunkTable(int rowNum) {
			this.rowNum = rowNum;
			chunkTable = new ArrayList<ArrayList<Chunk>>();
			for (int i = 0; i < this.rowNum; i++) {
				ArrayList<Chunk> ar = new ArrayList<Chunk>();
				for (int j = 0; j < colNum; j++)
					ar.add(null);
				chunkTable.add(ar);
			}
		}
		public void setRow(ArrayList<Chunk> alc, int currRow) {
			//System.out.println("ChunkTable.addRow() method");
			for (int i = 0; i < colNum; i++) {
				chunkTable.get(currRow).set(i, alc.get(i)); 
				//System.out.println("iter = " + i + ", currRow = " + currRow + ", " + chunkTable.get(currRow).get(i));
			}
		}
		
		@Override
		public void printTable() {
			System.out.println("This is Chunk Table");
			for (int i = 0; i < chunkTable.size(); i++) {
				System.out.println(chunkTable.get(i));
			}
			
		}
		
		
}
