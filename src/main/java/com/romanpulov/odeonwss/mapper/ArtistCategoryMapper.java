package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.ArtistCategoryArtistDTO;
import com.romanpulov.odeonwss.dto.ArtistCategoryArtistListDTO;
import com.romanpulov.odeonwss.entity.ArtistCategoryType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ArtistCategoryMapper {
    public List<ArtistCategoryArtistListDTO> transformArtistCategoryArtistsDTO(List<ArtistCategoryArtistDTO> acaList) {
        List<ArtistCategoryArtistListDTO> result = new ArrayList<>();

        acaList.forEach(aca -> {
            if ((result.size() == 0) || (!Objects.equals(result.get(result.size() - 1).getId(), aca.getId()))) {
                result.add(new ArtistCategoryArtistListDTO(aca.getId(), aca.getArtistName(), aca.getDetailId()));
            }
            if (aca.getCategoryType() != null &&  aca.getCategoryName() != null && aca.getCategoryType().equals(ArtistCategoryType.GENRE)) {
                result.get(result.size()-1).setGenre(aca.getCategoryName());
            } else {
                result.get(result.size()-1).getStyles().add(aca.getCategoryName());
            }
        });

        return result;
    }
}
