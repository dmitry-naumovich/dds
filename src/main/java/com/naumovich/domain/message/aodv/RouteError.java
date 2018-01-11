package com.naumovich.domain.message.aodv;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RouteError extends AodvMessage {

    public final static int TYPE = 3;
    private String destNode;
    private int destSN;
    private String offNode;

    @Override
    public int getMessageType() {
        return TYPE;
    }

}
