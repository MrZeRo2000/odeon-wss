package com.romanpulov.odeonwss.utils.media.model;

import java.util.Objects;

public abstract class AbstractMediaStreamInfo {

    private final long order;
    private final long duration;
    private final long bitRate;

    public long getOrder() {
        return order;
    }

    public long getDuration() {
        return duration;
    }

    public long getBitRate() {
        return bitRate;
    }

    protected AbstractMediaStreamInfo(long order, long duration, long bitRate) {
        this.order = order;
        this.duration = duration;
        this.bitRate = bitRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractMediaStreamInfo that = (AbstractMediaStreamInfo) o;
        return order == that.order && duration == that.duration && bitRate == that.bitRate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, duration, bitRate);
    }

    @Override
    public String toString() {
        return "AbstractMediaStreamInfo{" +
                "order=" + order +
                ", duration=" + duration +
                ", bitRate=" + bitRate +
                '}';
    }
}
