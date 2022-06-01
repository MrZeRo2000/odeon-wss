package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistDetail;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.entitybuilder.EntityArtistDetailBuilder;
import org.hibernate.JDBCException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.jdbc.Sql;

import java.util.NoSuchElementException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryArtistDetailTests {

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtistDetailRepository artistDetailRepository;

    @Test
    @Order(1)
    @Sql(value = {"/schema.sql", "/data.sql"})
    void testCRUD() throws Exception {
        Artist artist1 = artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 1").build());
        artistDetailRepository.save(new EntityArtistDetailBuilder().withArtist(artist1).withBiography("Bio 1").build());

        // no duplicate with same artist
        Assertions.assertThrows(JpaSystemException.class, () -> artistDetailRepository.save(new EntityArtistDetailBuilder().withArtist(artist1).withBiography("Bio 2").build()));

        Artist artist2 = artistRepository.save(new EntityArtistBuilder().withType(ArtistType.ARTIST).withName("Name 2").build());
        artistDetailRepository.save(new EntityArtistDetailBuilder().withArtist(artist2).withBiography("Bio 2").build());

        ArtistDetail artistDetail = artistDetailRepository.findArtistDetailByArtist(artist2).orElseThrow();
        Assertions.assertEquals("Bio 2", artistDetail.getBiography());

        artistDetailRepository.delete(artistDetail);

        Assertions.assertThrows(NoSuchElementException.class, () -> artistDetailRepository.findArtistDetailByArtist(artist2).orElseThrow());
    }
}