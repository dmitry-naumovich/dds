package com.naumovich.domain;

import static com.naumovich.network.TestNetwork.NODES_NUM;

import java.util.ArrayList;
import java.util.List;

import com.naumovich.domain.message.Message;
import com.naumovich.domain.message.ResCopyMessage;
import com.naumovich.manager.ChunkManager;
import com.naumovich.manager.DijkstraRoutingManager;
import com.naumovich.manager.MessageManager;
import com.naumovich.manager.RoutingManager;
import com.naumovich.network.*;
import com.naumovich.table.AddressTable;
import com.naumovich.util.Dijkstra;
import com.naumovich.util.MathOperations;
import com.naumovich.util.tuple.FourTuple;
import com.naumovich.util.tuple.TwoTuple;

//TODO: override toString, hashCode and equals after Node entity completed
public class Node {
	
	private NodeThread nodeThread;
	private final Field field;
	private final String login;
	private final int persNum;
	private final String nodeID;
	private ChunkStorage chunkStorage;
	private AddressTable addrTable;
	private boolean isOnline;
	private int amountOfRetransmitted = 0;
	private long amountOfMsgChecks = 0;
	private long amountOfNodeStatusChecks = 0;
	private long amountOfFindingPath = 0;
	
	private ChunkManager chunkManager;
	private RoutingManager routingManager;
	private MessageManager messageManager;

	private static int counter = 0;
	
	public Node(NodeThread thread, Field field) {
		this.nodeThread = thread;
		this.field = field;
		chunkStorage = new ChunkStorage();
		persNum = counter;
		nodeID = MathOperations.getRandomHexString(40);
		login = "Node" + counter++;
		isOnline = true;

		chunkManager = new ChunkManager(this);
		routingManager = new DijkstraRoutingManager();
		messageManager = new MessageManager(this);
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
	public String getLogin() {
		return login;
	}
	public int getPersNum() {
		return persNum;
	}
	public String getNodeID() {
		return nodeID;
	}
	public ChunkStorage getChunkStorage() {
		return chunkStorage;
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
			System.out.println(login + ": I'm offline!");
		}
	}
	public int getAmountOfRetransmitted() {
		return amountOfRetransmitted;
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

	public void incrementAmountOfFindingPath() {
	    amountOfFindingPath++;
    }
    public void incrementAmountOfMsgChecks() {
        amountOfMsgChecks++;
    }
    public void incrementAmountOfNodeStatusChecks() {
        amountOfNodeStatusChecks++;
    }
    public void incrementAmountOfRestransmitted() {
        amountOfRetransmitted++;
    }
	
	public void distributeFile(File file) {
		this.addrTable = chunkManager.createAddressTable(file);
		routingManager.distributeChunks(this, addrTable);
	}

	public void checkMessageContainer() {
        messageManager.checkMessageContainer();
	}
	
	public void findNeighbors() { // find neighbors and fill the edgesMatrix
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
		if (addrTable != null) {
			routingManager.checkNodesStatus(this, addrTable);
		}
	}

	public void makeResCopy() {
		messageManager.makeResCopy();
	}
}
