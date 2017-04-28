package com.naumovich.manager;

import com.naumovich.domain.Chunk;
import com.naumovich.domain.File;
import com.naumovich.domain.Node;
import com.naumovich.table.AddressTable;
import com.naumovich.util.MathOperations;
import com.naumovich.util.tuple.TwoTuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkManager {

	private Map<File, AddressTable> addressTableMap;
	private Node owner;
	
	public ChunkManager(Node owner) {
		this.owner = owner;
		addressTableMap = new HashMap<>();
	}
	
	public AddressTable createAddressTable(File file) {
		int n = MathOperations.defineChunksAmount(file.getSize());
		System.out.println(owner.getLogin() + ": I distribute file '" + file.getFileName() + "' into " + n + " chunks");
		List<Chunk> chunks = createChunks(file, n);
		
		AddressTable addressTable = new AddressTable(owner);
		
		List<Chunk> chunksAndCopies = new ArrayList<>();
		for (Chunk ch : chunks) {
			List<Chunk> alc = ch.makeCopies();
			chunksAndCopies.addAll(alc);
		}
		
		for (Chunk ch : chunksAndCopies) {
			TwoTuple<Node, Integer> tuple = ch.findNodeForMe();
			addressTable.addRow(ch.getOrderNum(), ch, tuple.first, tuple.second);
			// encryptedChunk = ch.encrypt();
		}
		
		addressTableMap.put(file, addressTable);
		return addressTable;
	}
	
	private ArrayList<Chunk> createChunks(File file, int n) {
		ArrayList<Chunk> chunks = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			chunks.add(new Chunk(owner, file.getSize() / n, file.getFileName(), i+1));
		}
		return chunks;
	}
}
