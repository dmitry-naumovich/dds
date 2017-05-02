package com.naumovich.manager;

import com.naumovich.domain.Chunk;
import com.naumovich.domain.File;
import com.naumovich.domain.Node;
import com.naumovich.table.AddressTable;
import com.naumovich.util.MathOperations;
import com.naumovich.util.tuple.TwoTuple;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
public class ChunkManager {

    private static final int AMOUNT_OF_CHUNK_COPIES = 4;
	private Map<File, AddressTable> addressTableMap;
	private Node owner;
	
	public ChunkManager(Node owner) {
		this.owner = owner;
		addressTableMap = new HashMap<>();
	}
	
	public AddressTable createAddressTable(File file) {
		int n = MathOperations.defineChunksAmount(file.getSize());
		log.info(owner.getLogin() + ": I distribute file '" + file.getFileName() + "' into " + n + " chunks");
		List<Chunk> chunksAndCopies = createChunksAndCopies(file, n);

		AddressTable addressTable = new AddressTable(owner);
		for (Chunk ch : chunksAndCopies) {
			TwoTuple<Node, Integer> tuple = ch.findNodeForMe();
			addressTable.addRow(ch.getOrderNum(), ch, tuple.first, tuple.second);
			// encryptedChunk = ch.encrypt();
		}
		
		addressTableMap.put(file, addressTable);
		return addressTable;
	}


	private List<Chunk> createChunksAndCopies(File file, int n) {
		ArrayList<Chunk> chunks = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			chunks.add(new Chunk(owner, file.getSize() / n, file.getFileName(), i+1));
		}

		List<Chunk> chunksAndCopies = new ArrayList<>();
		for (Chunk ch : chunks) {
			List<Chunk> alc = ch.makeCopies(AMOUNT_OF_CHUNK_COPIES);
			chunksAndCopies.addAll(alc);
		}

		return chunksAndCopies;
	}
}
