package com.romanpulov.odeonwss.entity;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "compositions")
public class Composition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comp_id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artf_id", referencedColumnName = "artf_id")
    @NotNull
    private Artifact artifact;

    public Artifact getArtifact() {
        return artifact;
    }

    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mdfl_id", referencedColumnName = "mdfl_id")
    @Nullable
    private MediaFile mediaFile;

    @Nullable
    public MediaFile getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(@Nullable MediaFile mediaFile) {
        this.mediaFile = mediaFile;
    }

    @Column(name = "comp_title")
    @NotNull
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "comp_duration")
    @Nullable
    private Long duration;

    @Nullable
    public Long getDuration() {
        return duration;
    }

    public void setDuration(@Nullable Long duration) {
        this.duration = duration;
    }

    @Column(name = "comp_disk_num")
    @Nullable
    private Long diskNum;

    @Nullable
    public Long getDiskNum() {
        return diskNum;
    }

    public void setDiskNum(@Nullable Long diskNum) {
        this.diskNum = diskNum;
    }

    @Column(name = "comp_num")
    @Nullable
    private Long num;

    @Nullable
    public Long getNum() {
        return num;
    }

    public void setNum(@Nullable Long num) {
        this.num = num;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Composition that = (Composition) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Composition{" +
                "id=" + id +
                ", artifact=" + artifact +
                ", mediaFile=" + mediaFile +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", diskNum=" + diskNum +
                ", num=" + num +
                '}';
    }
}
