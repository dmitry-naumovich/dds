package com.naumovich.manager;

import com.naumovich.domain.Chunk;
import com.naumovich.domain.File;
import com.naumovich.domain.Node;
import com.naumovich.network.Field;
import com.naumovich.table.AddressTable;
import com.naumovich.util.MathOperations;
import com.naumovich.util.tuple.TwoTuple;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.naumovich.configuration.DdsConfiguration.AMOUNT_OF_CHUNK_COPIES;

@Slf4j
public class ChunkManager {

	private Node owner;
	
	public ChunkManager(Node owner) {
		this.owner = owner;
	}
	
	public AddressTable createAddressTable(File file) {
		int n = MathOperations.defineChunksAmount(file.getSize());
		log.info(owner.getLogin() + ": I distribute file '" + file.getFileName() + "' into " + n + " chunks");
		List<Chunk> chunksAndCopies = createChunksAndCopies(file, n);

		AddressTable addressTable = new AddressTable(owner);
		for (Chunk ch : chunksAndCopies) {
			TwoTuple<Node, Integer> tuple = findNodeForChunk(ch);
			addressTable.addRow(ch.getOrderNum(), ch, tuple.first, tuple.second);
		}
		return addressTable;
	}


	private List<Chunk> createChunksAndCopies(File file, int n) {
		ArrayList<Chunk> chunks = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			chunks.add(new Chunk(owner, file.getSize() / n, file.getFileName(), i+1));
		}

		List<Chunk> chunksAndCopies = new ArrayList<>();
		for (Chunk ch : chunks) {
			List<Chunk> alc = makeChunkCopies(ch, AMOUNT_OF_CHUNK_COPIES);
			chunksAndCopies.addAll(alc);
		}

		return chunksAndCopies;
	}

	private List<Chunk> makeChunkCopies(Chunk chunk, int numOfCopies) {
		List<Chunk> chs = new ArrayList<>();
		chs.add(chunk);
		for (int i = 0; i < numOfCopies; i++) {
			chs.add(new Chunk(chunk.getOriginalOwner(), chunk.getChunkSize(), chunk.getParentFileName(), chunk.getOrderNum()));
		}
		return chs;
	}

	public TwoTuple<Node, Integer> findNodeForChunk(Chunk chunk) {
		List<TwoTuple<Node, Integer>> allMetrics = new ArrayList<>();
		List<Node> allNodes = new ArrayList<>(Field.getNodes());
		allNodes.remove(chunk.getOriginalOwner());
		for (Node n: allNodes) {
			if (n.isOnline())
				allMetrics.add(new TwoTuple<>(n, MathOperations.findXORMetric(n.getNodeID(), chunk.getChunkID())));
		}
		return MathOperations.findMin(allMetrics);
	}

}
