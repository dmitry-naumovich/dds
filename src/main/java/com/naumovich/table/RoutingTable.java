package com.naumovich.table;

import com.naumovich.domain.Node;

import java.util.Iterator;
import java.util.List;

/**
 * Created by dzmitry on 17.5.17.
 */
//TODO: FINISH (equals, hashcode, toString also)
public class RoutingTable implements Iterable<RouteEntry> {

    private Node owner;
    private List<RouteEntry> routingTable;

    @Override
    public Iterator<RouteEntry> iterator() {
        return new RoutingTableIterator();
    }

    private class RoutingTableIterator implements Iterator<RouteEntry> {

        private int curIndex;

        public RoutingTableIterator() {
            this.curIndex = 0;
        }

        @Override
        public boolean hasNext() {
            if (curIndex < routingTable.size()) {
                return true;
            }
            return false;
        }

        @Override
        public RouteEntry next() {
            return routingTable.get(curIndex++);
        }

        //TODO: override it correctly
        @Override
        public void remove() {
            routingTable.remove(curIndex++);
        }
    }

}
