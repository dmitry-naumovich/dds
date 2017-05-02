package com.naumovich.domain;

import java.util.ArrayList;

import com.naumovich.manager.ChunkManager;
import com.naumovich.manager.DijkstraRoutingManager;
import com.naumovich.manager.MessageManager;
import com.naumovich.manager.RoutingManager;
import com.naumovich.network.*;
import com.naumovich.table.AddressTable;
import com.naumovich.util.MathOperations;
import lombok.extern.slf4j.Slf4j;

//TODO: override toString, hashCode and equals after Node entity completed
@Slf4j
public class Node {

    private static int counter = 0;

    private NodeThread nodeThread;
    private final Field field;
    private final int persNum = counter;
    private final String login = "Node" + counter++;
    private final String nodeID = MathOperations.getRandomHexString(40);
    private ChunkStorage chunkStorage = new ChunkStorage();
    private AddressTable addrTable;
    private boolean isOnline = true;

    private int amountOfRetransmitted;
    private long amountOfMsgChecks;
    private long amountOfNodeStatusChecks;
    private long amountOfFindingPath;

    private ChunkManager chunkManager;
    private RoutingManager routingManager;
    private MessageManager messageManager;

    public Node(NodeThread thread, Field field) {
        this.nodeThread = thread;
        this.field = field;

        chunkManager = new ChunkManager(this);
        routingManager = new DijkstraRoutingManager();
        messageManager = new MessageManager(this);
    }

    public NodeThread getNodeThread() {
        return nodeThread;
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

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
        if (isOnline == false) {
            log.debug(login + ": I'm offline!");
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

    public void incrementAmountOfRetransmitted() {
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
        for (Node n : nodes) {
            amountOfNodeStatusChecks++;
            if (isNeighborWith(n) && n.isOnline()) {
                field.setEdgesMatrixCell(persNum, n.getPersNum(), 1);
                field.setEdgesMatrixCell(n.getPersNum(), persNum, 1);
            } else {
                field.setEdgesMatrixCell(persNum, n.getPersNum(), 0);
                field.setEdgesMatrixCell(n.getPersNum(), persNum, 0);
            }
        }
    }

    public boolean isNeighborWith(Node n) {
        if (this.equals(n)) {
            return false;
        }
        else if (Math.pow(nodeThread.getX() - n.getNodeThread().getX(), 2) + Math.pow(nodeThread.getY() - n.getNodeThread().getY(), 2) <= Math.pow(12 * NodeThread.getRadius(), 2)) {
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

    public void makeBackup() {
        messageManager.makeBackup();
    }

    @Override
    public String toString() {
        return login;
    }
}
