package com.naumovich.manager;

import com.naumovich.domain.Node;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class RrepBufferManager {

    private Node owner;
    private Map<String, Long> rrepBuffer;

    public RrepBufferManager(Node owner) {
        this.owner = owner;
        rrepBuffer = new HashMap<>();
        RrepBufferManager.BufferCleaner cleaner = new RrepBufferManager.BufferCleaner();
        Thread thread = new Thread(cleaner);
        thread.start();
    }
    public void addNodeToBuffer(String node) {
        rrepBuffer.put(node, System.currentTimeMillis());
    }

    public boolean containsNode(String node) {
        return rrepBuffer.containsKey(node);
    }

    private class BufferCleaner implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(30000);
                synchronized (rrepBuffer) {
                    rrepBuffer.entrySet().removeIf(entry ->
                            System.currentTimeMillis() - 3000 >= entry.getValue());
                }
            } catch (InterruptedException e) {
                log.error("InterruptedException occured in BufferCleaner in RrepBufferManager of " + owner);
            }
        }
    }
}
