package com.romanpulov.odeonwss.repository;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistCategory;
import com.romanpulov.odeonwss.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.entitybuilder.EntityArtistCategoryBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.jdbc.Sql;

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
        Artist artist = artistRepository.save(new EntityArtistBuilder().withType("A").withName("Name 1").build());
        ArtistCategory artistCategory = artistCategoryRepository.save(
                new EntityArtistCategoryBuilder().withType("G").withArtist(artist).withName("Rock").build()
        );

        ArtistCategory savedArtistCategory = artistCategoryRepository.getArtistCategoriesByArtistOrderByName(artist).get(0);
        Assertions.assertEquals("Rock", savedArtistCategory.getName());
        Assertions.assertEquals("G", savedArtistCategory.getType());

        artistCategory.setName("Pop");
        artistCategoryRepository.save(artistCategory);
        Assertions.assertEquals("Pop", artistCategoryRepository.getArtistCategoriesByArtistOrderByName(artist).get(0).getName());

        artistCategoryRepository.save(
                new EntityArtistCategoryBuilder().withType("S").withArtist(artist).withName("Alternative Rock").build()
        );

        Assertions.assertEquals(2, StreamSupport.stream(artistCategoryRepository.findAll().spliterator(), false).count());
    }

    @Test
    @Order(2)
    void testCascade() throws Exception {
        Artist artist = artistRepository.getAllByType("A").get(0);

        // delete with child element throws
        Assertions.assertThrows(JpaSystemException.class, () -> artistRepository.delete(artist));

        artistCategoryRepository.deleteAllByArtist(artist);
        artistRepository.delete(artist);
    }
}
