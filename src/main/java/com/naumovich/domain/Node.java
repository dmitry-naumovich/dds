package com.naumovich.domain;

import java.util.*;

import com.naumovich.domain.message.aodv.IpMessage;
import com.naumovich.manager.*;
import com.naumovich.network.*;
import com.naumovich.table.FileDistributionTable;
import com.naumovich.table.RoutingTable;
import com.naumovich.util.MathOperations;
import lombok.extern.slf4j.Slf4j;

import static com.naumovich.configuration.ModelConfiguration.*;

/**
 *
 * @Author Dzmitry Naumovich
 */
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

    private boolean isOnline = true;

    private int amountOfRetransmitted;
    private long amountOfMsgChecks;
    private long amountOfNodeStatusChecks;
    private long amountOfFindingPath;

    private RoutingTable routingTable;
    private Map<File, FileDistributionTable> fileDistributionTableMap;

    private ChunkManager chunkManager;
    private AodvRoutingManager routingManager;
    private AodvMessageManager messageManager;
    private RreqBufferManager rreqBufferManager;
    private RrepBufferManager rrepBufferManager;

    private int floodId;
    private int seqNumber = 1;

    public Node(NodeThread thread, Field field) {
        this.nodeThread = thread;
        this.field = field;

        routingTable = new RoutingTable(this);
        fileDistributionTableMap = new HashMap<>();
        chunkManager = new ChunkManager(this);
        routingManager = new AodvRoutingManager(this);
        messageManager = new AodvMessageManager(this);
        rreqBufferManager = new RreqBufferManager(this);
        rrepBufferManager = new RrepBufferManager(this);

    }

    public int getFloodId() {
        return floodId;
    }

    public void setFloodId(int floodId) {
        this.floodId = floodId;
    }

    public int incrementFloodId() {
        return ++floodId;
    }

    public int getSeqNumber() {
        return seqNumber;
    }

    public void setSeqNumber(int seqNumber) {
        this.seqNumber = seqNumber;
    }

    public void incrementSeqNumber() {
        seqNumber++;
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

    public ChunkManager getChunkManager() {
        return chunkManager;
    }

    public RoutingTable getRoutingTable() {
        return routingTable;
    }

    public RreqBufferManager getRreqBufferManager() {
        return rreqBufferManager;
    }

    public RrepBufferManager getRrepBufferManager() {
        return rrepBufferManager;
    }

    public AodvRoutingManager getRoutingManager() {
        return routingManager;
    }

    public AodvMessageManager getMessageManager() {
        return messageManager;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;

        if (isOnline == false) {
            nodeThread.setColor(WHITE_COLOR);
            log.debug(login + ": I'm offline!");
        } else {
            nodeThread.setColor(BLUE_COLOR);
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
        FileDistributionTable table = chunkManager.createAddressTable(file);
        fileDistributionTableMap.put(file, table);
        routingManager.distributeChunks(table);
    }

    public void checkMessageContainer() {
        messageManager.checkMessageContainer();
    }

    public void findNeighbors() {
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
        else if (Math.pow(nodeThread.getX() - n.getNodeThread().getX(), 2) +
                Math.pow(nodeThread.getY() - n.getNodeThread().getY(), 2) <=
                Math.pow(NEIGHBOR_DISTANCE_PARAMETER * RADIUS, 2)) {
            return true;
        }
        return false;
    }

    public void checkNeighbors() {
            routingManager.checkNeighbors();
    }

    //TODO: consider this method and its usage (while checking neighbors, if routing manager discovers link break,
    // it generates RERR, and when any node receives RERR, it checks if any chunk is located there
    // so as I guess there is not necessity in this method and in relevant flag in thread class
    /*public void makeBackup() {
        (messageManager.makeBackup();
    }*/

    @Override
    public String toString() {
        return login;
    }

    public void receiveMessage(IpMessage m) {
        messageManager.receiveMessage(m);
    }
}
