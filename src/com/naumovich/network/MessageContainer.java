package com.naumovich.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.naumovich.domain.message.Message;

public class MessageContainer {

	public static List<Message> allMsgs = Collections.synchronizedList(new ArrayList<Message>());
	//public static CopyOnWriteArrayList<Message> allMsgs = new CopyOnWriteArrayList<Message>(new ArrayList<Message>());
	public synchronized void addMsg(Message msg) {
		allMsgs.add(msg);
	}
}
