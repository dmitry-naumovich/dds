package com.naumovich.domain.message.aodv;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IpMessage {

    private String sourceNode;
    private String destNode;
    private AodvMessage data;
    private int hl;

    public int getMessageType() {
        return data.getMessageType();
    }

    public String getSourceNode() {
        return sourceNode;
    }

    public void decrementHl() {
        hl--;
    }
}
