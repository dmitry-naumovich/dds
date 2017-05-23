package com.naumovich.manager;

import com.naumovich.domain.Node;
import com.naumovich.domain.message.aodv.*;
import com.naumovich.network.Field;
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
            switch (message.getMessageType()) {
                case 1:
                    proceedRouteRequest((RouteRequest)message.getData(), message.getSourceNode(), message.getHl());
                    break;
                case 2:
                    proceedRouteReply((RouteReply)message.getData(), message.getSourceNode(), message.getHl());
                    break;
                case 3:
                    proceedRouteError((RouteError)message.getData());
                    break;
                case 4:
                    proceedChunkMessage((AodvChunkMessage)message.getData(), message.getHl());
                    break;
                case 5:
                    proceedBackupMessage((AodvBackupMessage)message.getData());
                    break;
                default:
                    break;
            }
        }
    }



    private void proceedChunkMessage(AodvChunkMessage chunkMessage, int hl) {
        if (chunkMessage.getDestNode().equals(owner.getLogin())) {
            log.debug(owner + ": I've received Chunk and save it to my local storage");
            owner.getChunkStorage().add(chunkMessage.getChunk());
        } else if (hl > 1) {
            RouteEntry route = owner.getRoutingTable().getActualRouteTo(chunkMessage.getDestNode());
            if (route != null) {
                routingManager.forwardAodvMessage(chunkMessage, route.getNextHop(), --hl);
            }
        }
    }

    private void proceedBackupMessage(AodvBackupMessage m) {

    }

    private void proceedRouteRequest(RouteRequest request, String prevNode, int hl) {
        if (request.getDestNode().equals(owner.getLogin()) && !owner.getRreqBufferManager().containsRreq(request)) {
            log.debug(owner + ": received RREQ, I'm destination - generating RREP");
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
                log.debug(owner + ": received RREQ, I'm intermediate, I broadcast it further");

                request.incrementHopCount();
                owner.incrementAmountOfRetransmitted();
                routingManager.broadcastRouteRequest(request, --hl);

            } else if (route != null && route.getDestSN() >= request.getDestSN()){
                log.debug(owner + ": received RREQ, I'm intermediate, know route to dest - generating RREP to " + request.getSourceNode() + ". Next hop is: " + reverseRoute.getNextHop());

                route.addPrecursor(reverseRoute.getNextHop());
                reverseRoute.addPrecursor(route.getNextHop());
                routingManager.generateAndSendRrepAsIntermediate(request, reverseRoute, route);
            }
        }
    }

    private void proceedRouteReply(RouteReply reply, String prevNode, int hl) {
        if (reply.getSourceNode().equals(owner.getLogin())) {
            log.debug(owner + ": received RREP, this is a reply for me");

            owner.getRrepBufferManager().addNodeToBuffer(reply.getDestNode());
            RouteEntry route = routingManager.maintainDirectRoute(reply, prevNode);
            if (!reply.isgFlag()) {
                routingManager.sendChunkToObtainedNode(route);
            }

        } else if (hl > 1) {
            log.debug(owner + ": received RREP for " + reply.getSourceNode() + ", I'm intermediate, I maintain direct route and forward this further");

            String nextHop = owner.getRoutingTable().getActualRouteTo(reply.getSourceNode()).getNextHop();

            RouteEntry route = routingManager.maintainDirectRoute(reply, prevNode);
            route.addPrecursor(nextHop);
            reply.incrementHopCount();
            owner.incrementAmountOfRetransmitted();

            routingManager.forwardAodvMessage(reply, nextHop, --hl);
        }
    }

    private void proceedRouteError(RouteError error) {

    }
}
