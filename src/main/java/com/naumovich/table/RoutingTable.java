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
    private List<RouteEntry> routingTable = new ArrayList<>();

    public RoutingTable(Node owner) {
        this.owner = owner;
        Thread routeCleanerThread = new Thread(new ExpiredRouteCleaner());
        routeCleanerThread.start();
    }

    public void addEntry(RouteEntry entry) {
        routingTable.add(entry);
    }

    public void updateEntry(RouteEntry updatedRoute) {
        routingTable.stream().filter(route -> route.getDestNode().equals(updatedRoute.getDestNode())).forEach(route -> {
            route.setDestSN(updatedRoute.getDestSN());
            route.setNextHop(updatedRoute.getNextHop());
            route.setHopCount(updatedRoute.getHopCount());
            route.setLastHopCount(updatedRoute.getHopCount());
            route.setLifeTime(updatedRoute.getLifeTime());
            route.setPrecursors(updatedRoute.getPrecursors());
        });
    }

    public RouteEntry getActualRouteTo(String node) {
        return routingTable.stream().filter(e -> e.getDestNode().equals(node) && e.getDestSN() > 0).findFirst().orElse(null);
    }

    public RouteEntry getRouteTo(String node) {
        return routingTable.stream().filter(e -> e.getDestNode().equals(node)).findFirst().orElse(null);
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
                log.error("InterruptedException occurred in ExpiredRouteCleaner of" + owner);
            }
        }
    }
}
