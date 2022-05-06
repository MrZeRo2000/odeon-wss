package com.romanpulov.odeonwss.utils.media;

import java.util.Objects;

public class MediaFormatInfo {
    private final String formatName;

    public String getFormatName() {
        return formatName;
    }

    private final long duration;

    public long getDuration() {
        return duration;
    }

    private final long size;

    public long getSize() {
        return size;
    }

    private final long bitRate;

    public long getBitRate() {
        return bitRate;
    }

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
