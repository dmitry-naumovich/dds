package com.naumovich.domain.message.dijkstra;

import java.util.List;

import com.naumovich.domain.Chunk;
import com.naumovich.domain.Node;

public class ChunkMessage extends DdsMessage {

	public ChunkMessage(List<Node> path, Chunk chunk) {
		super(path, chunk);
	}
	
}
