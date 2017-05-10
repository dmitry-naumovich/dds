package com.naumovich.manager;

import com.naumovich.domain.Node;
import com.naumovich.domain.message.aodv.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by dzmitry on 4.5.17.
 */

//TODO: change synchronous queue to other implementation so it would be able to store more than 1 element at one moment
@Slf4j
public class AodvMessageManager implements MessageManager {

    private Node owner;
    private Queue<IpMessage> queue = new SynchronousQueue<>();

    public AodvMessageManager(Node owner) {
        this.owner = owner;
    }

    @Override
    public void checkMessageContainer() {
        IpMessage message = queue.poll();
        if (message != null) {
            switch (message.getMessageType()) {
                case 1:
                    proceedRouteRequest((RouteRequest)message.getData());
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

    private void proceedRouteRequest(RouteRequest m) {

    }

    private void proceedRouteReply(RouteReply m) {

    }

    private void proceedRouteError(RouteError m) {

    }

}
