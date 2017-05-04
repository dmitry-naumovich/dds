package com.naumovich.manager;

import com.naumovich.domain.Chunk;
import com.naumovich.domain.Node;
import com.naumovich.domain.message.ChunkMessage;
import com.naumovich.domain.message.Message;
import com.naumovich.network.MessageContainer;
import com.naumovich.table.AddressTable;
import com.naumovich.table.AddressTableEntry;
import com.naumovich.table.RouteEntry;
import com.naumovich.util.tuple.FourTuple;

import java.util.List;

/**
 * Created by dzmitry on 4.5.17.
 */
public class AodvRoutingManager implements  RoutingManager {

    @Override
    public void distributeChunks(Node owner, AddressTable addressTable) {
        for (AddressTableEntry entry : addressTable) {
            Node nextHop = getNextHopIfPresent(entry.getNode(), owner.getRoutingTable());
            if (nextHop != null) {
                // create chunk message
                // send chunk to next hop
                //Message msg = new ChunkMessage(path, tableRow.second);
                //msg.excludeFirstNodeFromPath();
                //MessageContainer.addMsg(msg);
            } else {
                generateRreqFlood();
            }
        }
    }

    private Node getNextHopIfPresent(Node node, List<RouteEntry> routingTable) {
        for (RouteEntry entry : routingTable) {
            if (entry.getDestinationNode().equals(node) && entry.getDestinationSequenceNum() >= 0) {
                return entry.getNextHop();
            }
        }
        return null;
    }

    // TODO: implement
    private void generateRreqFlood() {
        // RREQ = new RREQ
        // findNeighbors
        // for each neighbor send rreq
    }

    // TODO: implement it
    @Override
    public void checkNodesStatus(Node owner, AddressTable addressTable) {

    }
}
