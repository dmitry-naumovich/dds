package com.naumovich.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.naumovich.domain.message.dijkstra.DdsMessage;

public class MessageContainer {

	public static volatile List<DdsMessage> allMsgs = Collections.synchronizedList(new ArrayList<DdsMessage>());

	public static synchronized void addMsg(DdsMessage msg) {
		allMsgs.add(msg);
	}
}
