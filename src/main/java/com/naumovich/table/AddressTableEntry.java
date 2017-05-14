package com.naumovich.table;

import com.naumovich.domain.Chunk;
import com.naumovich.domain.Node;

import java.util.Objects;

/**
 * Created by dzmitry on 4.5.17.
 */
public class AddressTableEntry {

    private int orderNum;
    private Chunk chunk;
    private String node;
    private int metric;

    public AddressTableEntry(int orderNum, Chunk chunk, String node, int metric) {
        this.orderNum = orderNum;
        this.chunk = chunk;
        this.node = node;
        this.metric = metric;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public int getMetric() {
        return metric;
    }

    public void setMetric(int metric) {
        this.metric = metric;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderNum, chunk, node, metric);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressTableEntry that = (AddressTableEntry) o;
        return orderNum == that.orderNum &&
                metric == that.metric &&
                Objects.equals(chunk, that.chunk) &&
                Objects.equals(node, that.node);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AddressTableEntry{");
        sb.append("orderNum=").append(orderNum);
        sb.append(", chunk=").append(chunk);
        sb.append(", node=").append(node);
        sb.append(", metric=").append(metric);
        sb.append('}');
        return sb.toString();
    }
}
