package com.romanpulov.odeonwss.utils.media;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MediaFileInfo {
    private final String fileName;

    public String getFileName() {
        return fileName;
    }

    private final MediaType primaryMediaType;

    public MediaType getPrimaryMediaType() {
        return primaryMediaType;
    }

    private final List<MediaStreamInfo> mediaStreams;

    public List<MediaStreamInfo> getMediaStreams() {
        return mediaStreams;
    }

    private final MediaFormatInfo mediaFormatInfo;

    public MediaFormatInfo getMediaFormatInfo() {
        return mediaFormatInfo;
    }

    public MediaFileInfo(String fileName, MediaType primaryMediaType, List<MediaStreamInfo> mediaStreams, MediaFormatInfo mediaFormatInfo) {
        this.fileName = fileName;
        this.primaryMediaType = primaryMediaType;
        this.mediaStreams = Collections.unmodifiableList(mediaStreams);
        this.mediaFormatInfo = mediaFormatInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaFileInfo that = (MediaFileInfo) o;
        return fileName.equals(that.fileName) && primaryMediaType == that.primaryMediaType && Objects.equals(mediaStreams, that.mediaStreams) && Objects.equals(mediaFormatInfo, that.mediaFormatInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, primaryMediaType, mediaStreams, mediaFormatInfo);
    }

    @Override
    public String toString() {
        return "MediaFileInfo{" +
                "fileName='" + fileName + '\'' +
                ", primaryMediaType=" + primaryMediaType +
                ", mediaStreams=" + mediaStreams +
                ", mediaFormatInfo=" + mediaFormatInfo +
                '}';
    }
}
