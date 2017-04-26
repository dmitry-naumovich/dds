package com.naumovich.domain;

import static com.naumovich.network.TestNetwork.NODES_NUM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.naumovich.domain.message.ChunkMessage;
import com.naumovich.domain.message.Message;
import com.naumovich.domain.message.ResCopyMessage;
import com.naumovich.network.*;
import com.naumovich.table.AddressTable;
import com.naumovich.util.Dijkstra;
import com.naumovich.util.MathOperations;
import com.naumovich.util.tuple.FourTuple;
import com.naumovich.util.tuple.TwoTuple;

import manager.ChunkManager;
import manager.RoutingManager;

public class Node {
	
	private NodeThread nodeThread;
	private Field field;
	private String login;
	private int persNum;
	private String nodeID;
	private List<Chunk> chunkStorage;
	private List<Message> resCopyMsgs;
	private AddressTable addrTable;
	private boolean isOnline;
	private int amountOfRestransmitted = 0;
	private long amountOfMsgChecks = 0;
	private long amountOfNodeStatusChecks = 0;
	private long amountOfFindingPath = 0;
	
	private ChunkManager chunkManager;
	private RoutingManager routingManager;

	private static int counter = 0;
	
	public Node(NodeThread thread, Field field) {
		this.nodeThread = thread;
		this.field = field;
		chunkStorage = Collections.synchronizedList(new ArrayList<Chunk>());
		resCopyMsgs = new ArrayList<Message>();
		persNum = counter;
		nodeID = MathOperations.getRandomHexString(40);
		login = "NodeThread" + counter++; 
		isOnline = true;
		addrTable = new AddressTable(this);
		chunkManager = new ChunkManager(this);
	}
	
	public NodeThread getNodeThread() {
		return nodeThread;
	}

	public void setNodeThread(NodeThread nodeThread) {
		this.nodeThread = nodeThread;
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
	public long getAmountOfMsgChecks() {
		return amountOfMsgChecks;
	}
	public long getAmountOfNodeStatusChecks() {
		return amountOfNodeStatusChecks;
	}
	public long getAmountOfFindingPath() {
		return amountOfFindingPath;
	}
	
	public void distributeFile(File file) {
		AddressTable table = chunkManager.createAddressTable(file);
		routingManager.distributeChunks(this, table);
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
							Node receiver = ((TwoTuple<Node, Chunk>)m.getData()).first;
							Chunk chunkToCopy = ((TwoTuple<Node, Chunk>)m.getData()).second;
			    			
			    			Dijkstra dijAlg = new Dijkstra(); // Dijkstra defines the route to destination
			    			dijAlg.execute(this);
			    			List<Node> path = dijAlg.getPath(receiver);
			    			amountOfFindingPath++;
			    			System.out.println(this + ": I have to complete ResCopy of " + chunkToCopy + " to " + receiver
			    					+ ". Path is: " + path);
			    			if (path != null) {
			    				Message newM = new ChunkMessage(path, getChunkByName(chunkToCopy.getChunkName()));
			    				newM.excludeFirstNodeFromPath();
			    				nodeThread.setResCopyFlag(true);
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
		    			List<Node> path = dijAlg.getPath(m.getDestination());
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
		ArrayList<Node> nodes = Field.getNodes();	
		if (nodes.size() == NODES_NUM) {	
			for (Node n: nodes) {
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
	public boolean isNeighborWith(Node n) {
		if (this.equals(n)) return false;
		else if (Math.pow(nodeThread.getX() - n.getNodeThread().getX(), 2) + Math.pow(nodeThread.getY() - n.getNodeThread().getY(), 2) <= Math.pow(12*NodeThread.getRadius(), 2)) {
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
				TwoTuple<Node, Integer> tup2 = tup.second.findNodeForMe();
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
					List<Node> path = dijAlg.getPath(sender);
					amountOfFindingPath++;
					if (path != null) {
						Message resCopyMsg = new ResCopyMessage(path, new TwoTuple<Node, Chunk>(tup2.first, chunkToSend) );
						resCopyMsg.excludeFirstNodeFromPath();
						MessageContainer.addMsg(resCopyMsg);
	    			}
				}
					
			}
		}
	}
	public Integer getNewSender(int i) {
		int orderNum = addrTable.getRow(i).first;
		for (int j = 0; j < addrTable.getRowCount(); j++) {
			FourTuple<Integer, Chunk, Node, Integer> t = addrTable.getRow(j);
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((addrTable == null) ? 0 : addrTable.hashCode());
		result = prime * result + (int) (amountOfFindingPath ^ (amountOfFindingPath >>> 32));
		result = prime * result + (int) (amountOfMsgChecks ^ (amountOfMsgChecks >>> 32));
		result = prime * result + (int) (amountOfNodeStatusChecks ^ (amountOfNodeStatusChecks >>> 32));
		result = prime * result + amountOfRestransmitted;
		result = prime * result + ((chunkStorage == null) ? 0 : chunkStorage.hashCode());
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + (isOnline ? 1231 : 1237);
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		result = prime * result + ((nodeID == null) ? 0 : nodeID.hashCode());
		result = prime * result + ((nodeThread == null) ? 0 : nodeThread.hashCode());
		result = prime * result + persNum;
		result = prime * result + ((resCopyMsgs == null) ? 0 : resCopyMsgs.hashCode());
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
		Node other = (Node) obj;
		if (addrTable == null) {
			if (other.addrTable != null)
				return false;
		} else if (!addrTable.equals(other.addrTable))
			return false;
		if (amountOfFindingPath != other.amountOfFindingPath)
			return false;
		if (amountOfMsgChecks != other.amountOfMsgChecks)
			return false;
		if (amountOfNodeStatusChecks != other.amountOfNodeStatusChecks)
			return false;
		if (amountOfRestransmitted != other.amountOfRestransmitted)
			return false;
		if (chunkStorage == null) {
			if (other.chunkStorage != null)
				return false;
		} else if (!chunkStorage.equals(other.chunkStorage))
			return false;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (isOnline != other.isOnline)
			return false;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		if (nodeID == null) {
			if (other.nodeID != null)
				return false;
		} else if (!nodeID.equals(other.nodeID))
			return false;
		if (nodeThread == null) {
			if (other.nodeThread != null)
				return false;
		} else if (!nodeThread.equals(other.nodeThread))
			return false;
		if (persNum != other.persNum)
			return false;
		if (resCopyMsgs == null) {
			if (other.resCopyMsgs != null)
				return false;
		} else if (!resCopyMsgs.equals(other.resCopyMsgs))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Node [nodeThread=" + nodeThread + ", field=" + field + ", login=" + login + ", persNum=" + persNum
				+ ", nodeID=" + nodeID + ", chunkStorage=" + chunkStorage + ", resCopyMsgs=" + resCopyMsgs
				+ ", addrTable=" + addrTable + ", isOnline=" + isOnline
				+ ", amountOfRestransmitted=" + amountOfRestransmitted + ", amountOfMsgChecks=" + amountOfMsgChecks
				+ ", amountOfNodeStatusChecks=" + amountOfNodeStatusChecks + ", amountOfFindingPath="
				+ amountOfFindingPath + " ]";
	}
	
	
}
