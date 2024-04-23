package com.romanpulov.odeonwss.dto;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ArtifactTransformer {
    public List<ArtifactDTO> transform(Collection<ArtifactFlatDTO> rs) {
        final Map<Long, ArtifactTypeDTO> artifactTypeDTOMap = new HashMap<>();
        final Map<Long, ArtistDTO> artistDTOMap = new HashMap<>();
        final Map<Long, ArtifactDTOImpl> artifactDTOMap = new LinkedHashMap<>();

        for (ArtifactFlatDTO row: rs) {
            long id = row.getId();
            ArtifactDTOImpl dto = artifactDTOMap.computeIfAbsent(id, new_id -> {
                ArtifactDTOImpl newDTO = new ArtifactDTOImpl();
                newDTO.setId(new_id);

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
                                newArtistDTO.setArtistType(row.getArtistType());
                                newArtistDTO.setArtistName(row.getArtistName());

                                return newArtistDTO;
                            }
                    ));
                }

                if (row.getPerformerArtistId() != null) {
                    newDTO.setPerformerArtist(artistDTOMap.computeIfAbsent(
                            row.getPerformerArtistId(),
                            v -> {
                                ArtistDTOImpl newPerformerArtistDTO = new ArtistDTOImpl();
                                newPerformerArtistDTO.setId(row.getPerformerArtistId());
                                newPerformerArtistDTO.setArtistType(row.getArtistType());
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

                return newDTO;
            });

            if (row.getTagName() != null && !row.getTagName().isEmpty()) {
                dto.getTags().add(row.getTagName());
            }
        }

        return new ArrayList<>(artifactDTOMap.values());
    }

    public ArtifactDTO transformOne(ArtifactFlatDTO dto) {
        return this.transform(List.of(dto)).get(0);
    }
}
