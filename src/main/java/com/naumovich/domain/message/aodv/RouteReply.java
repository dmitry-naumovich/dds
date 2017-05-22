package com.naumovich.domain.message.aodv;

import java.util.Objects;

public class RouteReply extends AodvMessage {

    private final static int TYPE = 2;
    private boolean aFlag;
    private int hopCount;
    private String destNode;
    private int destSN;
    private String sourceNode;
    private long lifetime;

    public RouteReply(int hopCount, String destNode, int destSN, String sourceNode, long lifetime, boolean aFlag) {
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

    public long getLifetime() {
        return lifetime;
    }

    public void setLifetime(long lifetime) {
        this.lifetime = lifetime;
    }

    public void incrementHopCount() {
        hopCount++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteReply that = (RouteReply) o;
        return aFlag == that.aFlag &&
                hopCount == that.hopCount &&
                destSN == that.destSN &&
                lifetime == that.lifetime &&
                Objects.equals(destNode, that.destNode) &&
                Objects.equals(sourceNode, that.sourceNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aFlag, hopCount, destNode, destSN, sourceNode, lifetime);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RouteReply{");
        sb.append("aFlag=").append(aFlag);
        sb.append(", hopCount=").append(hopCount);
        sb.append(", destNode='").append(destNode).append('\'');
        sb.append(", destSN=").append(destSN);
        sb.append(", sourceNode='").append(sourceNode).append('\'');
        sb.append(", lifetime=").append(lifetime);
        sb.append('}');
        return sb.toString();
    }
}
