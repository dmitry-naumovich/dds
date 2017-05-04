package com.naumovich.domain;

import java.util.*;

import com.naumovich.domain.message.Message;
import com.naumovich.manager.*;
import com.naumovich.network.*;
import com.naumovich.table.AddressTable;
import com.naumovich.table.RouteEntry;
import com.naumovich.util.MathOperations;
import lombok.extern.slf4j.Slf4j;

import static com.naumovich.configuration.FieldConfiguration.BLUE_COLOR;
import static com.naumovich.configuration.FieldConfiguration.WHITE_COLOR;

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

    public List<RouteEntry> getRoutingTable() {
        return routingTable;
    }

    private List<RouteEntry> routingTable = new ArrayList<>();
    private boolean isOnline = true;

    private int amountOfRetransmitted;
    private long amountOfMsgChecks;
    private long amountOfNodeStatusChecks;
    private long amountOfFindingPath;

    private Map<File, AddressTable> addressTableMap;

    private ChunkManager chunkManager;
    private RoutingManager routingManager;
    private DijkstraMessageManager dijkstraMessageManager;
    private AodvMessageManager aodvMessageManager;

    public Node(NodeThread thread, Field field) {
        this.nodeThread = thread;
        this.field = field;

        addressTableMap = new HashMap<>();
        chunkManager = new ChunkManager(this);
        routingManager = new DijkstraRoutingManager();
        dijkstraMessageManager = new DijkstraMessageManager(this);
        aodvMessageManager = new AodvMessageManager();
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
        AddressTable table = chunkManager.createAddressTable(file);
        addressTableMap.put(file, table);
        routingManager.distributeChunks(this, table);
    }

    public void checkMessageContainer() {
        dijkstraMessageManager.checkMessageContainer();
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
            return true;
        }
        return false;
    }

    public void checkNodesStatus() {
        Iterator it = addressTableMap.entrySet().iterator();
        while (it.hasNext()) {
            routingManager.checkNodesStatus(this, (AddressTable)((Map.Entry)it.next()).getValue());
        }
    }

    public void makeBackup() {
        dijkstraMessageManager.makeBackup();
    }

    @Override
    public String toString() {
        return login;
    }

    public void receiveMessage(Message m) {
        aodvMessageManager.receiveMessage(this, m);
    }
}
