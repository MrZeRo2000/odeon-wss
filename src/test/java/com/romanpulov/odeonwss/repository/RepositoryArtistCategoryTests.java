package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.ArtistCategoryArtistDTO;
import com.romanpulov.odeonwss.dto.ArtistCategoryTableDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistCategory;
import com.romanpulov.odeonwss.entity.ArtistCategoryType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistCategoryBuilder;
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
    void testCRUD() throws Exception {
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
    void testCascade() throws Exception {
        Artist artist = artistRepository.getAllByType(ArtistType.ARTIST).get(0);

        // delete with child element works
        artistRepository.delete(artist);
        Assertions.assertEquals(0, StreamSupport.stream(artistCategoryRepository.findAll().spliterator(), false).count());
    }

    @Test
    @Order(3)
    void testWithCategories() throws Exception {
        Artist artist1 = artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 3").build());
        ArtistCategory artistCategory11 = artistCategoryRepository.save(
                new EntityArtistCategoryBuilder().withType(ArtistCategoryType.GENRE).withArtist(artist1).withName("Rock").build()
        );
        ArtistCategory artistCategory12 = artistCategoryRepository.save(
                new EntityArtistCategoryBuilder().withType(ArtistCategoryType.STYLE).withArtist(artist1).withName("Pop").build()
        );
        ArtistCategory artistCategory13 = artistCategoryRepository.save(
                new EntityArtistCategoryBuilder().withType(ArtistCategoryType.STYLE).withArtist(artist1).withName("Alternative Rock").build()
        );

        Artist artist2 = artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 1").build());
        ArtistCategory artistCategory21 = artistCategoryRepository.save(
                new EntityArtistCategoryBuilder().withType(ArtistCategoryType.GENRE).withArtist(artist2).withName("Pop").build()
        );
        ArtistCategory artistCategory22 = artistCategoryRepository.save(
                new EntityArtistCategoryBuilder().withType(ArtistCategoryType.STYLE).withArtist(artist2).withName("Electronic").build()
        );
        ArtistCategory artistCategory23 = artistCategoryRepository.save(
                new EntityArtistCategoryBuilder().withType(ArtistCategoryType.STYLE).withArtist(artist2).withName("Rap").build()
        );

        List<Artist> artists = artistRepository.getAllByTypeOrderByName(ArtistType.ARTIST);
        Assertions.assertEquals(2, artists.size());

        List<ArtistCategory> artistCategories = artistCategoryRepository.findByOrderByArtistNameAsc();
        Assertions.assertEquals(6, artistCategories.size());
        Assertions.assertEquals("Name 1", artistCategories.get(0).getArtist().getName());

        List<ArtistCategoryArtistDTO> acaDTOs = artistCategoryRepository.getAllWithArtistOrdered();
        Assertions.assertEquals(6, acaDTOs.size());

        List<ArtistCategoryTableDTO> acalDTOs = new ArrayList<>();
        acaDTOs.forEach(acaDTO -> {
            if ((acalDTOs.size() == 0) || (!Objects.equals(acalDTOs.get(acalDTOs.size() - 1).getId(), acaDTO.getId()))) {
                acalDTOs.add(new ArtistCategoryTableDTO(acaDTO.getId(), acaDTO.getArtistName(), acaDTO.getArtistType().getCode(), acaDTO.getDetailId()));
            }
            if (acaDTO.getCategoryType().equals(ArtistCategoryType.GENRE)) {
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

        acaDTOs = artistCategoryRepository.getAllWithArtistByIdOrdered(artist1.getId());
        Assertions.assertEquals(3, acaDTOs.size());

        Assertions.assertEquals(0, artistCategoryRepository.getAllWithArtistByIdOrdered(777L).size());
    }

}
