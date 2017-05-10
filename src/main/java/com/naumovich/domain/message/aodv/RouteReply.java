package com.naumovich.domain.message.aodv;

import com.naumovich.domain.Node;

/**
 * Created by dzmitry on 2.5.17.
 */
public class RouteReply extends AodvMessage {

    private final static int TYPE = 2;
    private boolean aFlag;
    private int hopCount;
    private Node destinationNode;
    private int destSequenceNum;
    private Node sourceNode;
    private int lifetime;

    @Override
    public int getMessageType() {
        return TYPE;
    }
}
