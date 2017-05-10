package com.naumovich.domain.message.aodv;

/**
 * Created by dzmitry on 10.5.17.
 */
public class IpMessage {

    private int ttl;
    private String sourceNode;
    private String destinationNode;
    private AodvMessage data;


    public int getMessageType() {
        return data.getMessageType();
    }

    public AodvMessage getData() {
        return data;
    }
}
