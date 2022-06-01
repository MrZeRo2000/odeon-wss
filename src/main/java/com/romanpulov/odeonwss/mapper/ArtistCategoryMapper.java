package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.ArtistCategoriesDetailDTO;
import com.romanpulov.odeonwss.dto.ArtistCategoryArtistDTO;
import com.romanpulov.odeonwss.dto.ArtistCategoryArtistListDTO;
import com.romanpulov.odeonwss.dto.ArtistCategoryDetailDTO;
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

    public ArtistCategoriesDetailDTO transformArtistCategoryDetailDTO(List<ArtistCategoryDetailDTO> acdList) {
        ArtistCategoriesDetailDTO result = null;

        for (ArtistCategoryDetailDTO acd: acdList) {
            if (result == null) {
                result = new ArtistCategoriesDetailDTO();
                result.setId(acd.getId());
                result.setArtistType(acd.getArtistType());
                result.setArtistName(acd.getArtistName());

                result.setArtistBiography(acd.getArtistBiography());
            }
            if (acd.getCategoryType().equals(ArtistCategoryType.GENRE)) {
                result.setGenre(acd.getCategoryName());
            } else if (acd.getCategoryType().equals(ArtistCategoryType.STYLE)) {
                result.getStyles().add(acd.getCategoryName());
            }
        }

        return result;
    }
}
