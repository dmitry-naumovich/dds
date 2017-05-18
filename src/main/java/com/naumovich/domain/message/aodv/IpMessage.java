package com.naumovich.domain.message.aodv;

import com.naumovich.table.FDTEntry;

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

    public IpMessage(String sourceNode, String nextHop, FDTEntry entry, int hl) {
        this.sourceNode = sourceNode;
        this.destNode = nextHop;
        data = new AodvChunkMessage(entry.getNode(), entry.getChunk());
        this.hl = hl;
    }

    public int getMessageType() {
        return data.getMessageType();
    }

    public String getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(String sourceNode) {
        this.sourceNode = sourceNode;
    }

    public String getDestNode() {
        return destNode;
    }

    public void setDestNode(String destNode) {
        this.destNode = destNode;
    }

    public AodvMessage getData() {
        return data;
    }

    public void setData(AodvMessage data) {
        this.data = data;
    }

    public int getHl() {
        return hl;
    }

    public void setHl(int hl) {
        this.hl = hl;
    }
}
