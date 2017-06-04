package com.naumovich.domain.message.aodv;

import java.util.Objects;

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

    public AodvBackupMessage(String chunkId, String newChunkId, String sourceNode, String destNode, String newChunkSaver) {
        this.chunkId = chunkId;
        this.newChunkId = newChunkId;
        this.sourceNode = sourceNode;
        this.destNode = destNode;
        this.newChunkSaver = newChunkSaver;
    }

    public String getChunkId() {
        return chunkId;
    }

    public void setChunkId(String chunkId) {
        this.chunkId = chunkId;
    }

    public String getNewChunkId() {
        return newChunkId;
    }

    public void setNewChunkId(String newChunkId) {
        this.newChunkId = newChunkId;
    }

    public String getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(String sourceNode) {
        this.sourceNode = sourceNode;
    }

    public String getDestNode() {
        return destNode;
    }

    public void setDestNode(String destNode) {
        this.destNode = destNode;
    }

    public String getNewChunkSaver() {
        return newChunkSaver;
    }

    public void setNewChunkSaver(String newChunkSaver) {
        this.newChunkSaver = newChunkSaver;
    }

    @Override
    public int getMessageType() {
        return TYPE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AodvBackupMessage that = (AodvBackupMessage) o;
        return Objects.equals(chunkId, that.chunkId) &&
                Objects.equals(newChunkId, that.newChunkId) &&
                Objects.equals(sourceNode, that.sourceNode) &&
                Objects.equals(destNode, that.destNode) &&
                Objects.equals(newChunkSaver, that.newChunkSaver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chunkId, newChunkId, sourceNode, destNode, newChunkSaver);
    }

    @Override
    public String toString() {
        return "AodvBackupMessage{" +
                "chunkId='" + chunkId + '\'' +
                ", newChunkId='" + newChunkId + '\'' +
                ", sourceNode='" + sourceNode + '\'' +
                ", destNode='" + destNode + '\'' +
                ", newChunkSaver='" + newChunkSaver + '\'' +
                '}';
    }
}
