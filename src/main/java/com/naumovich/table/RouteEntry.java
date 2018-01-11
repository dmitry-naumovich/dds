package com.naumovich.table;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * This class describes RoutingTable entry which describes the route, or path, to a node in the network
 * Created by Dzmitry Naumovich on 2.5.17.
 *
 * @version 1.0
 * @author Dzmitry Naumovich
 */
@Data
@Builder
public class RouteEntry {

    private String destNode;
    private int destSN;
    private int hopCount;
    private int lastHopCount;
    private String nextHop;
    private List<String> precursors = new ArrayList<>();
    private long lifeTime;

    public RouteEntry(String destNode, int destSN, int hopCount, int lastHopCount, String nextHop, List<String> precursors, long lifeTime) {
        this.destNode = destNode;
        this.destSN = destSN;
        this.hopCount = hopCount;
        this.lastHopCount = lastHopCount;
        this.nextHop = nextHop;
        if (precursors != null) {
            this.precursors = precursors;
        }
        this.lifeTime = lifeTime;
    }

    public void addPrecursor(String node) {
        precursors.add(node);
    }

    public void removePrecursor(String node) {
        precursors.remove(node);
    }
}
