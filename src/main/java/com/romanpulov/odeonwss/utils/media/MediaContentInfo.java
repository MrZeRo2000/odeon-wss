package com.romanpulov.odeonwss.utils.media;

import java.util.List;
import java.util.Objects;

public class MediaContentInfo {
    private final List<MediaStreamInfo> mediaStreams;

    public List<MediaStreamInfo> getMediaStreams() {
        return mediaStreams;
    }

    private final MediaFormatInfo mediaFormatInfo;

    public MediaFormatInfo getMediaFormatInfo() {
        return mediaFormatInfo;
    }

    public MediaContentInfo(List<MediaStreamInfo> mediaStreams, MediaFormatInfo mediaFormatInfo) {
        this.mediaStreams = mediaStreams;
        this.mediaFormatInfo = mediaFormatInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaContentInfo that = (MediaContentInfo) o;
        return mediaStreams.equals(that.mediaStreams) && mediaFormatInfo.equals(that.mediaFormatInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mediaStreams, mediaFormatInfo);
    }

    @Override
    public String toString() {
        return "MediaContentInfo{" +
                "mediaStreams=" + mediaStreams +
                ", mediaFormatInfo=" + mediaFormatInfo +
                '}';
    }
}
