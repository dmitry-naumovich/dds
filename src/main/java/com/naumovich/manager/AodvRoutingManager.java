package com.naumovich.manager;

import com.naumovich.domain.Node;
import com.naumovich.domain.message.aodv.AodvChunkMessage;
import com.naumovich.domain.message.aodv.AodvMessage;
import com.naumovich.domain.message.aodv.IpMessage;
import com.naumovich.network.Field;
import com.naumovich.table.AddressTable;
import com.naumovich.table.AddressTableEntry;
import com.naumovich.table.RouteEntry;

import java.util.List;

/**
 * Created by dzmitry on 4.5.17.
 */
public class AodvRoutingManager implements  RoutingManager {

    @Override
    public void distributeChunks(Node owner, AddressTable addressTable) {
        for (AddressTableEntry entry : addressTable) {
            String nextHop = getNextHopIfPresent(entry.getNode().getLogin(), owner.getRoutingTable());
            if (nextHop != null) {
                AodvMessage chunkMessage = new AodvChunkMessage(entry.getChunk());
                IpMessage ipMessage = new IpMessage(); // change constructor in ipmessage
                Field.getNodeByLogin(nextHop).receiveMessage(ipMessage);
            } else {
                generateRreqFlood();
            }
        }
    }

    private String getNextHopIfPresent(String node, List<RouteEntry> routingTable) {
        for (RouteEntry entry : routingTable) {
            if (entry.getDestinationNode().equals(node) && entry.getDestinationSequenceNum() >= 0) {
                return entry.getNextHop();
            }
        }
        return null;
    }

    // TODO: implement
    private void generateRreqFlood() {
        // RREQ = new RREQ
        // findNeighbors
        // for each neighbor send rreq
    }

    // TODO: implement it
    @Override
    public void checkNodesStatus(Node owner, AddressTable addressTable) {

    }
}
