package com.naumovich.table;

import com.naumovich.domain.Node;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EqualsAndHashCode
@ToString
@Slf4j
public class RoutingTable implements Iterable<RouteEntry> {

    private Node owner;
    private List<RouteEntry> routingTable;

    public RoutingTable(Node owner) {
        this.owner = owner;
        routingTable = new ArrayList<>();
        ExpiredRouteCleaner routeCleaner = new ExpiredRouteCleaner();
        Thread routeCleanerThread = new Thread(routeCleaner);
        routeCleanerThread.start();
    }

    public void addEntry(RouteEntry entry) {
        routingTable.add(entry);
    }

    public void updateEntry(RouteEntry updatedEntry) {
        for (RouteEntry entry : routingTable) {
            if (entry.getDestNode().equals(updatedEntry.getDestNode())) {
                entry.setDestSN(updatedEntry.getDestSN());
                entry.setNextHop(updatedEntry.getNextHop());
                entry.setHopCount(updatedEntry.getHopCount());
                entry.setLastHopCount(updatedEntry.getHopCount());
                entry.setLifeTime(updatedEntry.getLifeTime());
                entry.setPrecursors(updatedEntry.getPrecursors());
            }
        }
    }

    public RouteEntry getActualRouteTo(String node) {
        for (RouteEntry entry : routingTable) {
            if (entry.getDestNode().equals(node) && entry.getDestSN() > 0) {
                return entry;
            }
        }
        return null;
    }

    public RouteEntry getRouteTo(String node) {
        for (RouteEntry entry : routingTable) {
            if (entry.getDestNode().equals(node)) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public Iterator<RouteEntry> iterator() {
        return new RoutingTableIterator();
    }

    private class RoutingTableIterator implements Iterator<RouteEntry> {

        private int curIndex;
        private boolean canRemove = false;

        RoutingTableIterator() {
            this.curIndex = 0;
        }

        @Override
        public boolean hasNext() {
            return curIndex < routingTable.size();
        }

        @Override
        public RouteEntry next() {
            canRemove = true;
            return routingTable.get(curIndex++);
        }

        //TODO: test this impl
        @Override
        public void remove() {
            if (!canRemove) {
                throw new IllegalStateException("Can only remove() content after a call to next()");
            }
            canRemove = false;
            routingTable.remove(--curIndex);
        }
    }

    private class ExpiredRouteCleaner implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(30000);
                synchronized (routingTable) {
                    routingTable.removeIf((RouteEntry entry) -> System.currentTimeMillis() - entry.getLifeTime() >= 0);
                }
            } catch (InterruptedException e) {
                log.error("InterruptedException occured in ExpiredRouteCleaner of" + owner);
            }
        }
    }
}
