package com.romanpulov.odeonwss.generator;

import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DataGeneratorTest {

    @Autowired
    DataGenerator dataGenerator;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Autowired
    TrackRepository trackRepository;

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void testCreateArtists() {
        String json =
"""
{
    "artists": [
        {
            "artistType": "A",
            "artistName": "Ozzy Osbourne"
        },
        {
            "artistType": "A",
            "artistName": "A-HA"
        }
    ]
}
""";
        dataGenerator.generateFromJSON(json);

        assertThat(artistRepository.getAllByType(ArtistType.ARTIST).size()).isEqualTo(2);
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void testMovies() {
        String json =
"""
{
    "artifacts": [
        {
            "artifactType": {
                "id": 202
            },
            "title": "10 ярдов"
        },
        {
            "artifactType": {
                "id": 202
            },
            "title": "Крепкий орешек"
        }
    ],
    "mediaFiles": [
        {
            "artifactTitle": "10 ярдов",
            "name": "10 yards part 1.mkv"
        },
        {
            "artifactTitle": "10 ярдов",
            "name": "10 yards part 2.mkv"
        },
        {
            "artifactTitle": "Крепкий орешек",
            "name": "die.hard.mkv"
        }
    ],
    "tracks": [
        {
            "artifact": {
                "title": "10 ярдов"
            },
            "title": "10 ярдов",
            "mediaFiles": [
                {
                    "name": "10 yards part 1.mkv"
                },
                {
                    "name": "10 yards part 2.mkv"
                }
            ]
        },
        {
            "artifact": {
                "title": "Крепкий орешек"
            },
            "title": "Крепкий орешек",
            "mediaFiles": [
                {
                    "name": "die.hard.mkv"
                }
            ]
        }
    ]
}
""";

        dataGenerator.generateFromJSON(json);

        assertThat(artifactRepository.getAllByArtifactType(artifactTypeRepository.getWithDVMovies()).size())
                .isEqualTo(2);

        assertThat(mediaFileRepository.getMediaFilesByArtifactType(artifactTypeRepository.getWithDVMovies()).size())
                .isEqualTo(3);

        assertThat(trackRepository.getTracksByArtifactType(artifactTypeRepository.getWithDVMovies()).size())
                .isEqualTo(2);
    }
}
