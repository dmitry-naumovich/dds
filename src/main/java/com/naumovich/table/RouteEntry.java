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

    public Node getDestinationNode() {
        return destinationNode;
    }

    public void setDestinationNode(Node destinationNode) {
        this.destinationNode = destinationNode;
    }

    public int getDestinationSequenceNum() {
        return destinationSequenceNum;
    }

    public void setDestinationSequenceNum(int destinationSequenceNum) {
        this.destinationSequenceNum = destinationSequenceNum;
    }

    public int getHopCount() {
        return hopCount;
    }

    public void setHopCount(int hopCount) {
        this.hopCount = hopCount;
    }

    public int getLastHopCount() {
        return lastHopCount;
    }

    public void setLastHopCount(int lastHopCount) {
        this.lastHopCount = lastHopCount;
    }

    public Node getNextHop() {
        return nextHop;
    }

    public void setNextHop(Node nextHop) {
        this.nextHop = nextHop;
    }

    public List<Node> getPrecursors() {
        return precursors;
    }

    public void setPrecursors(List<Node> precursors) {
        this.precursors = precursors;
    }

    public int getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(int lifeTime) {
        this.lifeTime = lifeTime;
    }
}
