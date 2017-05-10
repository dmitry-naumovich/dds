package com.naumovich.domain.message.aodv;

/**
 * Created by dzmitry on 2.5.17.
 */
public class RouteRequest extends AodvMessage {

    private final static int TYPE = 1;
    private boolean gFlag;
    private int hopCount;
    private int floodingId;
    private String destinationNode;
    private int destSequenceNum;
    private String sourceNode;
    private int sourceSequenceNum;


    @Override
    public int getMessageType() {
        return TYPE;
    }
}
