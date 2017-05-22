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
                    proceedRouteRequest(message);
                    break;
                case 2:
                    proceedRouteReply(message);
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



    private void proceedChunkMessage(AodvChunkMessage m) {

    }

    private void proceedBackupMessage(AodvBackupMessage m) {

    }

    private void proceedRouteRequest(IpMessage message) {
        RouteRequest request = (RouteRequest)message.getData();

        if (request.getDestNode().equals(owner.getLogin())) {
            log.debug(owner + ": received RREQ, I'm destination - generating RREP");

            RouteEntry reverseRoute = routingManager.maintainReverseRoute(request, message.getSourceNode());
            if (request.getDestSN() > owner.getSeqNumber()) {
                owner.setSeqNumber(request.getDestSN());
            }
            routingManager.generateAndSendRrepAsDestination(request, reverseRoute);

        } else if (!request.getSourceNode().equals(owner.getLogin()) && !owner.getRreqBufferManager().containsRreq(request)) {
            owner.getRreqBufferManager().addRequestToBuffer(request);

            RouteEntry reverseRoute = routingManager.maintainReverseRoute(request, message.getSourceNode());
            RouteEntry route = owner.getRoutingTable().getActualRouteTo(request.getDestNode());

            if ((route == null && message.getHl() > 1) || (route != null && route.getDestSN() < request.getDestSN() && message.getHl() > 1)) {
                log.debug(owner + ": received RREQ, I'm intermediate, I broadcast it further");

                message.decrementHl();
                request.incrementHopCount();
                owner.incrementAmountOfRetransmitted();
                routingManager.broadcastRouteRequest(request, message.getHl());

            } else if (route != null && route.getDestSN() >= request.getDestSN()){
                log.debug(owner + ": received RREQ, I'm intermediate, know route to dest - generating RREP to " + request.getSourceNode() + ". Next hop is: " + reverseRoute.getNextHop());

                routingManager.generateAndSendRrepAsIntermediate(request, reverseRoute, route);
            }
        }
    }

    private void proceedRouteReply(IpMessage message) {
        RouteReply reply = (RouteReply)message.getData();

        if (reply.getSourceNode().equals(owner.getLogin())) {
            log.debug(owner + ": received RREP, this is a reply for me");

            owner.getRrepBufferManager().addNodeToBuffer(reply.getDestNode());
            RouteEntry route = routingManager.maintainDirectRoute(reply, message.getSourceNode());
            routingManager.sendChunkToObtainedNode(route);

        } else if (message.getHl() > 1) {
            log.debug(owner + ": received RREP for " + reply.getSourceNode() + ", I'm intermediate, I maintain direct route and forward this further");

            String nextHop = owner.getRoutingTable().getActualRouteTo(reply.getSourceNode()).getNextHop();

            RouteEntry route = routingManager.maintainDirectRoute(reply, message.getSourceNode());
            route.addPrecursor(nextHop);
            message.decrementHl();
            reply.incrementHopCount();
            owner.incrementAmountOfRetransmitted();



            routingManager.forwardAodvMessage(reply, nextHop, message.getHl());
        }
    }

    private void proceedRouteError(RouteError m) {

    }
}
