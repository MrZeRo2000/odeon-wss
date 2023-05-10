package com.romanpulov.odeonwss.dto;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ArtistTransformer {
    public List<ArtistDTO> transform(List<ArtistFlatDTO> rs) {
        final Map<Long, ArtistDTOImpl> artistDTOMap = new LinkedHashMap<>();

        for (ArtistFlatDTO row: rs) {
            Long id = row.getId();
            ArtistDTOImpl dto = Optional.ofNullable(artistDTOMap.get(id)).orElseGet(() -> {
                ArtistDTOImpl newDTO = new ArtistDTOImpl();
                newDTO.setId(id);
                newDTO.setArtistName(row.getArtistName());
                newDTO.setArtistType(row.getArtistType().getCode());
                newDTO.setDetailId(row.getDetailId());

                artistDTOMap.put(id, newDTO);
                return newDTO;
            });

            if (row.getCategoryType() != null) {
                switch (row.getCategoryType()) {
                    case GENRE:
                        dto.setGenre(row.getCategoryName());
                        break;
                    case STYLE:
                        dto.getStyles().add(row.getCategoryName());
                        break;
                }
            }
        }

        return new ArrayList<>(artistDTOMap.values());
    }
}
