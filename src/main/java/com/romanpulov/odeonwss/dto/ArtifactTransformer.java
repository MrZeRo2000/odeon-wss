package com.romanpulov.odeonwss.dto;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ArtifactTransformer {
    public List<ArtifactDTO> transform(Collection<ArtifactFlatDTO> rs) {
        final Map<Long, ArtifactTypeDTO> artifactTypeDTOMap = new HashMap<>();
        final Map<Long, ArtistDTO> artistDTOMap = new HashMap<>();

        List<ArtifactDTO> result = new ArrayList<>();
        for (ArtifactFlatDTO row: rs) {
            ArtifactDTOImpl newDTO = new ArtifactDTOImpl();
            newDTO.setId(row.getId());

            if (row.getArtifactTypeId() != null) {
                newDTO.setArtifactType(artifactTypeDTOMap.computeIfAbsent(
                        row.getArtifactTypeId(),
                        v -> {
                            ArtifactTypeDTOImpl newArtifactTypeDTO = new ArtifactTypeDTOImpl();
                            newArtifactTypeDTO.setId(row.getArtifactTypeId());
                            newArtifactTypeDTO.setName(row.getArtifactTypeName());

                            return newArtifactTypeDTO;
                        }
                        ));
            }

            if (row.getArtistId() != null) {
                newDTO.setArtist(artistDTOMap.computeIfAbsent(
                        row.getArtistId(),
                        v -> {
                            ArtistDTOImpl newArtistDTO = new ArtistDTOImpl();
                            newArtistDTO.setId(row.getArtistId());
                            newArtistDTO.setArtistType(row.getArtistTypeCode());
                            newArtistDTO.setArtistName(row.getArtistName());

                            return newArtistDTO;
                        }
                ));
            }

            if (row.getPerformerArtistId() != null) {
                newDTO.setArtist(artistDTOMap.computeIfAbsent(
                        row.getPerformerArtistId(),
                        v -> {
                            ArtistDTOImpl newPerformerArtistDTO = new ArtistDTOImpl();
                            newPerformerArtistDTO.setId(row.getPerformerArtistId());
                            newPerformerArtistDTO.setArtistType(row.getArtistTypeCode());
                            newPerformerArtistDTO.setArtistName(row.getPerformerArtistName());

                            return newPerformerArtistDTO;
                        }
                ));
            }

            newDTO.setTitle(row.getTitle());
            newDTO.setYear(row.getYear());
            newDTO.setDuration(row.getDuration());
            newDTO.setSize(row.getSize());
            newDTO.setInsertDateTime(row.getInsertDateTime());

            result.add(newDTO);
        }

        return result;
    }
}
