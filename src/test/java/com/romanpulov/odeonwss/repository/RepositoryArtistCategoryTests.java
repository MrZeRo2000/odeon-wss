package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistCategoryBuilder;
import com.romanpulov.odeonwss.dto.ArtistDTOImpl;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistCategory;
import com.romanpulov.odeonwss.entity.ArtistCategoryType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.utils.EnumUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryArtistCategoryTests {

    @Autowired
    ArtistCategoryRepository artistCategoryRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Test
    @Order(1)
    @Sql(value = {"/schema.sql", "/data.sql"})
    void testCRUD() {
        Artist artist = artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 1").build());
        ArtistCategory artistCategory = artistCategoryRepository.save(
                new EntityArtistCategoryBuilder().withType(ArtistCategoryType.GENRE).withArtist(artist).withName("Rock").build()
        );

        ArtistCategory savedArtistCategory = artistCategoryRepository.getArtistCategoriesByArtistOrderByTypeAscNameAsc(artist).get(0);
        Assertions.assertEquals("Rock", savedArtistCategory.getName());
        Assertions.assertEquals(ArtistCategoryType.GENRE, savedArtistCategory.getType());

        artistCategory.setName("Pop");
        artistCategoryRepository.save(artistCategory);
        Assertions.assertEquals("Pop", artistCategoryRepository.getArtistCategoriesByArtistOrderByTypeAscNameAsc(artist).get(0).getName());

        artistCategoryRepository.save(
                new EntityArtistCategoryBuilder().withType(ArtistCategoryType.STYLE).withArtist(artist).withName("Alternative Rock").build()
        );

        Assertions.assertEquals(2, StreamSupport.stream(artistCategoryRepository.findAll().spliterator(), false).count());
    }

    @Test
    @Order(2)
    void testCascade() {
        Artist artist = artistRepository.getAllByType(ArtistType.ARTIST).get(0);

        // delete with child element works
        artistRepository.delete(artist);
        Assertions.assertEquals(0, StreamSupport.stream(artistCategoryRepository.findAll().spliterator(), false).count());
    }

    @Test
    @Order(3)
    void testWithCategories() {
        Artist artist1 = artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 3").build());
        artistCategoryRepository.save(
                new EntityArtistCategoryBuilder().withType(ArtistCategoryType.GENRE).withArtist(artist1).withName("Rock").build()
        );
        artistCategoryRepository.save(
                new EntityArtistCategoryBuilder().withType(ArtistCategoryType.STYLE).withArtist(artist1).withName("Pop").build()
        );
        artistCategoryRepository.save(
                new EntityArtistCategoryBuilder().withType(ArtistCategoryType.STYLE).withArtist(artist1).withName("Alternative Rock").build()
        );

        Artist artist2 = artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 1").build());
        artistCategoryRepository.save(
                new EntityArtistCategoryBuilder().withType(ArtistCategoryType.GENRE).withArtist(artist2).withName("Pop").build()
        );
        artistCategoryRepository.save(
                new EntityArtistCategoryBuilder().withType(ArtistCategoryType.STYLE).withArtist(artist2).withName("Electronic").build()
        );
        artistCategoryRepository.save(
                new EntityArtistCategoryBuilder().withType(ArtistCategoryType.STYLE).withArtist(artist2).withName("Rap").build()
        );

        List<Artist> artists = artistRepository.getAllByType(ArtistType.ARTIST);
        Assertions.assertEquals(2, artists.size());

        List<ArtistCategory> artistCategories = artistCategoryRepository.findByOrderByArtistNameAsc();
        Assertions.assertEquals(6, artistCategories.size());
        Assertions.assertEquals("Name 1", artistCategories.get(0).getArtist().getName());

        var acaDTOs = artistRepository.findAllFlatDTO();
        Assertions.assertEquals(6, acaDTOs.size());

        List<ArtistDTOImpl> acalDTOs = new ArrayList<>();
        acaDTOs.forEach(acaDTO -> {
            if ((acalDTOs.isEmpty()) || (!Objects.equals(acalDTOs.get(acalDTOs.size() - 1).getId(), acaDTO.getId()))) {
                ArtistDTOImpl newArtistDTO = new ArtistDTOImpl();
                newArtistDTO.setId(acaDTO.getId());
                newArtistDTO.setArtistName(acaDTO.getArtistName());
                newArtistDTO.setArtistType(EnumUtils.getEnumFromString(ArtistType.class, acaDTO.getArtistTypeCode()));
                newArtistDTO.setDetailId(acaDTO.getDetailId());

                acalDTOs.add(newArtistDTO);
            }
            if (acaDTO.getCategoryTypeCode().equals("G")) {
                acalDTOs.get(acalDTOs.size()-1).setGenre(acaDTO.getCategoryName());
            } else {
                acalDTOs.get(acalDTOs.size()-1).getStyles().add(acaDTO.getCategoryName());
            }
        });

        Assertions.assertEquals(2, acalDTOs.size());
        Assertions.assertEquals("Name 1", acalDTOs.get(0).getArtistName());
        Assertions.assertEquals("Name 3", acalDTOs.get(1).getArtistName());
        Assertions.assertEquals("Alternative Rock", acalDTOs.get(1).getStyles().get(0));
        Assertions.assertEquals("Rock", acalDTOs.get(1).getGenre());

        var acaDTO1 = artistRepository.findFlatDTOById(artist1.getId());
        Assertions.assertEquals(3, acaDTO1.size());
        Assertions.assertEquals(0, artistRepository.findFlatDTOById(777L).size());
    }

}
