package com.naumovich.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.naumovich.domain.message.Message;

public class MessageContainer {

	public static volatile List<Message> allMsgs = Collections.synchronizedList(new ArrayList<Message>());

	public static synchronized void addMsg(Message msg) {
		allMsgs.add(msg);
	}
}
