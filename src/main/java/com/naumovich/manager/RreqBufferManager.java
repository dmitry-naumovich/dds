package com.naumovich.manager;

import com.naumovich.configuration.AodvConfiguration;
import com.naumovich.domain.Node;
import com.naumovich.domain.message.aodv.IpMessage;
import com.naumovich.domain.message.aodv.RouteRequest;
import com.naumovich.util.tuple.TwoTuple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dzmitry on 16.5.17.
 */
public class RreqBufferManager {

    private Node owner;
    private List<TwoTuple<RouteRequest, Long>> rreqBuffer;

    public RreqBufferManager(Node owner) {
        this.owner = owner;
        rreqBuffer = new ArrayList<>();
        BufferCleaner cleaner = new BufferCleaner();
        Thread thread = new Thread(cleaner);
        thread.start();
    }
    public void addRequestToBuffer(RouteRequest request) {
        rreqBuffer.add(new TwoTuple<>(request, System.currentTimeMillis()));
    }

    public boolean containsRreq(RouteRequest request) {
        for (TwoTuple<RouteRequest, Long> rec : rreqBuffer) {
            if (rec.first.equals(request)) {
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
                    rreqBuffer.removeIf((TwoTuple<RouteRequest, Long> entry) ->
                            System.currentTimeMillis() - entry.second >= AodvConfiguration.FLOOD_RECORD_TIME);
                }
            } catch (InterruptedException e) {
                //TODO: handle
            }
        }
    }
}
