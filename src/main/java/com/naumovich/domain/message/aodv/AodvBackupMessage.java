package com.naumovich.domain.message.aodv;

/**
 * Created by dzmitry on 5.5.17.
 */
public class AodvBackupMessage extends AodvMessage {

    private final static int TYPE = 5;

    @Override
    public int getMessageType() {
        return TYPE;
    }
}
