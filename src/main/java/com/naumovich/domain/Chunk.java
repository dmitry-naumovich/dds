package com.naumovich.domain;

import com.naumovich.configuration.DdsConfiguration;
import com.naumovich.util.MathOperations;
import lombok.Data;

@Data
public class Chunk {

	private static int counter = 0;

	private Node originalOwner;
	private String chunkID;
	private long chunkSize;
	private String parentFileName;
	private int orderNum;
	private String chunkName;
	
	public Chunk(Node originalOwner, long chunkSize, String parentFileName, int orderNum) {
		this.originalOwner = originalOwner;
		this.chunkID = MathOperations.getRandomHexString(DdsConfiguration.ID_LENGTH_IN_HEX);
		this.chunkSize = chunkSize;
		this.parentFileName = parentFileName;
		this.orderNum = orderNum;
		this.chunkName = "Chunk" + orderNum + counter++;
	}

	public Chunk(Chunk chunk, String newID) {
		this.originalOwner = chunk.originalOwner;
		this.chunkID = newID;
		this.chunkSize = chunk.chunkSize;
		this.parentFileName = chunk.parentFileName;
		this.orderNum = chunk.orderNum;
		this.chunkName = chunk.chunkName;
	}

}
