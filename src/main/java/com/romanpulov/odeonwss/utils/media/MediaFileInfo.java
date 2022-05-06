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

    private final MediaContentInfo mediaContentInfo;

    public MediaContentInfo getMediaContentInfo() {
        return mediaContentInfo;
    }

    public MediaFileInfo(String fileName, MediaType primaryMediaType, MediaContentInfo mediaContentInfo) {
        this.fileName = fileName;
        this.primaryMediaType = primaryMediaType;
        this.mediaContentInfo = mediaContentInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaFileInfo that = (MediaFileInfo) o;
        return fileName.equals(that.fileName) && primaryMediaType == that.primaryMediaType && mediaContentInfo.equals(that.mediaContentInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, primaryMediaType, mediaContentInfo);
    }

    @Override
    public String toString() {
        return "MediaFileInfo{" +
                "fileName='" + fileName + '\'' +
                ", primaryMediaType=" + primaryMediaType +
                ", mediaContentInfo=" + mediaContentInfo +
                '}';
    }
}
