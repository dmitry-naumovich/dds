package com.naumovich.manager;

import com.naumovich.configuration.AodvConfiguration;
import com.naumovich.domain.Node;
import com.naumovich.domain.message.aodv.IpMessage;
import com.naumovich.domain.message.aodv.RouteRequest;
import com.naumovich.network.Field;
import com.naumovich.table.AddressTable;
import com.naumovich.table.AddressTableEntry;
import com.naumovich.table.RouteEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dzmitry on 4.5.17.
 */
public class AodvRoutingManager implements  RoutingManager {

    @Override
    public void distributeChunks(Node owner, AddressTable addressTable) {
        for (AddressTableEntry entry : addressTable) {
            RouteEntry route = getRouteIfActual(entry.getNode(), owner.getRoutingTable());
            if (route != null) {
                Node nextHop = Field.getNodeByLogin(route.getNextHop());
                IpMessage ipMessage = new IpMessage(owner.getLogin(), nextHop.getLogin(), entry, route.getHopCount());
                nextHop.receiveMessage(ipMessage);
            } else {
                generateRreqFlood(owner, entry.getNode());
            }
        }
    }

    private RouteEntry getRouteIfActual(String node, List<RouteEntry> routingTable) {
        for (RouteEntry entry : routingTable) {
            if (entry.getDestinationNode().equals(node) && entry.getDestinationSequenceNum() > 0) {
                return entry;
            }
        }
        return null;
    }

    // TODO: implement
    private void generateRreqFlood(Node owner, String destination) {
        owner.incrementFloodId();
        owner.incrementSeqNumber();
        RouteRequest request = new RouteRequest(owner.getFloodId(), destination, 0, owner.getLogin(), owner.getSeqNumber(), true);
        List<Node> neighbors = findNeighbors(owner);
        for (Node neighbor : neighbors) {
            IpMessage ipMessage = new IpMessage(owner.getLogin(), neighbor.getLogin(), request, AodvConfiguration.TTL_START);
            neighbor.receiveMessage(ipMessage);
        }
    }

    private List<Node> findNeighbors(Node owner) {
        List<Node> neighbors = new ArrayList<>();
        int[] nodeEdgesMatrixRow = Field.getEdgesMatrix()[owner.getPersNum()];
        for (int i = 0; i < nodeEdgesMatrixRow.length; i++) {
            if (nodeEdgesMatrixRow[i] == 1) {
                neighbors.add(Field.getNodeByPersNum(i));
            }
        }
        return neighbors;
    }

    // TODO: implement it
    @Override
    public void checkNodesStatus(Node owner, AddressTable addressTable) {

    }
}
