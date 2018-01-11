package com.naumovich.manager;

import com.naumovich.configuration.AodvConfiguration;
import com.naumovich.domain.Node;
import com.naumovich.domain.message.aodv.RouteRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

@Slf4j
public class RreqBufferManager {

    private Node owner;
    private List<Pair<RouteRequest, Long>> rreqBuffer;

    public RreqBufferManager(Node owner) {
        this.owner = owner;
        rreqBuffer = new ArrayList<>();
        BufferCleaner cleaner = new BufferCleaner();
        Thread thread = new Thread(cleaner);
        thread.start();
    }
    public void addRequestToBuffer(RouteRequest request) {
        rreqBuffer.add(Pair.of(request, System.currentTimeMillis()));
    }

    public boolean containsRreq(RouteRequest request) {
        for (Pair<RouteRequest, Long> rec : rreqBuffer) {
            if (rec.getLeft().equals(request)) {
                return true;
            }
        }
        return false;
    }

    private class BufferCleaner implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(30000);
                synchronized (rreqBuffer) {
                    rreqBuffer.removeIf(entry ->
                            System.currentTimeMillis() - entry.getRight() >= AodvConfiguration.FLOOD_RECORD_TIME);
                }
            } catch (InterruptedException e) {
                log.error("InterruptedException occurred in BufferCleaner in RreqBufferManager of " + owner);
            }
        }
    }
}
