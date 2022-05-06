package com.romanpulov.odeonwss.utils.media;

import java.util.Objects;

public class MediaStreamInfo {
    private final MediaType mediaType;

    public MediaType getMediaType() {
        return mediaType;
    }

    private final long duration;

    public long getDuration() {
        return duration;
    }

    private final long bitRate;

    public long getBitRate() {
        return bitRate;
    }

    public MediaStreamInfo(MediaType mediaType, long duration, long bitRate) {
        this.mediaType = mediaType;
        this.duration = duration;
        this.bitRate = bitRate;
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
