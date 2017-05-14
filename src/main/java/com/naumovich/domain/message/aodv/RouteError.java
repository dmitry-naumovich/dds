package com.naumovich.domain.message.aodv;

import com.naumovich.domain.Node;

/**
 * Created by dzmitry on 2.5.17.
 */
public class RouteError extends AodvMessage {

    public final static int TYPE = 3;
    private boolean nFlag;
    private Node destNode;
    private int destSN;

    @Override
    public int getMessageType() {
        return TYPE;
    }
}
