package com.naumovich.manager;

import com.naumovich.configuration.AodvConfiguration;
import com.naumovich.domain.Node;
import com.naumovich.domain.message.aodv.IpMessage;
import com.naumovich.domain.message.aodv.RouteReply;
import com.naumovich.domain.message.aodv.RouteRequest;
import com.naumovich.network.Field;
import com.naumovich.table.FDTEntry;
import com.naumovich.table.FileDistributionTable;
import com.naumovich.table.RouteEntry;
import com.naumovich.table.RoutingTable;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.naumovich.configuration.AodvConfiguration.*;

@Slf4j
public class AodvRoutingManager {

    private Node owner;

    public AodvRoutingManager(Node owner) {
        this.owner = owner;
    }

    public void distributeChunks(FileDistributionTable fileDistributionTable) {
        for (FDTEntry entry : fileDistributionTable) {
            log.debug(owner + ": I proceed " + entry);
            RouteEntry route = owner.getRoutingTable().getActualRouteTo(entry.getNode());
            if (route != null) {
                Node nextHop = Field.getNodeByLogin(route.getNextHop());
                IpMessage ipMessage = new IpMessage(owner.getLogin(), nextHop.getLogin(), entry, route.getHopCount());
                log.debug(owner + ": I've got a route: " + route);
                nextHop.receiveMessage(ipMessage);
            } else {
                log.debug(owner + ": I don't have a route, starting flood");
                generateRreqFlood(entry.getNode());
            }
        }
    }

    private void generateRreqFlood(String destination) {
        owner.incrementFloodId();
        owner.incrementSeqNumber();
        RouteRequest request = new RouteRequest(owner.getFloodId(), destination, 0, owner.getLogin(), owner.getSeqNumber(), true);
        broadcastRouteRequest(request);
    }

    protected void broadcastRouteRequest(RouteRequest request) {
        List<Node> neighbors = findNeighbors();
        log.debug(owner + ": my neighbors are: " + Arrays.toString(neighbors.toArray()));
        for (Node neighbor : neighbors) {
            IpMessage ipMessage = new IpMessage(owner.getLogin(), neighbor.getLogin(), request, AodvConfiguration.TTL_START);
            neighbor.receiveMessage(ipMessage);
        }
    }

    protected void generateAndSendRrepAsDestination(RouteRequest request, RouteEntry reverseRoute) {
        RouteReply reply = new RouteReply(0, request.getDestNode(), owner.getSeqNumber(), request.getSourceNode(),
                MY_ROUTE_TIMEOUT_MILLIS, false);
        IpMessage ipMessage = new IpMessage(owner.getLogin(), reverseRoute.getNextHop(), reply, reverseRoute.getHopCount());
        log.debug(owner + ": Sending RREP to " + reply.getSourceNode() + " which generated RREQ for me. Next hop is: " + reverseRoute.getNextHop());
        Field.getNodeByLogin(reverseRoute.getNextHop()).receiveMessage(ipMessage);
    }

    protected void generateAndSendRrepAsIntermediate(RouteRequest request, RouteEntry reverseRoute, RouteEntry route) {
        RouteReply reply = new RouteReply(route.getHopCount(), request.getDestNode(), route.getDestSN(), request.getSourceNode(),
                route.getLifeTime() - System.currentTimeMillis(), false);
        IpMessage ipMessage = new IpMessage(owner.getLogin(), reverseRoute.getNextHop(), reply, reverseRoute.getHopCount());

        route.addPrecursor(reverseRoute.getNextHop());
        reverseRoute.addPrecursor(route.getNextHop());
        Field.getNodeByLogin(reverseRoute.getNextHop()).receiveMessage(ipMessage);
        // gratuitous RREP
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

    protected RouteEntry maintainReverseRoute(IpMessage rreqIpMessage) {
        RouteRequest rreq = (RouteRequest)rreqIpMessage.getData();
        RoutingTable routingTable = owner.getRoutingTable();
        RouteEntry routeToOrigin = routingTable.getRouteTo(rreq.getSourceNode());
        if (routeToOrigin == null) {
            RouteEntry newRoute = getNewRouteEntryToOrigin(rreqIpMessage, 0);
            owner.getRoutingTable().addEntry(newRoute);
            return newRoute;
        } else {
            if (rreq.getSourceSN() > routeToOrigin.getDestSN() || (rreq.getSourceSN() == routeToOrigin.getDestSN() && rreq.getHopCount() + 1 < routeToOrigin.getHopCount())) {
                RouteEntry updatedRoute = getNewRouteEntryToOrigin(rreqIpMessage, routeToOrigin.getLifeTime());
                routingTable.updateEntry(updatedRoute);
                return updatedRoute;
            }
            return routeToOrigin;
        }
    }

    private RouteEntry getNewRouteEntryToOrigin(IpMessage rreqIpMessage, long lifeTime) {
        RouteRequest rreq = (RouteRequest)rreqIpMessage.getData();
        RouteEntry routeEntry = new RouteEntry();
        routeEntry.setDestNode(rreq.getSourceNode());
        routeEntry.setDestSN(rreq.getSourceSN());
        routeEntry.setNextHop(rreqIpMessage.getSourceNode());
        routeEntry.setHopCount(rreq.getHopCount() + 1);
        long minLifeTime = (System.currentTimeMillis() + REV_ROUTE_LIFE - routeEntry.getHopCount() * NODE_TRAVERSAL_TIME);
        routeEntry.setLifeTime(Math.max(lifeTime, minLifeTime));
        routeEntry.setLastHopCount(rreq.getHopCount() + 1);
        return routeEntry;
    }

    // TODO: implement it
    public void checkNeighbors() {

    }
}
