package com.romanpulov.odeonwss.utils.media;

import java.util.Objects;

public class MediaStreamInfo {
    private final MediaType mediaType;

    public MediaType getMediaType() {
        return mediaType;
    }

    private final long order;

    public long getOrder() {
        return order;
    }

    private final long duration;

    public long getDuration() {
        return duration;
    }

    private final long bitRate;

    public long getBitRate() {
        return bitRate;
    }

    private MediaStreamInfo(MediaType mediaType, long order, long duration, long bitRate) {
        this.mediaType = mediaType;
        this.order = order;
        this.duration = duration;
        this.bitRate = bitRate;
    }

    public static MediaStreamInfo createOrdered(MediaType mediaType, long order, long duration, long bitRate) {
        return new MediaStreamInfo(mediaType, order, duration, bitRate);
    }

    public static MediaStreamInfo createUnordered(MediaType mediaType, long duration, long bitRate) {
        return new MediaStreamInfo(mediaType, 0, duration, bitRate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaStreamInfo that = (MediaStreamInfo) o;
        return duration == that.duration && bitRate == that.bitRate && mediaType == that.mediaType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mediaType, duration, bitRate);
    }

    @Override
    public String toString() {
        return "MediaStreamInfo{" +
                "mediaStreamType=" + mediaType +
                ", duration=" + duration +
                ", bitRate=" + bitRate +
                '}';
    }
}
