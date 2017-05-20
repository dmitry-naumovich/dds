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

    public void checkMessageContainer() {
        IpMessage message = queue.poll();
        if (message != null) {
            owner.incrementAmountOfMsgChecks();
            switch (message.getMessageType()) {
                case 1:
                    proceedRouteRequest(message);
                    break;
                case 2:
                    proceedRouteReply((RouteReply)message.getData());
                    break;
                case 3:
                    proceedRouteError((RouteError)message.getData());
                    break;
                case 4:
                    proceedChunkMessage((AodvChunkMessage)message.getData());
                    break;
                case 5:
                    proceedBackupMessage((AodvBackupMessage)message.getData());
                    break;
                default:
                    break;
            }
        }
    }

    public void receiveMessage(IpMessage message) {
        queue.offer(message);
    }

    private void proceedChunkMessage(AodvChunkMessage m) {

    }

    private void proceedBackupMessage(AodvBackupMessage m) {

    }

    private void proceedRouteRequest(IpMessage message) {
        RouteRequest request = (RouteRequest)message.getData();
        log.debug(owner + ": I've received RouteRequest, originator is " + request.getSourceNode());

        if (request.getDestNode().equals(owner)) {
            log.debug(owner + ": I am destination and I suggest a path me and now I will generate RREP");

            RouteEntry reverseRoute = routingManager.maintainReverseRoute(message);
            if (request.getDestSN() > owner.getSeqNumber()) {
                owner.setSeqNumber(request.getDestSN());
            }
            routingManager.generateAndSendRrepAsDestination(request, reverseRoute);

        } else if (!request.getSourceNode().equals(owner) && owner.getRreqBufferManager().containsRreq(request)) {
            log.debug(owner + ": I'm intermediate node, I proceed this request");

            owner.getRreqBufferManager().addRequestToBuffer(request);

            RouteEntry reverseRoute = routingManager.maintainReverseRoute(message);
            RouteEntry route = owner.getRoutingTable().getActualRouteTo(request.getDestNode());

            if ((route == null && message.getHl() > 1) || (route != null && route.getDestSN() < request.getDestSN() && message.getHl() > 1)) {
                message.decrementHl();
                request.incrementHopCount();
                owner.incrementAmountOfRetransmitted();
                routingManager.broadcastRouteRequest(request);

            } else if (route != null && route.getDestSN() >= request.getDestSN()){
                log.debug(owner + ": I know the actual route to dest, sending RREP to " + request.getSourceNode() + " which generated RREQ. Next hop is: " + reverseRoute.getNextHop());

                routingManager.generateAndSendRrepAsIntermediate(request, reverseRoute, route);
            }
        }
    }



    private void proceedRouteReply(RouteReply m) {

    }

    private void proceedRouteError(RouteError m) {

    }
}
