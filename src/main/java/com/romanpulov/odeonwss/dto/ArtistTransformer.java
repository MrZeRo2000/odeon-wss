package com.romanpulov.odeonwss.dto;

import com.romanpulov.odeonwss.entity.ArtistCategoryType;
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
                newDTO.setArtistType(row.getArtistType());
                newDTO.setDetailId(row.getDetailId());
                newDTO.setHasLyrics(row.getHasLyrics() != null && row.getHasLyrics().equals(1L) ? true : null);

                artistDTOMap.put(id, newDTO);
                return newDTO;
            });

            if (row.getCategoryType() != null) {
                if (row.getCategoryType().equals(ArtistCategoryType.GENRE.getCode())) {
                    dto.setGenre(row.getCategoryName());
                } else if (row.getCategoryType().equals(ArtistCategoryType.STYLE.getCode())) {
                    dto.getStyles().add(row.getCategoryName());
                }
            }
        }

        return new ArrayList<>(artistDTOMap.values());
    }
}
