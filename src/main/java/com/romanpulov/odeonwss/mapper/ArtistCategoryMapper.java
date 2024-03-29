package com.romanpulov.odeonwss.mapper;

import com.romanpulov.odeonwss.dto.ArtistDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistCategory;
import com.romanpulov.odeonwss.entity.ArtistCategoryType;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ArtistCategoryMapper {

    public List<ArtistCategory> createFromArtistDTO(Artist artist, ArtistDTO acd) {
        List<ArtistCategory> artistCategories = new ArrayList<>();

        // artist category genre
        if (acd.getGenre() != null) {
            ArtistCategory artistCategory = new ArtistCategory();
            artistCategory.setArtist(artist);
            artistCategory.setType(ArtistCategoryType.GENRE);
            artistCategory.setName(acd.getGenre());

            artistCategories.add(artistCategory);
        }

        for (String style: acd.getStyles().stream().distinct().sorted().toList()) {
            ArtistCategory artistCategory = new ArtistCategory();
            artistCategory.setArtist(artist);
            artistCategory.setType(ArtistCategoryType.STYLE);
            artistCategory.setName(style);

            artistCategories.add(artistCategory);
        }

        return artistCategories;
    }

    public boolean categoryValueEquals(ArtistCategory category1, ArtistCategory category2) {
        return (category1.getType() == category2.getType() && Objects.equals(category1.getName(), category2.getName()));
    }

    public Pair<List<ArtistCategory>, List<ArtistCategory>> mergeCategories(List<ArtistCategory> oldCategories, List<ArtistCategory> newCategories) {
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
