package com.naumovich.table;

import com.naumovich.domain.Node;

import java.util.List;

/**
 * Created by dzmitry on 2.5.17.
 */
public class RouteEntry {

    private Node destinationNode;
    private int destinationSequenceNum;
    private int hopCount;
    private int lastHopCount;
    private Node nextHop;
    private List<Node> precursors;
    private int lifeTime;

}
