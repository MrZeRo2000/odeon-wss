package com.romanpulov.odeonwss.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TrackDTOImpl implements TrackDTO {
    private Long id;
    private ArtifactTypeDTO artifactType;
    private ArtifactDTO artifact;
    private ArtistDTO artist;
    private ArtistDTO performerArtist;
    private IdNameDTO dvType;
    private String title;
    private Long duration;
    private Long diskNum;
    private Long num;
    private Long size;
    private Long bitRate;
    private List<MediaFileDTO> mediaFiles = new ArrayList<>();
    private DVProductDTO dvProduct;
    private List<String> tags = new ArrayList<>();

    public static TrackDTOImpl fromTrackDTO(TrackDTO dto) {
        TrackDTOImpl instance = new TrackDTOImpl();
        instance.setId(dto.getId());
        instance.setArtifactType(dto.getArtifactType());
        instance.setArtifact(dto.getArtifact());
        instance.setArtist(dto.getArtist());
        instance.setPerformerArtist(dto.getPerformerArtist());
        instance.setDvType(dto.getDvType());
        instance.setTitle(dto.getTitle());
        instance.setDuration(dto.getDuration());
        instance.setDiskNum(dto.getDiskNum());
        instance.setNum(dto.getNum());
        instance.setSize(dto.getSize());
        instance.setBitRate(dto.getBitRate());
        instance.setMediaFiles(dto.getMediaFiles());
        instance.setDvProduct(dto.getDvProduct());

        return instance;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ArtifactTypeDTO getArtifactType() {
        return artifactType;
    }

    public void setArtifactType(ArtifactTypeDTO artifactType) {
        this.artifactType = artifactType;
    }

    @Override
    public ArtifactDTO getArtifact() {
        return artifact;
    }

    public void setArtifact(ArtifactDTO artifact) {
        this.artifact = artifact;
    }

    @Override
    public ArtistDTO getArtist() {
        return artist;
    }

    public void setArtist(ArtistDTO artist) {
        this.artist = artist;
    }

    @Override
    public ArtistDTO getPerformerArtist() {
        return performerArtist;
    }

    public void setPerformerArtist(ArtistDTO performerArtist) {
        this.performerArtist = performerArtist;
    }

    @Override
    public IdNameDTO getDvType() {
        return dvType;
    }

    public void setDvType(IdNameDTO dvType) {
        this.dvType = dvType;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    @Override
    public Long getDiskNum() {
        return diskNum;
    }

    public void setDiskNum(Long diskNum) {
        this.diskNum = diskNum;
    }

    @Override
    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    @Override
    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public Long getBitRate() {
        return bitRate;
    }

    public void setBitRate(Long bitRate) {
        this.bitRate = bitRate;
    }

    public List<MediaFileDTO> getMediaFiles() {
        return mediaFiles;
    }

    public void setMediaFiles(List<MediaFileDTO> mediaFiles) {
        this.mediaFiles = mediaFiles;
    }

    @Override
    public DVProductDTO getDvProduct() {
        return dvProduct;
    }

    public void setDvProduct(DVProductDTO dvProduct) {
        this.dvProduct = dvProduct;
    }

    @Override
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackDTOImpl trackDTO = (TrackDTOImpl) o;
        return Objects.equals(id, trackDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TrackDTOImpl{" +
                "id=" + id +
                ", artifact" + artifact +
                ", artist=" + artist +
                ", performerArtist=" + performerArtist +
                ", dvType=" + dvType +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", diskNum=" + diskNum +
                ", num=" + num +
                ", size=" + size +
                ", bitRate=" + bitRate +
                ", mediaFiles=" + mediaFiles +
                ", dvProduct=" + dvProduct +
                ", tags=" + getTags() +
                '}';
    }
}
