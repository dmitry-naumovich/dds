package com.naumovich.table;

import com.naumovich.domain.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Created by dzmitry on 17.5.17.
 */
//TODO: FINISH (equals, hashcode, toString also)
public class RoutingTable implements Iterable<RouteEntry> {

    private Node owner;
    private List<RouteEntry> routingTable;

    public RoutingTable(Node owner) {
        this.owner = owner;
        routingTable = new ArrayList<>();
    }

    public void addEntry(RouteEntry entry) {
        routingTable.add(entry);
    }

    public RouteEntry getActualRouteTo(String node) {
        for (RouteEntry entry : routingTable) {
            if (entry.getDestNode().equals(node) && entry.getDestSN() > 0) {
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

        public RoutingTableIterator() {
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

        //TODO: check this impl
        @Override
        public void remove() {
            if (!canRemove) {
                throw new IllegalStateException("Can only remove() content after a call to next()");
            }
            canRemove = false;
            routingTable.remove(--curIndex);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoutingTable that = (RoutingTable) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(routingTable, that.routingTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, routingTable);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RoutingTable{");
        sb.append("owner=").append(owner);
        sb.append(", routingTable=").append(routingTable);
        sb.append('}');
        return sb.toString();
    }
}
