package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceCompositionTest {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private ArtifactService artifactService;

    @Autowired
    private CompositionService compositionService;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testInsertShouldBeOk() {
        Artist artist = artistRepository.save(new EntityArtistBuilder()
                .withType(ArtistType.ARTIST)
                .withName("Artist 1")
                .build()
        );

        Artifact artifact = artifactRepository.save(
                new EntityArtifactBuilder()
                        .withArtifactType(ArtifactType.withMP3())
                        .withArtist(artist)
                        .withTitle("Title 1")
                        .withYear(2001L)
                        .withDuration(12345L)
                        .build()
        );

    }
}
