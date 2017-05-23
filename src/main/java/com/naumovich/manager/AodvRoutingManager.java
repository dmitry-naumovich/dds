package com.naumovich.manager;

import com.naumovich.domain.Chunk;
import com.naumovich.domain.Node;
import com.naumovich.domain.message.aodv.*;
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
    //TODO: temp solution to provide sendChunkToObtainedNode method with FDT
    private FileDistributionTable fileDistributionTable;

    public AodvRoutingManager(Node owner) {
        this.owner = owner;
    }

    public void distributeChunks(FileDistributionTable fileDistributionTable) {
        this.fileDistributionTable = fileDistributionTable;

        for (FDTEntry entry : fileDistributionTable) {
            log.debug(owner + ": I proceed " + entry);

            RouteEntry route = owner.getRoutingTable().getActualRouteTo(entry.getNode());
            if (route != null) {
                sendChunkAlongTheRoute(entry, route);
            } else {
                log.debug(owner + ": I don't have a route, starting flood");
                generateRreqFlood(entry.getNode());
            }
        }
    }

    private void sendChunkAlongTheRoute(FDTEntry entry, RouteEntry route) {
        Chunk chunkToSend = owner.getChunkStorage().extractChunkByName(entry.getChunk());
        if (chunkToSend != null) {
            Node nextHop = Field.getNodeByLogin(route.getNextHop());
            AodvChunkMessage chunkMessage = new AodvChunkMessage(entry.getNode(), chunkToSend);
            IpMessage ipMessage = new IpMessage(owner.getLogin(), nextHop.getLogin(), chunkMessage, route.getHopCount());
            log.debug(owner + ": I've got a route: " + route);
            nextHop.receiveMessage(ipMessage);
        }
    }

    //TODO: expanding ring search technique in separate thread
    private void generateRreqFlood(String destination) {
        owner.incrementSeqNumber();
        RouteRequest request = new RouteRequest(owner.getFloodId() + 1, destination, 0, owner.getLogin(), owner.getSeqNumber());
        owner.incrementFloodId();
        broadcastRouteRequest(request, NET_DIAMETER);
        /*for (int hl = HL_START; hl < HL_THRESHOLD; hl += HL_INCREMENT) {
            owner.incrementFloodId();
            broadcastRouteRequest(request, hl);
            try {
                Thread.sleep(2 * hl * NODE_TRAVERSAL_TIME);
            } catch (InterruptedException e) {
                // TODO: smth
            }
            if (owner.getRrepBufferManager().containsNode(destination)) {
                return;
            }
        }

        for (int i = 0, hl = NET_DIAMETER; i < RREQ_RETRIES; i++) {
            owner.incrementFloodId();
            broadcastRouteRequest(request, hl);
            try {
                Thread.sleep(2 * hl * NODE_TRAVERSAL_TIME);
            } catch (InterruptedException e) {
                // TODO: smth
            }
            if (owner.getRrepBufferManager().containsNode(destination)) {
                return;
            }
        }*/
    }

    protected void broadcastRouteRequest(RouteRequest request, int hopLimit) {
        List<Node> neighbors = findNeighbors();
        log.debug(owner + ": my neighbors are: " + Arrays.toString(neighbors.toArray()));
        for (Node neighbor : neighbors) {
            IpMessage ipMessage = new IpMessage(owner.getLogin(), neighbor.getLogin(), request, hopLimit);
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

        Field.getNodeByLogin(reverseRoute.getNextHop()).receiveMessage(ipMessage);
        sendGratuitousReply(request, route);
    }

    private void sendGratuitousReply(RouteRequest request, RouteEntry route) {
        log.debug(owner + ": generating GRREP and sending it to " + request.getDestNode());

        RouteReply gratReply = new RouteReply(request.getHopCount(), request.getSourceNode(), request.getSourceSN(),
                request.getDestNode(), route.getLifeTime(), true);
        IpMessage ipMessage = new IpMessage(owner.getLogin(), route.getNextHop(), gratReply, route.getHopCount());
        Field.getNodeByLogin(route.getNextHop()).receiveMessage(ipMessage);
    }

    protected void forwardAodvMessage(AodvMessage message, String nextHop, int hopLimit) {
        IpMessage ipMessage = new IpMessage(owner.getLogin(), nextHop, message, hopLimit);

        Field.getNodeByLogin(nextHop).receiveMessage(ipMessage);
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

    protected RouteEntry maintainReverseRoute(RouteRequest rreq, String prevNode) {
        RoutingTable routingTable = owner.getRoutingTable();
        RouteEntry routeToOrigin = routingTable.getRouteTo(rreq.getSourceNode());
        if (routeToOrigin == null) {
            RouteEntry newRoute = getNewRouteEntryToOrigin(rreq,  prevNode, 0);
            owner.getRoutingTable().addEntry(newRoute);
            return newRoute;
        } else {
            if (rreq.getSourceSN() > routeToOrigin.getDestSN() || (rreq.getSourceSN() == routeToOrigin.getDestSN() && rreq.getHopCount() + 1 < routeToOrigin.getHopCount())) {
                RouteEntry updatedRoute = getNewRouteEntryToOrigin(rreq, prevNode, routeToOrigin.getLifeTime());
                routingTable.updateEntry(updatedRoute);
                return updatedRoute;
            }
            return routeToOrigin;
        }
    }

    protected RouteEntry maintainDirectRoute(RouteReply reply, String prevNode) {
        RoutingTable routingTable = owner.getRoutingTable();
        RouteEntry routeToDestination = routingTable.getRouteTo(reply.getDestNode());
        if (routeToDestination == null) {
            RouteEntry newRoute = getNewRouteEntryToDestination(reply,  prevNode);
            owner.getRoutingTable().addEntry(newRoute);
            return newRoute;
        } else {
            if (reply.getDestSN() > routeToDestination.getDestSN() || (reply.getDestSN() == routeToDestination.getDestSN() && reply.getHopCount() + 1 < routeToDestination.getHopCount())) {
                RouteEntry updatedRoute = getNewRouteEntryToDestination(reply, prevNode);
                routingTable.updateEntry(updatedRoute);
                return updatedRoute;
            }
            return routeToDestination;
        }
    }

    private RouteEntry getNewRouteEntryToOrigin(RouteRequest rreq, String nextHop, long lifeTime) {
        RouteEntry routeEntry = new RouteEntry();
        routeEntry.setDestNode(rreq.getSourceNode());
        routeEntry.setDestSN(rreq.getSourceSN());
        routeEntry.setNextHop(nextHop);
        routeEntry.setHopCount(rreq.getHopCount() + 1);
        long minLifeTime = (System.currentTimeMillis() + REV_ROUTE_LIFE - routeEntry.getHopCount() * NODE_TRAVERSAL_TIME);
        routeEntry.setLifeTime(Math.max(lifeTime, minLifeTime));
        routeEntry.setLastHopCount(rreq.getHopCount() + 1);
        return routeEntry;
    }

    private RouteEntry getNewRouteEntryToDestination(RouteReply reply, String nextHop) {
        RouteEntry routeEntry = new RouteEntry();
        routeEntry.setDestNode(reply.getDestNode());
        routeEntry.setDestSN(reply.getDestSN());
        routeEntry.setNextHop(nextHop);
        routeEntry.setHopCount(reply.getHopCount() + 1);
        routeEntry.setLifeTime(System.currentTimeMillis() + reply.getLifetime());
        routeEntry.setLastHopCount(reply.getHopCount() + 1);
        return routeEntry;
    }

    protected void sendChunkToObtainedNode(RouteEntry route) {
        for (FDTEntry entry : fileDistributionTable.getEntriesByNode(route.getDestNode())) {
            log.debug(owner + ": now I've got route to " + route.getDestNode() + " and will send him a chunk");
            sendChunkAlongTheRoute(entry, route);
        }
    }

    // TODO: implement it
    public void checkNeighbors() {

    }
}
