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
    private int hl;

    public IpMessage(String sourceNode, String destNode, AodvMessage data, int hl) {
        this.sourceNode = sourceNode;
        this.destNode = destNode;
        this.data = data;
        this.hl = hl;
    }

    public IpMessage(String sourceNode, String nextHop, AddressTableEntry entry, int hl) {
        this.sourceNode = sourceNode;
        this.destNode = nextHop;
        data = new AodvChunkMessage(entry.getNode(), entry.getChunk());
        this.hl = hl;
    }

    public int getMessageType() {
        return data.getMessageType();
    }

    public AodvMessage getData() {
        return data;
    }
}
