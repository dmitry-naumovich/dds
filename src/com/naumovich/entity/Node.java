package com.naumovich.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.naumovich.abstraction.Dijkstra;
import com.naumovich.abstraction.FourTuple;
import com.naumovich.abstraction.MathOperations;
import com.naumovich.abstraction.TwoTuple;
import com.naumovich.message.ChunkMessage;
import com.naumovich.message.Message;
import com.naumovich.message.ResCopyMessage;
import com.naumovich.network.Field;
import com.naumovich.network.MessageContainer;

public class Node {
	
	private Field field;
	private String login;
	private int persNum;
	private String nodeID;
	private List<Chunk> chunkStorage;
	private List<Message> resCopyMsgs;
	private ChunkTable chTable;
	private AddressTable addrTable;
	private boolean isOnline;
	private int amountOfRestransmitted = 0;
	private long amountOfMsgChecks = 0;
	private long amountOfNodeStatusChecks = 0;
	private long amountOfFindingPath = 0;
	
	private MessageContainer msgContainer;
	private static int counter = 0;
	
	public Node(Field field) {
		this.field = field;
		chunkStorage = Collections.synchronizedList(new ArrayList<Chunk>());
		resCopyMsgs = new ArrayList<Message>();
		persNum = counter;
		nodeID = MathOperations.getRandomHexString(40);
		login = "NodeThread" + counter++; 
		isOnline = true;
		addrTable = new AddressTable(this);
		msgContainer = new MessageContainer();
	}
	
	public Field getField() {
		return field;
	}
	public void setField(Field field) {
		this.field = field;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public int getPersNum() {
		return persNum;
	}
	public void setPersNum(int persNum) {
		this.persNum = persNum;
	}
	public String getNodeID() {
		return nodeID;
	}
	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}
	public List<Chunk> getChunkStorage() {
		return chunkStorage;
	}
	public void setChunkStorage(List<Chunk> chunkStorage) {
		this.chunkStorage = chunkStorage;
	}
	public List<Message> getResCopyMsgs() {
		return resCopyMsgs;
	}
	public void setResCopyMsgs(List<Message> resCopyMsgs) {
		this.resCopyMsgs = resCopyMsgs;
	}
	public ChunkTable getChTable() {
		return chTable;
	}
	public void setChTable(ChunkTable chTable) {
		this.chTable = chTable;
	}
	public AddressTable getAddrTable() {
		return addrTable;
	}
	public void setAddrTable(AddressTable addrTable) {
		this.addrTable = addrTable;
	}
	public boolean isOnline() {
		return isOnline;
	}
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
		if (isOnline == false) {
			System.out.println(this + ": I'm offline!");
		}
	}
	public int getAmountOfRestransmitted() {
		return amountOfRestransmitted;
	}
	public void setAmountOfRestransmitted(int amountOfRestransmitted) {
		this.amountOfRestransmitted = amountOfRestransmitted;
	}
	public long getAmountOfMsgChecks() {
		return amountOfMsgChecks;
	}
	public void setAmountOfMsgChecks(long amountOfMsgChecks) {
		this.amountOfMsgChecks = amountOfMsgChecks;
	}
	public long getAmountOfNodeStatusChecks() {
		return amountOfNodeStatusChecks;
	}
	public void setAmountOfNodeStatusChecks(long amountOfNodeStatusChecks) {
		this.amountOfNodeStatusChecks = amountOfNodeStatusChecks;
	}
	public long getAmountOfFindingPath() {
		return amountOfFindingPath;
	}
	public void setAmountOfFindingPath(long amountOfFindingPath) {
		this.amountOfFindingPath = amountOfFindingPath;
	}
	
	
	public void distributeFile(File file) {
		int n = MathOperations.defineChunksAmount(file.getSize());
		System.out.println(login + ": I distribute file '" + file.getFileName() + "' into " + n + " chunks");
		List<Chunk> chunks = createChunks(file, n);
		chTable = new ChunkTable(n));
		//addrTable = new AddressTable(this);
		List<Chunk> chunksAndCopies = new ArrayList<Chunk>();
		for (Chunk ch : chunks) {
			ArrayList<Chunk> alc = ch.makeCopies();
			chTable.setRow(alc, ch.getOrderNum() - 1);
			chunksAndCopies.addAll(alc);
		}
