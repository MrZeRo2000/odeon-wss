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

        entity.setArtist(MapperUtils.createEntityFromDTO(dto.getArtist(), Artist.class));
        entity.setPerformerArtist(MapperUtils.createEntityFromDTO(dto.getPerformerArtist(), Artist.class));
        entity.setDvType(MapperUtils.createEntityFromDTO(dto.getDvType(), DVType.class));

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
