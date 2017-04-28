package com.naumovich.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dzmitry on 28.4.17.
 */
public class ChunkStorage {

    private List<Chunk> chunkStorage;

    public ChunkStorage() {
        chunkStorage = new ArrayList<>();
    }

    public Chunk getChunkByName(String name) {
        for (Chunk c : chunkStorage) {
            if (c.getChunkName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public void add(Chunk chunk) {
        chunkStorage.add(chunk);
    }

    public int size() {
        return chunkStorage.size();
    }


}
