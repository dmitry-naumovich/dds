package com.naumovich.manager;

import com.naumovich.domain.Chunk;
import com.naumovich.domain.Node;
import com.naumovich.domain.message.aodv.*;
import com.naumovich.table.RouteEntry;
import lombok.extern.slf4j.Slf4j;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class AodvMessageManager {

    private Node owner;
    private Queue<IpMessage> queue = new ConcurrentLinkedQueue<>();
    private AodvRoutingManager routingManager;

    public AodvMessageManager(Node owner) {
        this.owner = owner;
        routingManager = owner.getRoutingManager();
    }

    public void receiveMessage(IpMessage message) {
        queue.offer(message);
    }

    public void checkMessageContainer() {
        IpMessage message = queue.poll();
        if (message != null) {
            owner.incrementAmountOfMsgChecks();
            AodvMessage data = message.getData();
            String source = message.getSourceNode();
            int hl = message.getHl();

            switch (message.getMessageType()) {
                case 1:
                    proceedRouteRequest((RouteRequest)data, source, hl);
                    break;
                case 2:
                    proceedRouteReply((RouteReply)data, source, hl);
                    break;
                case 3:
                    proceedRouteError((RouteError)data, source);
                    break;
                case 4:
                    proceedChunkMessage((AodvChunkMessage)data, hl);
                    break;
                case 5:
                    proceedBackupMessage((AodvBackupMessage)data, hl);
                    break;
                default:
                    break;
            }
        }
    }



    private void proceedChunkMessage(AodvChunkMessage chunkMessage, int hl) {
        if (chunkMessage.getDestNode().equals(owner.getLogin())) {
            log.debug("{}: received Chunk and save it to my local storage", owner);
            owner.getChunkStorage().add(chunkMessage.getChunk());
        } else if (hl > 1) {
            RouteEntry route = owner.getRoutingTable().getActualRouteTo(chunkMessage.getDestNode());
            if (route != null) {
                routingManager.forwardAodvMessage(chunkMessage, route.getNextHop(), --hl);
            }
        }
    }

    private void proceedBackupMessage(AodvBackupMessage backupMessage, int hl) {
        String newChunkSaver = backupMessage.getNewChunkSaver();

        if (backupMessage.getDestNode().equals(owner.getLogin())) {
            log.debug("{}: received backupMessage, I must back up the chunk to {}", owner, newChunkSaver);
            Chunk existingChunk = owner.getChunkStorage().getChunkByName(backupMessage.getChunkId());
            Chunk newChunkCopy = new Chunk(existingChunk, backupMessage.getNewChunkId());
            owner.getChunkStorage().add(newChunkCopy);

            RouteEntry route = owner.getRoutingTable().getActualRouteTo(newChunkSaver);
            if (route != null) {
                log.debug("{}: I've got a route: {}", owner, route);
                owner.getRoutingManager().sendChunkAlongTheRoute(newChunkCopy.getChunkName(), route);
            } else {
                log.debug("{}: I don't have a route, starting flood");
                owner.getRoutingManager().generateRreqFlood(newChunkSaver);
            }
        } else if (hl > 1) {
            log.debug("{}: received backupMessage, I'm intermediate, transmit it further", owner);

            String nextHop = owner.getRoutingTable().getActualRouteTo(newChunkSaver).getNextHop();
            owner.incrementAmountOfRetransmitted();

            routingManager.forwardAodvMessage(backupMessage, nextHop, --hl);
        }
    }

    private void proceedRouteRequest(RouteRequest request, String prevNode, int hl) {
        if (request.getDestNode().equals(owner.getLogin()) && !owner.getRreqBufferManager().containsRreq(request)) {
            log.debug("{}: received RREQ, I'm destination - generating RREP", owner);
            owner.getRreqBufferManager().addRequestToBuffer(request);

            RouteEntry reverseRoute = routingManager.maintainReverseRoute(request, prevNode);
            if (request.getDestSN() > owner.getSeqNumber()) {
                owner.setSeqNumber(request.getDestSN());
            }
            routingManager.generateAndSendRrepAsDestination(request, reverseRoute);

        } else if (!request.getSourceNode().equals(owner.getLogin()) && !owner.getRreqBufferManager().containsRreq(request)) {
            owner.getRreqBufferManager().addRequestToBuffer(request);

            RouteEntry reverseRoute = routingManager.maintainReverseRoute(request, prevNode);
            RouteEntry route = owner.getRoutingTable().getActualRouteTo(request.getDestNode());

            if ((route == null && hl > 1) || (route != null && route.getDestSN() < request.getDestSN() && hl > 1)) {
                log.debug("{}: received RREQ, I'm intermediate, I broadcast it further", owner);

                request.incrementHopCount();
                owner.incrementAmountOfRetransmitted();
                routingManager.broadcastRreqToNeighbors(request, --hl);

            } else if (route != null && route.getDestSN() >= request.getDestSN()){
                log.debug("{}: received RREQ, I'm intermediate, know route to dest - generating RREP to {}. Next hop is: {}",
                        owner, request.getSourceNode(), reverseRoute.getNextHop());

                route.addPrecursor(reverseRoute.getNextHop());
                reverseRoute.addPrecursor(route.getNextHop());
                routingManager.generateAndSendRrepAsIntermediate(request, reverseRoute, route);
            } else {
                log.debug("{}: received RREQ, I'm intermediate, but I discard as hl <= 1", owner);
            }
        }
    }

    private void proceedRouteReply(RouteReply reply, String prevNode, int hl) {
        if (reply.getSourceNode().equals(owner.getLogin())) {
            log.debug("{}: received RREP, this is a reply for me", owner);

            owner.getRrepBufferManager().addNodeToBuffer(reply.getDestNode());
            RouteEntry route = routingManager.maintainDirectRoute(reply, prevNode);
            if (!reply.isGFlag()) {
                routingManager.sendChunkToObtainedNode(route);
            }

        } else if (hl > 1) {
            log.debug("{}: received RREP for {}, I'm intermediate, I maintain direct route and forward this further",
                    owner, reply.getSourceNode());

            String nextHop = owner.getRoutingTable().getActualRouteTo(reply.getSourceNode()).getNextHop();

            RouteEntry route = routingManager.maintainDirectRoute(reply, prevNode);
            route.addPrecursor(nextHop);
            reply.incrementHopCount();
            owner.incrementAmountOfRetransmitted();

            routingManager.forwardAodvMessage(reply, nextHop, --hl);
        }
    }

    private void proceedRouteError(RouteError error, String prevNode) {
        String offlineNode = error.getOffNode();
        String destNode = error.getDestNode();
        log.debug("{}: received RERR, offline node is = {} and unachievable is {}", owner, offlineNode, destNode);

        AodvRoutingManager routingManager = owner.getRoutingManager();
        for (RouteEntry route : owner.getRoutingTable()) {
            if (route.getDestNode().equals(destNode) && route.getNextHop().equals(prevNode)) {
                route.setDestSN(error.getDestSN());
                routingManager.sendRerrToPrecursors(error, route.getPrecursors());
                routingManager.invalidateRoute(route);
                routingManager.delegateBackupIfNecessary(offlineNode);
            }
            if (route.getDestNode().equals(offlineNode) && route.getHopCount() > 0) {
                routingManager.invalidateRoute(route);
            }
        }
    }
}
