package com.naumovich.message;

import java.util.List;

import com.naumovich.entity.Chunk;
import com.naumovich.entity.NodeThread;

public class ChunkMessage extends Message {

	public ChunkMessage(List<NodeThread> path, Chunk chunk) {
		super(path, chunk);
	}
}
