package com.naumovich.domain;

import java.util.ArrayList;
import java.util.List;

public class ChunkStorage {

    private List<Chunk> chunkStorage = new ArrayList<>();

    public Chunk getChunkByName(String name) {
        return chunkStorage.stream().filter(c -> c.getChunkName().equals(name)).findFirst().orElse(null);
    }

    public Chunk extractChunkByName(String name) {
        Chunk chunk = chunkStorage.stream().filter(c -> c.getChunkName().equals(name)).findFirst().orElse(null);
        chunkStorage.remove(chunk);
        return chunk;
    }

    public void add(Chunk chunk) {
        chunkStorage.add(chunk);
    }

    public int size() {
        return chunkStorage.size();
    }

}
