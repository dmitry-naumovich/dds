package com.naumovich.domain.message;

import java.util.List;

import com.naumovich.domain.Node;

public class BackupMessage extends Message {

	public BackupMessage(List<Node> path, Object data) {
		super(path, data);
	}

}
