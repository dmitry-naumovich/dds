package com.naumovich.table;

import java.util.Objects;

/**
 * Created by dzmitry on 4.5.17.
 */
public class FDTEntry {

    private int orderNum;
    private String chunk;
    private String node;
    private int metric;

    public FDTEntry(int orderNum, String chunk, String node, int metric) {
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

    public String getChunk() {
        return chunk;
    }

    public void setChunk(String chunk) {
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
        FDTEntry that = (FDTEntry) o;
        return orderNum == that.orderNum &&
                metric == that.metric &&
                Objects.equals(chunk, that.chunk) &&
                Objects.equals(node, that.node);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FDTEntry{");
        sb.append("orderNum=").append(orderNum);
        sb.append(", chunk=").append(chunk);
        sb.append(", node=").append(node);
        sb.append(", metric=").append(metric);
        sb.append('}');
        return sb.toString();
    }
}
