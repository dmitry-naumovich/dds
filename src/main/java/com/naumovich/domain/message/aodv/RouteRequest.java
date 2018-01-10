package com.naumovich.domain.message.aodv;

import lombok.Data;

@Data
public class RouteRequest extends AodvMessage {

    private final static int TYPE = 1;
    private int hopCount;
    private int floodId;
    private String destNode;
    private int destSN;
    private String sourceNode;
    private int sourceSN;

    //TODO: change with @AllArgsConstructor (but clarify when hopCount is set ? never?)
    public RouteRequest(int floodId, String destNode, int destSN, String sourceNode, int sourceSN) {
        this.floodId = floodId;
        this.destNode = destNode;
        this.destSN = destSN;
        this.sourceNode = sourceNode;
        this.sourceSN = sourceSN;
    }

    @Override
    public int getMessageType() {
        return TYPE;
    }

    public void incrementHopCount() {
        hopCount++;
    }

}
