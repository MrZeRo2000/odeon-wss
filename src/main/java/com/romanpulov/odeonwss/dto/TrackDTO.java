package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(as = TrackDTOImpl.class)
public interface TrackDTO extends BaseEntityDTO {
    ArtifactTypeDTO getArtifactType();
    ArtifactDTO getArtifact();
    ArtistDTO getArtist();
    ArtistDTO getPerformerArtist();
    IdNameDTO getDvType();
    String getTitle();
    Long getDuration();
    Long getDiskNum();
    Long getNum();
    Long getSize();
    Long getBitRate();
    List<MediaFileDTO> getMediaFiles();
    DVProductDTO getDvProduct();
}
