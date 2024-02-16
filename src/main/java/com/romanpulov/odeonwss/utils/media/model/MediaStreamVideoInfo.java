package com.romanpulov.odeonwss.utils.media.model;

import java.util.Objects;

public class MediaStreamVideoInfo extends AbstractMediaStreamInfo {
    private final long width;
    private final long height;

    public long getWidth() {
        return width;
    }

    public long getHeight() {
        return height;
    }

    public MediaStreamVideoInfo(long order, long duration, long bitRate, long width, long height) {
        super(order, duration, bitRate);
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MediaStreamVideoInfo that = (MediaStreamVideoInfo) o;
        return width == that.width && height == that.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), width, height);
    }

    @Override
    public String toString() {
        return "MediaStreamVideoInfo{" +
                "width=" + width +
                ", height=" + height +
                ", order=" + getOrder() +
                ", duration=" + getDuration() +
                ", bitRate=" + getBitRate() +
                '}';
    }
}
