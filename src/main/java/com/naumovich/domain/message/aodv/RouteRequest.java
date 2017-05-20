package com.naumovich.domain.message.aodv;

import java.util.Objects;

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

    public void incrementHopCount() {
        hopCount++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteRequest that = (RouteRequest) o;
        return gFlag == that.gFlag &&
                hopCount == that.hopCount &&
                floodId == that.floodId &&
                destSN == that.destSN &&
                sourceSN == that.sourceSN &&
                Objects.equals(destNode, that.destNode) &&
                Objects.equals(sourceNode, that.sourceNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gFlag, hopCount, floodId, destNode, destSN, sourceNode, sourceSN);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RouteRequest{");
        sb.append("gFlag=").append(gFlag);
        sb.append(", hopCount=").append(hopCount);
        sb.append(", floodId=").append(floodId);
        sb.append(", destNode='").append(destNode).append('\'');
        sb.append(", destSN=").append(destSN);
        sb.append(", sourceNode='").append(sourceNode).append('\'');
        sb.append(", sourceSN=").append(sourceSN);
        sb.append('}');
        return sb.toString();
    }
}
