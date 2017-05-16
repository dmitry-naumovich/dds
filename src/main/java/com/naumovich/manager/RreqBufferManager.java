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
    private List<TwoTuple<IpMessage, Long>> floodings;

    public RreqBufferManager(Node owner) {
        this.owner = owner;
        floodings = new ArrayList<>();
        BufferCleaner cleaner = new BufferCleaner();
        Thread thread = new Thread(cleaner);
        thread.start();
    }
    public void addFloodingId(IpMessage rreqIpMessage, long timeMillis) {
        floodings.add(new TwoTuple<>(rreqIpMessage, timeMillis));
    }

    public boolean containsRreq(IpMessage rreqIpMessage) {
        if (floodings.contains(rreqIpMessage)) {
            return true;
        }
        return false;
    }

    private class BufferCleaner implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(30000);
                synchronized (floodings) {
                    for (Iterator iter = floodings.iterator(); iter.hasNext(); ) {
                        if (System.currentTimeMillis() - ((TwoTuple<Integer, Long>)iter.next()).second >= AodvConfiguration.FLOOD_RECORD_TIME) {
                            iter.remove();
                        }
                    }
                }
            } catch (InterruptedException e) {
                //TODO: handle
            }
        }
    }
}
