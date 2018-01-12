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
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;


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
            log.debug("{}: I proceed {}", owner, entry);

            RouteEntry route = owner.getRoutingTable().getActualRouteTo(entry.getNode());
            if (route != null) {
                log.debug("{}: I've got a route: {}", owner, route);
                sendChunkAlongTheRoute(entry, route);
            } else {
                log.debug("{}: I don't have a route, starting flood", owner);
                generateRreqFlood(entry.getNode());
            }
        }
    }

    private void sendChunkAlongTheRoute(FDTEntry entry, RouteEntry route) {
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

        Flooder(String destination) {
            this.destination = destination;
        }

        @Override
        public void run() {
            for (int hl = HL_START; hl < HL_THRESHOLD; hl += HL_INCREMENT) {
                if (broadcastRouteRequest(hl)) {
                    return;
                }
            }
            for (int i = 0; i < RREQ_RETRIES; i++) {
                if (broadcastRouteRequest(NET_DIAMETER)) {
                    return;
                }
            }
            log.debug("{}: I stop retrying to broadcast: dest is unreachable", owner);
        }

        private boolean broadcastRouteRequest(int hl) {
            owner.incrementFloodId();
            RouteRequest request = new RouteRequest(owner.getFloodId(), destination, 0, owner.getLogin(), owner.getSeqNumber());
            broadcastRreqToNeighbors(request, hl);
            try {
                Thread.sleep(2 * hl * NODE_TRAVERSAL_TIME);
            } catch (InterruptedException e) {
                log.debug("{}: InterruptedException occurred in Flooder thread", owner);
                return false;
            }
            return owner.getRrepBufferManager().containsNode(destination);
        }
    }

    void broadcastRreqToNeighbors(RouteRequest request, int hopLimit) {
        List<Node> neighbors = findNeighbors();
        log.debug("{}: my neighbors are: {}", owner, Arrays.toString(neighbors.toArray()));

        for (Node neighbor : neighbors) {
            IpMessage ipMessage = new IpMessage(owner.getLogin(), neighbor.getLogin(), request, hopLimit);
            neighbor.receiveMessage(ipMessage);
        }
    }

    void generateAndSendRrepAsDestination(RouteRequest request, RouteEntry reverseRoute) {
        RouteReply reply = new RouteReply(0, request.getDestNode(), owner.getSeqNumber(), request.getSourceNode(),
                MY_ROUTE_TIMEOUT_MILLIS, false);
        log.debug("{}: Sending RREP to {} which generated RREQ for me. Next hop is: {}", owner, reply.getSourceNode(), reverseRoute.getNextHop());
        forwardAodvMessage(reply, reverseRoute.getNextHop(), reverseRoute.getHopCount());
    }

    void generateAndSendRrepAsIntermediate(RouteRequest request, RouteEntry reverseRoute, RouteEntry route) {
        RouteReply reply = new RouteReply(route.getHopCount(), request.getDestNode(), route.getDestSN(), request.getSourceNode(),
                route.getLifeTime() - System.currentTimeMillis(), false);
        forwardAodvMessage(reply, reverseRoute.getNextHop(), reverseRoute.getHopCount());
        sendGratuitousReply(request, route);
    }

    private void sendGratuitousReply(RouteRequest request, RouteEntry route) {
        log.debug("{}: generating GRREP and sending it to {}", owner, request.getDestNode());

        RouteReply gratReply = new RouteReply(request.getHopCount(), request.getSourceNode(), request.getSourceSN(),
                request.getDestNode(), route.getLifeTime(), true);
        forwardAodvMessage(gratReply, route.getNextHop(), route.getHopCount());
    }

    void forwardAodvMessage(AodvMessage message, String nextHop, int hopLimit) {
        IpMessage ipMessage = new IpMessage(owner.getLogin(), nextHop, message, hopLimit);
        Field.getNodeByLogin(nextHop).receiveMessage(ipMessage);
    }

    private List<Node> findNeighbors() {
        List<Node> neighbors = new ArrayList<>();
        int[] nodeEdgesMatrixRow = Field.getEdgesMatrix()[owner.getId()];
        for (int i = 0; i < nodeEdgesMatrixRow.length; i++) {
            if (nodeEdgesMatrixRow[i] == 1) {
                neighbors.add(Field.getNodeById(i));
            }
        }
        return neighbors;
    }

    RouteEntry maintainReverseRoute(RouteRequest rreq, String prevNode) {
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

    RouteEntry maintainDirectRoute(RouteReply reply, String prevNode) {
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
        RouteEntry routeEntry = RouteEntry.builder()
                .destNode(rreq.getSourceNode())
                .destSN(rreq.getSourceSN())
                .nextHop(nextHop)
                .hopCount(rreq.getHopCount() + 1)
                .build();
        long minLifeTime = (System.currentTimeMillis() + REV_ROUTE_LIFE - routeEntry.getHopCount() * NODE_TRAVERSAL_TIME);
        routeEntry.setLifeTime(Math.max(lifeTime, minLifeTime));
        return routeEntry;
    }

    private RouteEntry getNewRouteEntryToDestination(RouteReply reply, String nextHop) {
        return RouteEntry.builder()
                .destNode(reply.getDestNode())
                .destSN(reply.getDestSN())
                .nextHop(nextHop)
                .hopCount(reply.getHopCount() + 1)
                .lifeTime(System.currentTimeMillis() + reply.getLifetime())
                .build();
    }

    void sendChunkToObtainedNode(RouteEntry route) {
        for (FDTEntry entry : fileDistributionTable.getEntriesByNode(route.getDestNode())) {
            log.debug("{}: now I've got route to {} and will send him a chunk", owner, route.getDestNode());
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
        precursors.forEach(precursor -> forwardAodvMessage(routeError, precursor, NET_DIAMETER));
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
                Pair<String, Integer> nodeAndMetrics = owner.getChunkManager().findNodeForChunk(fdtEntry.getChunk());

                if (nodeAndMetrics.getLeft().equals(offlineNode)) {
                    return; // the node has become online again, no need in backup
                } else {
                    String newChunkId = MathOperations.getRandomHexString(DdsConfiguration.ID_LENGTH_IN_HEX);
                    fdtEntry.setChunk(newChunkId);
                    fdtEntry.setNode(nodeAndMetrics.getLeft());
                    fdtEntry.setMetric(nodeAndMetrics.getRight());
                    FDTEntry entry = fileDistributionTable.getAnotherEntryWithChunkCopy(fdtEntry.getOrderNum(), offlineNode);

                    log.debug("{}: sending BackupMessage to {}. He must complete Backup to node {}",
                            owner, entry.getNode(), nodeAndMetrics.getLeft());

                    AodvBackupMessage backupMessage = new AodvBackupMessage(entry.getChunk(), newChunkId,
                            owner.getLogin(), entry.getNode(), nodeAndMetrics.getLeft());
                    RouteEntry actualRoute = owner.getRoutingTable().getActualRouteTo(entry.getNode());

                    if (actualRoute != null) {
                        log.debug("{}: I've got a route to send backup, next hop is {}", owner, actualRoute.getNextHop());
                        forwardAodvMessage(backupMessage, actualRoute.getNextHop(), actualRoute.getHopCount());
                    } else {
                        log.debug("{}: I don't have a route, starting flood", owner);
                        generateRreqFlood(entry.getNode());
                    }
                }
            }
        }
    }
}