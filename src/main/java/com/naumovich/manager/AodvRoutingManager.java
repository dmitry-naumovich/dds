package com.naumovich.manager;

import com.naumovich.domain.Node;
import com.naumovich.domain.message.aodv.IpMessage;
import com.naumovich.domain.message.aodv.RouteRequest;
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
            Node nextHop = getNextHopIfPresent(entry.getNode(), owner.getRoutingTable());
            if (nextHop != null) {
                IpMessage ipMessage = new IpMessage(owner, nextHop, entry);
                nextHop.receiveMessage(ipMessage);
            } else {
                generateRreqFlood(owner, entry.getNode());
            }
        }
    }

    private Node getNextHopIfPresent(String node, List<RouteEntry> routingTable) {
        for (RouteEntry entry : routingTable) {
            if (entry.getDestinationNode().equals(node) && entry.getDestinationSequenceNum() > 0) {
                return Field.getNodeByLogin(entry.getNextHop());
            }
        }
        return null;
    }

    // TODO: implement
    private void generateRreqFlood(Node owner, String destination) {
        owner.incrementFloodId();
        owner.incrementSeqNumber();
        RouteRequest request = new RouteRequest(owner.getFloodId(), destination, 0, owner.getLogin(), owner.getSeqNumber(), true);
        // findNeighbors
        // for each neighbor send rreq
    }

    // TODO: implement it
    @Override
    public void checkNodesStatus(Node owner, AddressTable addressTable) {

    }
}
