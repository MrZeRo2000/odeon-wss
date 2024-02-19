package com.romanpulov.odeonwss.utils.media.model;

import java.util.Objects;

public class MediaFormatInfo {
    private final String formatName;
    private final long duration;
    private final long size;
    private final long bitRate;
    private final Long width;
    private final Long height;

    public String getFormatName() {
        return formatName;
    }

    public long getDuration() {
        return duration;
    }

    public long getSize() {
        return size;
    }

    public long getBitRate() {
        return bitRate;
    }

    public Long getWidth() {
        return width;
    }

    public Long getHeight() {
        return height;
    }

    private MediaFormatInfo(
            String formatName,
            long duration,
            long size,
            long bitRate,
            Long width,
            Long height) {
        this.formatName = formatName;
        this.duration = duration;
        this.size = size;
        this.bitRate = bitRate;
        this.width = width;
        this.height = height;
    }

    public static MediaFormatInfo fromGeneralAttributes(
            String formatName,
            long duration,
            long size,
            long bitRate) {
        return new MediaFormatInfo(formatName, duration, size, bitRate, null, null);
    }

    public static MediaFormatInfo fromAllAttributes(
            String formatName,
            long duration,
            long size,
            long bitRate,
            Long width,
            Long height) {
        return new MediaFormatInfo(formatName, duration, size, bitRate, width, height);
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
