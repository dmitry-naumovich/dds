package com.naumovich.message;

import java.util.List;

import com.naumovich.entity.NodeThread;

public class Message {

	private NodeThread source;
	private NodeThread destination;
	private Object data;
	private List<NodeThread> path;
	
	public Message(List<NodeThread> path, Object data) {
		this.source = path.get(0); 
		this.destination = path.get(path.size() - 1);
		this.data = data;
		this.path = path;
	}
	public void excludeFirstNodeFromPath() {
		path.remove(0);
	}
	public NodeThread getSource() {
		return source;
	}

	public void setSource(NodeThread source) {
		this.source = source;
	}

	public NodeThread getDestination() {
		return destination;
	}

	public List<NodeThread> getPath() {
		return path;
	}
	public void setPath(List<NodeThread> path) {
		this.path = path;
	}
	public void setDestination(NodeThread destination) {
		this.destination = destination;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Message from : " + source + " to " + destination;
	}
	
	
}
