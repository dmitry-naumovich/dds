package com.naumovich.entity;

public class File {

	private String fileName;
	private long size;
	//private String ownerID;
	
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getFileName() {
		return fileName;
	}
	public File(String name, long size) {
		this.fileName = name;
		this.size = size;
	}
	
	
	
}
