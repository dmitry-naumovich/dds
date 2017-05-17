package com.naumovich.manager;

import com.naumovich.domain.Node;
import com.naumovich.domain.message.aodv.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by dzmitry on 4.5.17.
 */

//TODO: change synchronous queue to other implementation so it would be able to store more than 1 element at one moment
@Slf4j
public class AodvMessageManager {

    private Node owner;
    private Queue<IpMessage> queue = new ConcurrentLinkedQueue<>();
    //private Lock lock = new ReentrantLock();

    public AodvMessageManager(Node owner) {
        this.owner = owner;
    }

    public void checkMessageContainer() {
        //lock.lock();
        IpMessage message = queue.poll();
        //lock.unlock();
        if (message != null) {
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
        log.debug(owner.getLogin() + ": I've received RouteRequest, originator is " + request.getSourceNode());
        if (request.getDestNode().equals(owner.getLogin())) {
            log.debug(owner.getLogin() + ": I am destination and I suggest a path me and now I will generate RREP");
            // maintain reverse route
            if (request.getDestSN() > owner.getSeqNumber()) {
                owner.setSeqNumber(request.getDestSN());
            }
            // generate RREP
        } else {
            if (owner.getRreqBufferManager().containsRreq(request)) {
                log.debug(owner.getLogin() + ": I'm not destination, but I've already received such a rreq");
                // nothing else to do more, the msg is already polled off the queue
            } else {
                maintainReverseRoute(message);
                log.debug(owner.getLogin() + ": I'm not destination, I proceed this request");
                // maintain reverse route
                owner.getRreqBufferManager().addRequestToBuffer(request);

                // check where I've got a route to destination
                // if yes -> generate RREP
                // else broadcast further
                owner.getRoutingManager().broadcastRouteRequest(request);
            }
        }

    }

    private void maintainReverseRoute(IpMessage rreqIpMessage) {
        // owner.getRoutingTable() RETURN THE ROUTE IF IT"S PRESENT
        // compare
    }

    private void proceedRouteReply(RouteReply m) {

    }

    private void proceedRouteError(RouteError m) {

    }
}
