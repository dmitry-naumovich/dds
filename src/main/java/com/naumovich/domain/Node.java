package com.naumovich.domain;

import java.util.*;

import com.naumovich.domain.message.aodv.IpMessage;
import com.naumovich.manager.*;
import com.naumovich.network.*;
import com.naumovich.table.FileDistributionTable;
import com.naumovich.table.RoutingTable;
import com.naumovich.util.MathOperations;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import static com.naumovich.configuration.ModelConfiguration.*;

/**
 * This class describes the node entity
 */
@Data
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

    private RoutingTable routingTable = new RoutingTable(this);
    private Map<File, FileDistributionTable> fileDistributionTableMap = new HashMap<>();

    private ChunkManager chunkManager = new ChunkManager(this);
    private AodvRoutingManager routingManager = new AodvRoutingManager(this);
    private AodvMessageManager messageManager = new AodvMessageManager(this);
    private RreqBufferManager rreqBufferManager = new RreqBufferManager(this);
    private RrepBufferManager rrepBufferManager = new RrepBufferManager(this);

    private int floodId;
    private int seqNumber = 1;

    public Node(NodeThread thread, Field field) {
        this.nodeThread = thread;
        this.field = field;
    }

    public void incrementFloodId() {
        floodId++;
    }

    public void incrementSeqNumber() {
        seqNumber++;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
        if (!isOnline) {
            nodeThread.setColor(WHITE_COLOR);
            log.debug(login + ": I'm offline!");
        } else {
            nodeThread.setColor(BLUE_COLOR);
        }
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

    void distributeFile(File file) {
        FileDistributionTable table = chunkManager.createAddressTable(file);
        fileDistributionTableMap.put(file, table);
        routingManager.distributeChunks(table);
    }

    void checkMessageContainer() {
        messageManager.checkMessageContainer();
    }

    void checkNodesStatus() {
        routingManager.checkNodesStatus();
    }

    void findNeighbors() {
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

    private boolean isNeighborWith(Node n) {
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

    @Override
    public String toString() {
        return login;
    }

    public void receiveMessage(IpMessage m) {
        messageManager.receiveMessage(m);
    }

}
