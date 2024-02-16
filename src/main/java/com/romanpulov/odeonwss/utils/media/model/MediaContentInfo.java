package com.romanpulov.odeonwss.utils.media.model;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class MediaContentInfo {
    private final MediaFormatInfo mediaFormatInfo;
    private final List<AbstractMediaStreamInfo> mediaStreams;
    private final List<LocalTime> chapters;

    public MediaFormatInfo getMediaFormatInfo() {
        return mediaFormatInfo;
    }

    public List<AbstractMediaStreamInfo> getMediaStreams() {
        return mediaStreams;
    }

    public List<LocalTime> getChapters() {
        return chapters;
    }

    public MediaContentInfo(
            MediaFormatInfo mediaFormatInfo,
            List<AbstractMediaStreamInfo> mediaStreams,
            List<LocalTime> chapters) {
        this.mediaStreams = mediaStreams;
        this.mediaFormatInfo = mediaFormatInfo;
        this.chapters = chapters;
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
                "mediaFormatInfo=" + mediaFormatInfo +
                ", mediaStreams=" + mediaStreams +
                ", chapters=" + chapters +
                '}';
    }
}
