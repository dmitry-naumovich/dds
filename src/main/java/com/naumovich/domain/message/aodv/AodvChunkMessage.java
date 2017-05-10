package com.naumovich.domain.message.aodv;

import com.naumovich.domain.Chunk;

/**
 * Created by dzmitry on 5.5.17.
 */
public class AodvChunkMessage extends AodvMessage {

    private final static int TYPE = 4;
    private Chunk chunk;

    public AodvChunkMessage(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }


    @Override
    public int getMessageType() {
        return TYPE;
    }
}
