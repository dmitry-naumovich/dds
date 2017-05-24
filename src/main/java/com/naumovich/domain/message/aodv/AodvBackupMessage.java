package com.naumovich.domain.message.aodv;

/**
 * Created by dzmitry on 5.5.17.
 */
public class AodvBackupMessage extends AodvMessage {

    private final static int TYPE = 5;
    private String chunkId;
    private String newChunkId;
    private String sourceNode;
    private String destNode;
    private String newChunkSaver;

    @Override
    public int getMessageType() {
        return TYPE;
    }
}
