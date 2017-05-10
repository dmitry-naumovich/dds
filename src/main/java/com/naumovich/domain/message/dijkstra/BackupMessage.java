package com.naumovich.domain.message.dijkstra;

import java.util.List;

import com.naumovich.domain.Node;

public class BackupMessage extends DdsMessage {

	public BackupMessage(List<Node> path, Object data) {
		super(path, data);
	}

}
