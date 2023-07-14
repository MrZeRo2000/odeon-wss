package com.romanpulov.odeonwss.dto;

import com.romanpulov.odeonwss.entity.ArtistCategoryType;
import com.romanpulov.odeonwss.entity.ArtistType;
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
                newDTO.setArtistType(ArtistType.fromCode(row.getArtistTypeCode()));
                newDTO.setDetailId(row.getDetailId());
                newDTO.setArtistBiography(row.getArtistBiography());
                newDTO.setHasLyrics(TransformRules.booleanFromLong(row.getHasLyrics()));

                artistDTOMap.put(id, newDTO);
                return newDTO;
            });

            if (row.getCategoryTypeCode() != null) {
                if (row.getCategoryTypeCode().equals(ArtistCategoryType.GENRE.getCode())) {
                    dto.setGenre(row.getCategoryName());
                } else if (row.getCategoryTypeCode().equals(ArtistCategoryType.STYLE.getCode())) {
                    dto.getStyles().add(row.getCategoryName());
                }
            }
        }

        return new ArrayList<>(artistDTOMap.values());
    }
}
