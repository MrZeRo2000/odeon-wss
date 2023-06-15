package com.romanpulov.odeonwss.service.processor.vo;

public class SizeDuration {
    long size;

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    long duration;

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void addSize(long size) {
        this.size += size;
    }

    public void addDuration(long duration) {
        this.duration += duration;
    }

    public SizeDuration(long size, long duration) {
        this.size = size;
        this.duration = duration;
    }

    public SizeDuration() {
    }

    public static SizeDuration of(long size, long duration) {
        return new SizeDuration(size, duration);
    }

    public static SizeDuration empty() {
        return new SizeDuration(0, 0);
    }
}
