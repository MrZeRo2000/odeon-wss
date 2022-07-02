package com.romanpulov.odeonwss.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "compositions_media_files")
public class CompositionMediaFile {
    @Id
    @Column(name = "mdfl_id")
    private Long mediaFileId;


    @Column(name = "comp_id")
    private Long compositionId;


    public Long getCompositionId() {
        return compositionId;
    }

    public void setCompositionId(Long compositionId) {
        this.compositionId = compositionId;
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
        CompositionMediaFile that = (CompositionMediaFile) o;
        return compositionId.equals(that.compositionId) && mediaFileId.equals(that.mediaFileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compositionId, mediaFileId);
    }

    @Override
    public String toString() {
        return "CompositionMediaFile{" +
                "compositionId=" + compositionId +
                ", mediaFileId=" + mediaFileId +
                '}';
    }
}
