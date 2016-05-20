package com.naumovich.message;

import java.util.List;

import com.naumovich.entity.Chunk;
import com.naumovich.entity.Node;

public class ChunkMessage extends Message {

	public ChunkMessage(List<Node> path, Chunk chunk) {
		super(path, chunk);
	}
}
