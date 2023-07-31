package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.TrackDTO;
import com.romanpulov.odeonwss.entity.*;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TrackMapper implements EntityDTOMapper<Track, TrackDTO> {
    @Override
    public String getEntityName() {
        return "Track";
    }

    @Override
    public Track fromDTO(TrackDTO dto) {
        Track entity = new Track();

        // immutable fields
        entity.setId(dto.getId());

        // mutable fields
        this.update(entity, dto);

        return entity;
    }

    @Override
    public void update(Track entity, TrackDTO dto) {
        if ((dto.getArtifact() != null) && (dto.getArtifact().getId() != null)) {
            Artifact artifact = new Artifact();
            artifact.setId(dto.getArtifact().getId());
            entity.setArtifact(artifact);
        }

        if ((dto.getArtist() != null) && (dto.getArtist().getId() != null)) {
            Artist artist = new Artist();
            artist.setId(dto.getArtist().getId());
            entity.setArtist(artist);
        } else {
            entity.setArtist(null);
        }

        if ((dto.getPerformerArtist() != null) && (dto.getPerformerArtist().getId() != null)) {
            Artist performerArtist = new Artist();
            performerArtist.setId(dto.getPerformerArtist().getId());
            entity.setPerformerArtist(performerArtist);
        } else {
            entity.setPerformerArtist(null);
        }

        if (dto.getDvType().getId() != null) {
            DVType dvType = new DVType();
            dvType.setId(dto.getDvType().getId());
            entity.setDvType(dvType);
        } else {
            entity.setDvType(null);
        }

        entity.setMediaFiles(dto
                .getMediaFiles()
                .stream()
                .map(d -> {
                    MediaFile mediaFile = new MediaFile();
                    mediaFile.setId(d.getId());
                    return mediaFile;
                })
                .collect(Collectors.toSet()));

        if (dto.getDvProduct() != null) {
            entity.setDvProducts(
                    Stream.of(dto.getDvProduct())
                            .map(d -> {
                                DVProduct dvProduct = new DVProduct();
                                dvProduct.setId(d.getId());
                                return dvProduct;
                            })
                            .collect(Collectors.toSet()));
        } else {
            entity.setDvProducts(new HashSet<>());
        }

        entity.setTitle(dto.getTitle());
        entity.setDuration(dto.getDuration());
        entity.setDiskNum(dto.getDiskNum());
        entity.setNum(dto.getNum());
    }
}
