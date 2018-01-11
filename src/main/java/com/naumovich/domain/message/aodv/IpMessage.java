package com.naumovich.domain.message.aodv;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class IpMessage {

    @Getter
    private String sourceNode;
    private String destNode;
    private AodvMessage data;
    private int hl;

    public int getMessageType() {
        return data.getMessageType();
    }
}
