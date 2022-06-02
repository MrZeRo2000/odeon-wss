package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.ArtistCategoriesDetailDTO;
import com.romanpulov.odeonwss.dto.ArtistCategoryArtistDTO;
import com.romanpulov.odeonwss.dto.ArtistCategoryArtistListDTO;
import com.romanpulov.odeonwss.dto.ArtistCategoryDetailDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistCategory;
import com.romanpulov.odeonwss.entity.ArtistCategoryType;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArtistCategoryMapper {
    public static List<ArtistCategoryArtistListDTO> fromArtistCategoryArtistsDTO(List<ArtistCategoryArtistDTO> acaList) {
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

    public static ArtistCategoriesDetailDTO fromArtistCategoryDetailDTO(List<ArtistCategoryDetailDTO> acdList) {
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

    public static List<ArtistCategory> createFromArtistCategoriesDetailDTO(Artist artist, ArtistCategoriesDetailDTO acd) {
        List<ArtistCategory> artistCategories = new ArrayList<>();

        // artist category genre
        if (acd.getGenre() != null) {
            ArtistCategory artistCategory = new ArtistCategory();
            artistCategory.setArtist(artist);
            artistCategory.setType(ArtistCategoryType.GENRE);
            artistCategory.setName(acd.getGenre());

            artistCategories.add(artistCategory);
        }

        for (String style: acd.getStyles().stream().distinct().sorted().collect(Collectors.toList())) {
            ArtistCategory artistCategory = new ArtistCategory();
            artistCategory.setArtist(artist);
            artistCategory.setType(ArtistCategoryType.STYLE);
            artistCategory.setName(style);

            artistCategories.add(artistCategory);
        }

        return artistCategories;
    }

    public static boolean categoryValueEquals(ArtistCategory category1, ArtistCategory category2) {
        return (category1.getType() == category2.getType() && Objects.equals(category1.getName(), category2.getName()));
    }

    public static Pair<List<ArtistCategory>, List<ArtistCategory>> mergeCategories(List<ArtistCategory> oldCategories, List<ArtistCategory> newCategories) {
        List<ArtistCategory> createdCategories = new ArrayList<>();
        List<ArtistCategory> deletedCategories = new ArrayList<>();

        // get new
        for (ArtistCategory artistCategory: newCategories) {
            oldCategories.stream().filter(o -> categoryValueEquals(artistCategory, o)).findFirst().ifPresentOrElse(
                    category -> {},
                    () -> createdCategories.add(artistCategory)
            );
        }

        // get deleted
        for (ArtistCategory artistCategory: oldCategories) {
            newCategories.stream().filter(n -> categoryValueEquals(artistCategory, n)).findFirst().ifPresentOrElse(
                    category -> {},
                    () -> deletedCategories.add(artistCategory)
            );
        }

        return Pair.of(createdCategories, deletedCategories);
    }
}
