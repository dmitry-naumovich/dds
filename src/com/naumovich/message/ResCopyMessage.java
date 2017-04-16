package com.naumovich.message;

import java.util.List;

import com.naumovich.entity.NodeThread;

public class ResCopyMessage extends Message {

	public ResCopyMessage(List<NodeThread> path, Object data) {
		super(path, data);
		
	}

}
