package com.naumovich.table;

import com.naumovich.domain.Node;

import java.util.List;

/**
 * Created by dzmitry on 2.5.17.
 */
public class RouteEntry {

    private String destinationNode;
    private int destinationSequenceNum;
    private int hopCount;
    private int lastHopCount;
    private String nextHop;
    private List<String> precursors;
    private int lifeTime;

    public String getDestinationNode() {
        return destinationNode;
    }

    public void setDestinationNode(String destinationNode) {
        this.destinationNode = destinationNode;
    }

    public String getNextHop() {
        return nextHop;
    }

    public void setNextHop(String nextHop) {
        this.nextHop = nextHop;
    }

    public List<String> getPrecursors() {
        return precursors;
    }

    public void setPrecursors(List<String> precursors) {
        this.precursors = precursors;
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

    public int getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(int lifeTime) {
        this.lifeTime = lifeTime;
    }
}
