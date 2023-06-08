package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface TrackDTO extends AbstractEntityDTO {
    Long getArtifactId();
    ArtistDTO getArtist();
    ArtistDTO getPerformerArtist();
    IdNameDTO getDvType();
    String getTitle();
    Long getDuration();
    Long getDiskNum();
    Long getNum();
    Long getSize();
    Long getBitRate();
    List<String> getFileNames();
    DVProductDTO getDvProduct();
}
