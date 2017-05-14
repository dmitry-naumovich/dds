package com.naumovich.domain.message.aodv;

import com.naumovich.domain.Node;
import com.naumovich.table.AddressTableEntry;

/**
 * Created by dzmitry on 10.5.17.
 */
public class IpMessage {

    private String sourceNode;
    private String destNode;
    private AodvMessage data;
    private int ttl;

    public IpMessage(String sourceNode, String destNode, AodvMessage data, int ttl) {
        this.sourceNode = sourceNode;
        this.destNode = destNode;
        this.data = data;
        this.ttl = ttl;
    }

    public IpMessage(Node sourceNode, Node nextHop, AddressTableEntry entry) {
        this.sourceNode = sourceNode.getLogin();
        this.destNode = nextHop.getLogin();
        data = new AodvChunkMessage(entry.getNode(), entry.getChunk());
        ttl = entry.getMetric();
    }

    public int getMessageType() {
        return data.getMessageType();
    }

    public AodvMessage getData() {
        return data;
    }
}
