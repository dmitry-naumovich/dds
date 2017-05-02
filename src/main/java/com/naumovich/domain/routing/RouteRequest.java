package com.naumovich.domain.routing;

import com.naumovich.domain.Node;

/**
 * Created by dzmitry on 2.5.17.
 */
public class RouteRequest {

    public final static int TYPE = 1;
    private boolean gFlag;
    private int hopCount;
    private int floodingId;
    private Node destinationNode;
    private int destSequenceNum;
    private Node sourceNode;
    private int sourceSequenceNum;

}
