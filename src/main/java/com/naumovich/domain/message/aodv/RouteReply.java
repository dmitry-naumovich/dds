package com.naumovich.domain.message.aodv;

public class RouteReply extends AodvMessage {

    private final static int TYPE = 2;
    private boolean aFlag;
    private int hopCount;
    private String destNode;
    private int destSN;
    private String sourceNode;
    private int lifetime;

    public RouteReply(int hopCount, String destNode, int destSN, String sourceNode, int lifetime, boolean aFlag) {
        this.hopCount = hopCount;
        this.destNode = destNode;
        this.destSN = destSN;
        this.sourceNode = sourceNode;
        this.lifetime = lifetime;
        this.aFlag = aFlag;
    }

    public RouteReply() {}

    @Override
    public int getMessageType() {
        return TYPE;
    }

    public boolean isaFlag() {
        return aFlag;
    }

    public void setaFlag(boolean aFlag) {
        this.aFlag = aFlag;
    }

    public int getHopCount() {
        return hopCount;
    }

    public void setHopCount(int hopCount) {
        this.hopCount = hopCount;
    }

    public String getDestNode() {
        return destNode;
    }

    public void setDestNode(String destNode) {
        this.destNode = destNode;
    }

    public int getDestSN() {
        return destSN;
    }

    public void setDestSN(int destSN) {
        this.destSN = destSN;
    }

    public String getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(String sourceNode) {
        this.sourceNode = sourceNode;
    }

    public int getLifetime() {
        return lifetime;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }
}
