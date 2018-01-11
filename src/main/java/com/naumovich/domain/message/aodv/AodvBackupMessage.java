package com.naumovich.domain.message.aodv;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
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
