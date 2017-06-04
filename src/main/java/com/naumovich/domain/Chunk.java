package com.naumovich.domain;

import com.naumovich.configuration.DdsConfiguration;
import com.naumovich.util.MathOperations;

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

	public Node getOriginalOwner() {
		return originalOwner;
	}
	public long getChunkSize() {
		return chunkSize;
	}
	public String getParentFileName() {
		return parentFileName;
	}
	public int getOrderNum() {
		return orderNum;
	}
	public String getChunkID() {
		return chunkID;
	}
	public String getChunkName() {
		return chunkName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chunkID == null) ? 0 : chunkID.hashCode());
		result = prime * result + ((chunkName == null) ? 0 : chunkName.hashCode());
		result = prime * result + (int) (chunkSize ^ (chunkSize >>> 32));
		result = prime * result + orderNum;
		result = prime * result + ((originalOwner == null) ? 0 : originalOwner.hashCode());
		result = prime * result + ((parentFileName == null) ? 0 : parentFileName.hashCode());
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
		Chunk other = (Chunk) obj;
		if (chunkID == null) {
			if (other.chunkID != null)
				return false;
		} else if (!chunkID.equals(other.chunkID))
			return false;
		if (chunkName == null) {
			if (other.chunkName != null)
				return false;
		} else if (!chunkName.equals(other.chunkName))
			return false;
		if (chunkSize != other.chunkSize)
			return false;
		if (orderNum != other.orderNum)
			return false;
		if (originalOwner == null) {
			if (other.originalOwner != null)
				return false;
		} else if (!originalOwner.equals(other.originalOwner))
			return false;
		if (parentFileName == null) {
			if (other.parentFileName != null)
				return false;
		} else if (!parentFileName.equals(other.parentFileName))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Chunk [chunkID=" + chunkID + ", chunkSize=" + chunkSize + ", parentFileName=" + parentFileName
				+ ", originalOwner=" + originalOwner + ", orderNum=" + orderNum + ", chunkName=" + chunkName + "]";
	}
	
}
