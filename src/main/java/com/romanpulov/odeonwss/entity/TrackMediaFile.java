package com.romanpulov.odeonwss.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tracks_media_files")
public class TrackMediaFile {
    @Id
    @Column(name = "mdfl_id")
    private Long mediaFileId;


    @Column(name = "trck_id")
    private Long trackId;


    public Long getTrackId() {
        return trackId;
    }

    public void setTrackId(Long trackId) {
        this.trackId = trackId;
    }

    public Long getMediaFileId() {
        return mediaFileId;
    }

    public void setMediaFileId(Long mediaFileId) {
        this.mediaFileId = mediaFileId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackMediaFile that = (TrackMediaFile) o;
        return trackId.equals(that.trackId) && mediaFileId.equals(that.mediaFileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackId, mediaFileId);
    }

    @Override
    public String toString() {
        return "TrackMediaFile{" +
                "trackId=" + trackId +
                ", mediaFileId=" + mediaFileId +
                '}';
    }
}
