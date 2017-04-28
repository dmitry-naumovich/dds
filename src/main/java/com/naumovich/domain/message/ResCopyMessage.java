package com.naumovich.domain.message;

import java.util.List;

import com.naumovich.domain.Node;

public class ResCopyMessage extends Message {

	public ResCopyMessage(List<Node> path, Object data) {
		super(path, data);
	}

}
