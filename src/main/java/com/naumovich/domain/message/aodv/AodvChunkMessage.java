package com.naumovich.domain.message.aodv;

import com.naumovich.domain.Chunk;

/**
 * Created by dzmitry on 5.5.17.
 */
public class AodvChunkMessage extends AodvMessage {

    private final static int TYPE = 4;
    private String destNode;
    private String sourceNode;
    private Chunk chunk;

    public AodvChunkMessage(String destNode, Chunk chunk) {
        this.destNode = destNode;
        this.sourceNode = chunk.getOriginalOwner().getLogin();
        this.chunk = chunk;
    }


    @Override
    public int getMessageType() {
        return TYPE;
    }

    public String getDestNode() {
        return destNode;
    }

    public void setDestNode(String destNode) {
        this.destNode = destNode;
    }

    public String getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(String sourceNode) {
        this.sourceNode = sourceNode;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }
}
