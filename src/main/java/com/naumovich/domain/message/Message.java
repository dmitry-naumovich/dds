package com.naumovich.domain.message;

import java.util.List;

import com.naumovich.domain.Node;

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

	public void setDestination(Node destination) {
		this.destination = destination;
	}
	
	public List<Node> getPath() {
		return path;
	}
	
	public void setPath(List<Node> path) {
		this.path = path;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		Message other = (Message) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Message [source=" + source + ", destination=" + destination + ", data=" + data + ", path=" + path + "]";
	}
	
}
