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
}
