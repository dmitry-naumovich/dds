package com.naumovich.manager;

import com.naumovich.configuration.AodvConfiguration;
import com.naumovich.domain.Node;
import com.naumovich.domain.message.aodv.IpMessage;
import com.naumovich.domain.message.aodv.RouteRequest;
import com.naumovich.network.Field;
import com.naumovich.table.FDTEntry;
import com.naumovich.table.FileDistributionTable;
import com.naumovich.table.RouteEntry;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class AodvRoutingManager {

    private Node owner;

    public AodvRoutingManager(Node owner) {
        this.owner = owner;
    }

    public void distributeChunks(FileDistributionTable fileDistributionTable) {
        for (FDTEntry entry : fileDistributionTable) {
            log.debug(owner.getLogin() + ": I proceed " + entry);
            RouteEntry route = owner.getRoutingTable().getActualRouteTo(entry.getNode());
            if (route != null) {
                Node nextHop = Field.getNodeByLogin(route.getNextHop());
                IpMessage ipMessage = new IpMessage(owner.getLogin(), nextHop.getLogin(), entry, route.getHopCount());
                log.debug(owner.getLogin() + ": I've got a route: " + route);
                nextHop.receiveMessage(ipMessage);
            } else {
                log.debug(owner.getLogin() + ": I don't have a route, starting flood");
                generateRreqFlood(entry.getNode());
            }
        }
    }

    // TODO: implement
    private void generateRreqFlood(String destination) {
        owner.incrementFloodId();
        owner.incrementSeqNumber();
        RouteRequest request = new RouteRequest(owner.getFloodId(), destination, 0, owner.getLogin(), owner.getSeqNumber(), true);
        broadcastRouteRequest(request);
    }

    protected void broadcastRouteRequest(RouteRequest request) {
        List<Node> neighbors = findNeighbors();
        log.debug(owner.getLogin() + ": my neighbors are: " + Arrays.toString(neighbors.toArray()));
        for (Node neighbor : neighbors) {
            IpMessage ipMessage = new IpMessage(owner.getLogin(), neighbor.getLogin(), request, AodvConfiguration.TTL_START);
            neighbor.receiveMessage(ipMessage);
        }
    }

    private List<Node> findNeighbors() {
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
    public void checkNodesStatus(FileDistributionTable fileDistributionTable) {

    }
}
