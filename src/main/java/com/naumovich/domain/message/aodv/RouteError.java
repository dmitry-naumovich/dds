package com.naumovich.domain.message.aodv;

import com.naumovich.domain.Node;

/**
 * Created by dzmitry on 2.5.17.
 */
public class RouteError extends AodvMessage {

    public final static int TYPE = 3;
    private boolean nFlag;
    private int destCount;
    private Node destinationNode; //unreachable
    private int destSequenceNum;

    @Override
    public int getMessageType() {
        return TYPE;
    }
}