//		chTable.printTable();
		for (Chunk ch : chunksAndCopies) {
			TwoTuple<NodeThread, Integer> tuple = ch.findNodeForMe();
			addrTable.addRow(ch.getOrderNum(), ch, tuple.first, tuple.second);
			// encryptedChunk = ch.encrypt();
		}
		addrTable.printTable();
		for (Chunk ch : chunksAndCopies) {
			Dijkstra dijAlg = new Dijkstra(); // Dijkstra defines the route to destination
			dijAlg.execute(this); // Dijkstra works
			List<NodeThread> path = dijAlg.getPath(addrTable.getNodeByChunk(ch));
			amountOfFindingPath++;
			System.out.println(this.getLogin() + ": I send " + ch.getChunkName() + " to " + 
								addrTable.getNodeByChunk(ch).getLogin() + ". The way is: " + path);
			if (path != null) {
				Message msg = new ChunkMessage(path, ch); 
				msg.excludeFirstNodeFromPath();
				msgContainer.addMsg(msg);
			}
			
			// moreover, ����� ��� ����������� ��������� ���-�� ����� ��������, ��� �������������� �����
			// ��������, �� ���� 5 ������ ��� ����� ��������� 3
			// ��� ����������� ����, ��� �� ���� ���� ������� ��� ��������� �����
			// (��� �����, ������, ��������, ��� 8 ����)... ���� �� ���������
		}
	}
	
	private ArrayList<Chunk> createChunks(File file, int n) {
		ArrayList<Chunk> chunks = new ArrayList<Chunk>();
		for (int i = 0; i < n; i++) {
			chunks.add(new Chunk(this, file.getSize() / n, file.getFileName(), i+1));
		}
		return chunks;
	} 
	
	@SuppressWarnings("unchecked")
	public void checkMsgContainer() {
		List<Message> msgs = MessageContainer.allMsgs;
		synchronized(msgs) {
			for (Iterator<Message> iterator = msgs.iterator(); iterator.hasNext(); ) {
			    Message m = iterator.next();
			    amountOfMsgChecks++;
			    if (this.equals(m.getPath().get(0)) && this.equals(m.getDestination())) { // receive and store

			    	switch (m.getClass().getSimpleName()) {
			    		case "ChunkMessage": 
			    			System.out.println(this + ": I accept " + (Chunk)m.getData() + " to store it");
							chunkStorage.add((Chunk)m.getData());
							break;
			    		case "ResCopyMessage":
							NodeThread receiver = ((TwoTuple<NodeThread, Chunk>)m.getData()).first;
							Chunk chunkToCopy = ((TwoTuple<NodeThread, Chunk>)m.getData()).second;
			    			
			    			Dijkstra dijAlg = new Dijkstra(); // Dijkstra defines the route to destination
			    			dijAlg.execute(this);
			    			List<NodeThread> path = dijAlg.getPath(receiver);
			    			amountOfFindingPath++;
			    			System.out.println(this + ": I have to complete ResCopy of " + chunkToCopy + " to " + receiver
			    					+ ". Path is: " + path);
			    			if (path != null) {
			    				Message newM = new ChunkMessage(path, getChunkByName(chunkToCopy.getChunkName()));
			    				newM.excludeFirstNodeFromPath();
			    				resCopyFlag = true;
			    				resCopyMsgs.add(newM);
			    			}
			    			break;
			    	}
			    	iterator.remove();	
			    }
			    else if (this.equals(m.getPath().get(0)) && !this.equals(m.getDestination())) { // retransmit
			    	amountOfNodeStatusChecks++;
			    	if (!m.getDestination().isOnline) {
			    		
			    		System.out.println(this + ": WARNING! While retransmitting I've found out " + m.getDestination() + " is offline. And I discard this");
			    		iterator.remove();
			    	}
			    	else if (m.getPath().get(1).isOnline()) {
			    		amountOfNodeStatusChecks++;
			    		switch (m.getClass().getSimpleName()) {
				    		case "ChunkMessage": 
				    			System.out.println(this + ": I retransmit " + (Chunk)m.getData() + " further");
								break;
				    		case "ResCopyMessage":
				    			System.out.println(this + ": I retransmit ResCopyMessage of " + ((TwoTuple<NodeThread, Chunk>)m.getData()).second + " further");
				    			break;
			    		}
			    		amountOfRestransmitted++;
						m.excludeFirstNodeFromPath(); // i.e. retransmit
			    	}
			    	else {
			    		amountOfNodeStatusChecks++;
			    		//System.out.println(this + ": WARNING! While retransmitting I've found out " + m.getPath().get(1) + " is offline");
			    		// no action needed because source checks other nodes' status itself
			    		//iterator.remove();
			    		Dijkstra dijAlg = new Dijkstra(); // Dijkstra defines the route to destination
		    			dijAlg.execute(this);
		    			List<NodeThread> path = dijAlg.getPath(m.getDestination());
		    			amountOfFindingPath++;
		    			System.out.println(this + ": WARNING! While retransmitting I've found out " + m.getPath().get(1)
		    					+ " is offline. New path to destination " + m.getDestination() + " is: " + path);
		    			if (path != null) {
		    				m.setPath(path);
		    				m.excludeFirstNodeFromPath();
		    			}
			    	}
				}
			}
		}
	}
	
	public void checkNeighbors() { // find neighbors and fill the edgesMatrix
		ArrayList<NodeThread> nodes = Field.getNodes();	
		if (nodes.size() == Field.NNUM) {	
			for (NodeThread n: nodes) {
				amountOfNodeStatusChecks++;
				if (isNeighborWith(n) && n.isOnline()) {
					field.setEdgesMatrixCell(persNum, n.getPersNum(), 1);
					field.setEdgesMatrixCell(n.getPersNum(), persNum, 1);
				} 
				else {
					field.setEdgesMatrixCell(persNum, n.getPersNum(), 0);
					field.setEdgesMatrixCell(n.getPersNum(), persNum, 0);
				}
			}
		}
	}
	public boolean isNeighborWith(NodeThread n) {
		if (this.equals(n)) return false;
		else if (Math.pow(x - n.getX(), 2) + Math.pow(y - n.getY(), 2) <= Math.pow(12*radius, 2)) {
			// here if two nodes are neighbors
			return true;
		}
		return false;
	}
	public void checkNodesStatus() {
		for (int i = 0; i < addrTable.getRowCount(); i++) {
			FourTuple<Integer, Chunk, Node, Integer> tup = addrTable.getRow(i);
			amountOfNodeStatusChecks++;
			if (!tup.third.isOnline()) {
				System.out.println(this + ": I've found out " + tup.third + " is offline");
				TwoTuple<NodeThread, Integer> tup2 = tup.second.findNodeForMe();
				if (tup.second.equals(tup2.first)) {
					break; // same node returned so no more need for reserve copying
				}
				else {
					addrTable.setRow(i, tup2.first, tup2.second);
					int rowOfSender = getNewSender(i);
					Node sender = addrTable.getRow(rowOfSender).third;
					Chunk chunkToSend = addrTable.getRow(rowOfSender).second;
					System.out.println(this + ": new sender of " + chunkToSend + " is " + sender);
					Dijkstra dijAlg = new Dijkstra(); // Dijkstra defines the route to destination
					dijAlg.execute(this);
					List<NodeThread> path = dijAlg.getPath(sender);
					amountOfFindingPath++;
					if (path != null) {
						Message resCopyMsg = new ResCopyMessage(path, new TwoTuple<NodeThread, Chunk>(tup2.first, chunkToSend) );
						resCopyMsg.excludeFirstNodeFromPath();
						msgContainer.addMsg(resCopyMsg);
	    			}
				}
					
			}
		}
	}
	public Integer getNewSender(int i) {
		int orderNum = addrTable.getRow(i).first;
		for (int j = 0; j < addrTable.getRowCount(); j++) {
			FourTuple<Integer, Chunk, NodeThread, Integer> t = addrTable.getRow(j);
			if (t.first == orderNum && !t.third.equals(addrTable.getRow(i).third)) {
				return j;
			}
		}
		return null;
	}
	public void makeResCopy() { 
		List<Message> msgs = MessageContainer.allMsgs;
		synchronized(msgs) {
			for (Message m : resCopyMsgs) {
				msgs.add(m);
			}
		}
	}
	
	public Chunk getChunkByName(String name) { // in storage
		for (Chunk c : chunkStorage) {
			if (c.getChunkName().equals(name)) {
				return c;
			}
		}
		return null;
	}
	@Override
	public boolean equals(Object obj) {
		if (((Node)obj).nodeID == this.nodeID) 
			return true;
		else
			return false;
		
	}
	@Override
	public String toString() {
		return login;
	}
	
}
