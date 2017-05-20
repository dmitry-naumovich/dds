package com.naumovich.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class describes table entry of RoutingTable
 * Created by Dzmitry on 2.5.17.
 *
 * @version 1.0
 * @author Dzmitry Naumovich
 */
public class RouteEntry {

    private String destNode;
    private int destSN;
    private int hopCount;
    private int lastHopCount;
    private String nextHop;
    private List<String> precursors;
    private long lifeTime;

    public RouteEntry(String destNode, int destSN, int hopCount, int lastHopCount, String nextHop, List<String> precursors, long lifeTime) {
        this.destNode = destNode;
        this.destSN = destSN;
        this.hopCount = hopCount;
        this.lastHopCount = lastHopCount;
        this.nextHop = nextHop;
        if (precursors == null) {
            this.precursors = new ArrayList<>();
        } else {
            this.precursors = precursors;
        }
        this.lifeTime = lifeTime;
    }

    public RouteEntry() {
        precursors = new ArrayList<>();
    }

    public String getDestNode() {
        return destNode;
    }

    public void setDestNode(String destNode) {
        this.destNode = destNode;
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

    public int getDestSN() {
        return destSN;
    }

    public void setDestSN(int destSN) {
        this.destSN = destSN;
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

    public long getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(long lifeTime) {
        this.lifeTime = lifeTime;
    }

    public void addPrecursor(String node) {
        precursors.add(node);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteEntry entry = (RouteEntry) o;
        return destSN == entry.destSN &&
                hopCount == entry.hopCount &&
                lastHopCount == entry.lastHopCount &&
                lifeTime == entry.lifeTime &&
                Objects.equals(destNode, entry.destNode) &&
                Objects.equals(nextHop, entry.nextHop) &&
                Objects.equals(precursors, entry.precursors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(destNode, destSN, hopCount, lastHopCount, nextHop, precursors, lifeTime);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RouteEntry{");
        sb.append("destNode='").append(destNode).append('\'');
        sb.append(", destSN=").append(destSN);
        sb.append(", hopCount=").append(hopCount);
        sb.append(", lastHopCount=").append(lastHopCount);
        sb.append(", nextHop='").append(nextHop).append('\'');
        sb.append(", precursors=").append(precursors);
        sb.append(", lifeTime=").append(lifeTime);
        sb.append('}');
        return sb.toString();
    }
}
