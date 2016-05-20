package com.naumovich.message;

import java.util.List;

import com.naumovich.entity.Node;

public class ResCopyMessage extends Message {

	public ResCopyMessage(List<Node> path, Object data) {
		super(path, data);
		
	}

}
