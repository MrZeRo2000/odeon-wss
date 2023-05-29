package com.romanpulov.odeonwss.utils.media;

import java.util.Objects;

public class MediaFormatInfo {
    private String formatName;

    public String getFormatName() {
        return formatName;
    }

    public void setFormatName(String formatName) {
        this.formatName = formatName;
    }

    private long duration;

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    private long size;

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    private long bitRate;

    public long getBitRate() {
        return bitRate;
    }

    public void setBitRate(long bitRate) {
        this.bitRate = bitRate;
    }

    public MediaFormatInfo () {}

    public MediaFormatInfo(String formatName, long duration, long size, long bitRate) {
        this.formatName = formatName;
        this.duration = duration;
        this.size = size;
        this.bitRate = bitRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaFormatInfo that = (MediaFormatInfo) o;
        return duration == that.duration && size == that.size && bitRate == that.bitRate && formatName.equals(that.formatName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formatName, duration, size, bitRate);
    }

    @Override
    public String toString() {
        return "MediaFormatInfo{" +
                "formatName='" + formatName + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", bitRate=" + bitRate +
                '}';
    }
}
