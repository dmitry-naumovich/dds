package com.naumovich.manager;

import com.naumovich.configuration.DdsConfiguration;
import com.naumovich.domain.Chunk;
import com.naumovich.domain.Node;
import com.naumovich.domain.message.aodv.*;
import com.naumovich.network.Field;
import com.naumovich.table.FDTEntry;
import com.naumovich.table.FileDistributionTable;
import com.naumovich.table.RouteEntry;
import com.naumovich.table.RoutingTable;
import com.naumovich.util.MathOperations;
import com.naumovich.util.tuple.TwoTuple;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
                log.debug(owner + ": I've got a route: " + route);
                sendChunkAlongTheRoute(entry, route);
            } else {
                log.debug(owner + ": I don't have a route, starting flood");
                generateRreqFlood(entry.getNode());
            }
        }
    }

    void sendChunkAlongTheRoute(FDTEntry entry, RouteEntry route) {
        Chunk chunkToSend = owner.getChunkStorage().extractChunkByName(entry.getChunk());
        if (chunkToSend != null) {
            AodvChunkMessage chunkMessage = new AodvChunkMessage(entry.getNode(), chunkToSend);
            forwardAodvMessage(chunkMessage, route.getNextHop(), route.getHopCount());
        }
    }

    void sendChunkAlongTheRoute(String chunkName, RouteEntry route) {
        Chunk chunkToSend = owner.getChunkStorage().extractChunkByName(chunkName);
        if (chunkToSend != null) {
            AodvChunkMessage chunkMessage = new AodvChunkMessage(route.getDestNode(), chunkToSend);
            forwardAodvMessage(chunkMessage, route.getNextHop(), route.getHopCount());
        }
    }

    void generateRreqFlood(String destination) {
        owner.incrementSeqNumber();
        Thread flooder = new Thread(new Flooder(destination));
        flooder.start();
    }

    class Flooder implements Runnable {

        private String destination;

        public Flooder(String destination) {
            this.destination = destination;
        }

        @Override
        public void run() {
            for (int hl = HL_START; hl < HL_THRESHOLD; hl += HL_INCREMENT) {
                if (broadcastRouteRequest(hl)) {
                    return;
                }
            }
            for (int i = 0, hl = NET_DIAMETER; i < RREQ_RETRIES; i++) {
                if (broadcastRouteRequest(hl)) {
                    return;
                }
            }
            log.debug(owner + ": I stop retrying to broadcast: dest is unreachable");
        }

        private boolean broadcastRouteRequest(int hl) {
            owner.incrementFloodId();
            RouteRequest request = new RouteRequest(owner.getFloodId(), destination, 0, owner.getLogin(), owner.getSeqNumber());
            broadcastRreqToNeighbors(request, hl);
            try {
                Thread.sleep(2 * hl * NODE_TRAVERSAL_TIME);
            } catch (InterruptedException e) {
                log.debug(owner + ": InterruptedException occurred in Flooder thread");
                return false;
            }
            if (owner.getRrepBufferManager().containsNode(destination)) {
                return true;
            }
            return false;
        }
    }

    protected void broadcastRreqToNeighbors(RouteRequest request, int hopLimit) {
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
        log.debug(owner + ": Sending RREP to " + reply.getSourceNode() + " which generated RREQ for me. Next hop is: " + reverseRoute.getNextHop());
        forwardAodvMessage(reply, reverseRoute.getNextHop(), reverseRoute.getHopCount());
    }

    protected void generateAndSendRrepAsIntermediate(RouteRequest request, RouteEntry reverseRoute, RouteEntry route) {
        RouteReply reply = new RouteReply(route.getHopCount(), request.getDestNode(), route.getDestSN(), request.getSourceNode(),
                route.getLifeTime() - System.currentTimeMillis(), false);
        forwardAodvMessage(reply, reverseRoute.getNextHop(), reverseRoute.getHopCount());
        sendGratuitousReply(request, route);
    }

    private void sendGratuitousReply(RouteRequest request, RouteEntry route) {
        log.debug(owner + ": generating GRREP and sending it to " + request.getDestNode());

        RouteReply gratReply = new RouteReply(request.getHopCount(), request.getSourceNode(), request.getSourceSN(),
                request.getDestNode(), route.getLifeTime(), true);
        forwardAodvMessage(gratReply, route.getNextHop(), route.getHopCount());
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
        return routeEntry;
    }

    private RouteEntry getNewRouteEntryToDestination(RouteReply reply, String nextHop) {
        RouteEntry routeEntry = new RouteEntry();
        routeEntry.setDestNode(reply.getDestNode());
        routeEntry.setDestSN(reply.getDestSN());
        routeEntry.setNextHop(nextHop);
        routeEntry.setHopCount(reply.getHopCount() + 1);
        routeEntry.setLifeTime(System.currentTimeMillis() + reply.getLifetime());
        return routeEntry;
    }

    protected void sendChunkToObtainedNode(RouteEntry route) {
        for (FDTEntry entry : fileDistributionTable.getEntriesByNode(route.getDestNode())) {
            log.debug(owner + ": now I've got route to " + route.getDestNode() + " and will send him a chunk");
            sendChunkAlongTheRoute(entry, route);
        }
    }

    public void checkNodesStatus() {
        for (RouteEntry route : owner.getRoutingTable()) {
            owner.incrementAmountOfNodeStatusChecks();
            if (route.getHopCount() > 0 && !Field.getNodeByLogin(route.getNextHop()).isOnline()) {
                RouteError routeError = new RouteError(route.getDestNode(), route.getDestSN(), route.getNextHop());
                sendRerrToPrecursors(routeError, route.getPrecursors());
                invalidateRoute(route);
                rmOfflineNodeFromAllPrecursorLists(route.getNextHop());
            }
        }
    }

    void invalidateRoute(RouteEntry route) {
        route.setLastHopCount(route.getHopCount());
        route.setHopCount(-1);
        route.setPrecursors(Collections.emptyList());
    }

    void sendRerrToPrecursors(RouteError routeError, List<String> precursors) {
        for (String precursor : precursors) {
            forwardAodvMessage(routeError, precursor, NET_DIAMETER); //todo consider what ttl?
        }
    }

    private void rmOfflineNodeFromAllPrecursorLists(String offlineNode) {
        for (RouteEntry route : owner.getRoutingTable()) {
            if (route.getPrecursors().contains(offlineNode)) {
                route.removePrecursor(offlineNode);
            }
        }
    }

    void delegateBackupIfNecessary(String offlineNode) {
        if (fileDistributionTable != null) {
            for (FDTEntry fdtEntry : fileDistributionTable.getEntriesByNode(offlineNode)) {
                TwoTuple<String, Integer> nodeAndMetrics = owner.getChunkManager().findNodeForChunk(fdtEntry.getChunk());

                if (nodeAndMetrics.first.equals(offlineNode)) {
                    return; // the node has become online again, no need in backup
                } else {
                    String newChunkId = MathOperations.getRandomHexString(DdsConfiguration.ID_LENGTH_IN_HEX);
                    fdtEntry.setChunk(newChunkId);
                    fdtEntry.setNode(nodeAndMetrics.first);
                    fdtEntry.setMetric(nodeAndMetrics.second);
                    FDTEntry entry = fileDistributionTable.getAnotherEntryWithChunkCopy(fdtEntry.getOrderNum(), offlineNode);
                    log.debug(owner + ": sending BackupMessage to " + entry.getNode() + ". He must complete Backup to node " + nodeAndMetrics.first);

                    AodvBackupMessage backupMessage = new AodvBackupMessage(entry.getChunk(), newChunkId, owner.getLogin(), entry.getNode(), nodeAndMetrics.first);
                    RouteEntry actualRoute = owner.getRoutingTable().getActualRouteTo(entry.getNode());
                    if (actualRoute != null) {
                        log.debug(owner + ": I've got a route to send backup, nexthop is " + actualRoute.getNextHop());
                        forwardAodvMessage(backupMessage, actualRoute.getNextHop(), actualRoute.getHopCount());
                    } else {
                        log.debug(owner + ": I don't have a route, starting flood");
                        generateRreqFlood(entry.getNode());
                    }
                }
            }
        }
    }
}