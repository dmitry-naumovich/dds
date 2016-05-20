package com.naumovich.message;

import java.util.List;

import com.naumovich.entity.Node;

public class Message {

	private Node source;
	private Node destination;
	private Object data;
	private List<Node> path;
	
	public Message(List<Node> path, Object data) {
		this.source = path.get(0); 
		this.destination = path.get(path.size() - 1);
		this.data = data;
		this.path = path;
	}
	public void excludeFirstNodeFromPath() {
		path.remove(0);
	}
	public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	public Node getDestination() {
		return destination;
	}

	public List<Node> getPath() {
		return path;
	}
	public void setPath(List<Node> path) {
		this.path = path;
	}
	public void setDestination(Node destination) {
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
