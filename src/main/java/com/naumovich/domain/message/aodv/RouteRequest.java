package com.naumovich.domain.message.aodv;

/**
 * Created by dzmitry on 2.5.17.
 */
//TODO: generate equals, hashCode and toString
public class RouteRequest extends AodvMessage {

    private final static int TYPE = 1;
    private boolean gFlag;
    private int hopCount;
    private int floodId;
    private String destNode;
    private int destSN;
    private String sourceNode;
    private int sourceSN;

    public RouteRequest(int floodId, String destNode, int destSN, String sourceNode, int sourceSN, boolean gFlag) {
        this.floodId = floodId;
        this.destNode = destNode;
        this.destSN = destSN;
        this.sourceNode = sourceNode;
        this.sourceSN = sourceSN;
        this.gFlag = gFlag;
    }

    @Override
    public int getMessageType() {
        return TYPE;
    }

    public boolean isgFlag() {
        return gFlag;
    }

    public void setgFlag(boolean gFlag) {
        this.gFlag = gFlag;
    }

    public int getHopCount() {
        return hopCount;
    }

    public void setHopCount(int hopCount) {
        this.hopCount = hopCount;
    }

    public int getFloodId() {
        return floodId;
    }

    public void setFloodId(int floodId) {
        this.floodId = floodId;
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

    public int getSourceSN() {
        return sourceSN;
    }

    public void setSourceSN(int sourceSN) {
        this.sourceSN = sourceSN;
    }
}
