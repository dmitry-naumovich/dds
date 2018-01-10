package com.naumovich.domain.message.aodv;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteReply extends AodvMessage {

    private final static int TYPE = 2;
    private int hopCount;
    private String destNode;
    private int destSN;
    private String sourceNode;
    private long lifetime;
    private boolean gFlag;

    @Override
    public int getMessageType() {
        return TYPE;
    }

    public void incrementHopCount() {
        hopCount++;
    }

}
