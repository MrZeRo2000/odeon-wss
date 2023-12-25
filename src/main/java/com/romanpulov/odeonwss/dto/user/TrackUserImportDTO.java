package com.romanpulov.odeonwss.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.romanpulov.odeonwss.dto.ArtifactDTO;
import com.romanpulov.odeonwss.dto.IdNameDTO;
import com.romanpulov.odeonwss.dto.MediaFileDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrackUserImportDTO {
    private ArtifactDTO artifact;
    private MediaFileDTO mediaFile;
    private IdNameDTO dvType;
    private Long num;
    private List<String> titles;
    private List<String> chapters;

    public ArtifactDTO getArtifact() {
        return artifact;
    }

    public void setArtifact(ArtifactDTO artifact) {
        this.artifact = artifact;
    }

    public MediaFileDTO getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(MediaFileDTO mediaFile) {
        this.mediaFile = mediaFile;
    }

    public IdNameDTO getDvType() {
        return dvType;
    }

    public void setDvType(IdNameDTO dvType) {
        this.dvType = dvType;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public List<String> getTitles() {
        return titles;
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
    }

    public List<String> getChapters() {
        return chapters;
    }

    public void setChapters(List<String> chapters) {
        this.chapters = chapters;
    }

    @Override
    public String toString() {
        return "TrackUserImportDTO{" +
                "artifact=" + artifact +
                ", mediaFile=" + mediaFile +
                ", dvType=" + dvType +
                ", num=" + num +
                ", titles=" + titles +
                ", chapters=" + chapters +
                '}';
    }
}
