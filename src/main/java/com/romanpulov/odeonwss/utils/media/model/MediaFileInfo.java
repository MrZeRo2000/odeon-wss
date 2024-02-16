package com.romanpulov.odeonwss.utils.media.model;

import java.util.Objects;

public class MediaFileInfo {
    private final String fileName;

    public String getFileName() {
        return fileName;
    }

    private final MediaContentInfo mediaContentInfo;

    public MediaContentInfo getMediaContentInfo() {
        return mediaContentInfo;
    }

    public MediaFileInfo(String fileName, MediaContentInfo mediaContentInfo) {
        this.fileName = fileName;
        this.mediaContentInfo = mediaContentInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaFileInfo that = (MediaFileInfo) o;
        return fileName.equals(that.fileName) && mediaContentInfo.equals(that.mediaContentInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, mediaContentInfo);
    }

    @Override
    public String toString() {
        return "MediaFileInfo{" +
                "fileName='" + fileName + '\'' +
                ", mediaContentInfo=" + mediaContentInfo +
                '}';
    }
}
