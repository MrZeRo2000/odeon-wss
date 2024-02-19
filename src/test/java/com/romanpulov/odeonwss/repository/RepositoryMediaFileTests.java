package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityMediaFileBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityTrackBuilder;
import com.romanpulov.odeonwss.dto.MediaFileDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.logging.Logger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryMediaFileTests {

    private static final Logger log = Logger.getLogger(RepositoryMediaFileTests.class.getSimpleName());

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    TrackRepository trackRepository;

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testCreateGet() {
        Artist artist = new EntityArtistBuilder()
                .withType(ArtistType.ARTIST)
                .withName("Artist 1")
                .build();

        artistRepository.save(artist);

        Artifact artifact = new EntityArtifactBuilder()
                .withArtifactType(artifactTypeRepository.getWithMP3())
                .withArtist(artist)
                .withTitle("Title 1")
                .withYear(2022L)
                .withDuration(12346L)
                .build();

        artifactRepository.save(artifact);

        MediaFile mediaFile = new EntityMediaFileBuilder()
                .withArtifact(artifact)
                .withName("AAA.mp3")
                .withFormat("MP3")
                .withSize(423L)
                .withDuration(6234L)
                .withBitrate(320L)
                .withMigrationId(230L)
                .build();

        Track track = new EntityTrackBuilder()
                .withArtifact(artifact)
                .withTitle("Track title")
                .withNum(1L)
                .withDiskNum(1L)
                .withDuration(123456L)
                .build();

        trackRepository.save(track);

        MediaFile savedMediaFile = mediaFileRepository.save(mediaFile);
        Assertions.assertNotNull(savedMediaFile.getId());
        Assertions.assertEquals(artifact, mediaFile.getArtifact());
        Assertions.assertEquals(savedMediaFile.getName(), mediaFile.getName());
        Assertions.assertEquals(savedMediaFile.getFormat(), mediaFile.getFormat());
        Assertions.assertEquals(savedMediaFile.getSize(), mediaFile.getSize());
        Assertions.assertEquals(savedMediaFile.getDuration(), mediaFile.getDuration());
        Assertions.assertEquals(savedMediaFile.getBitrate(), mediaFile.getBitrate());
        Assertions.assertEquals(230L, mediaFile.getMigrationId());

        log.info("Saved MediaFile:" + savedMediaFile);

        mediaFile = new EntityMediaFileBuilder()
                .withArtifact(artifact)
                .withName("BBB.mp3")
                .withFormat("MP3")
                .withSize(323L)
                .withDuration(7234L)
                .withBitrate(320L)
                .build();
        mediaFileRepository.save(mediaFile);

        Artifact movieArtifact = new EntityArtifactBuilder()
                .withArtifactType(artifactTypeRepository.getWithDVMovies())
                .withTitle("Movie 1")
                .withDuration(12346L)
                .build();
        artifactRepository.save(movieArtifact);

        mediaFileRepository.save(new EntityMediaFileBuilder()
                .withArtifact(movieArtifact)
                        .withName("Scare movie.MKV")
                        .withFormat("MKV")
                        .withSize(473453L)
                        .withDuration(23146L)
                        .withBitrate(2000L)
                        .withDimensions(640, 480)
                        .withExtra("{\"extra\": [\"00:03:44\", \"01:01:22\"]}")
                .build()
        );
    }

    @Test
    @Order(2)
    void testCreateMinimal() {
        Artifact artifact = artifactRepository.findById(1L).orElseThrow();

        MediaFile mediaFile = new EntityMediaFileBuilder()
                .withArtifact(artifact)
                .withName("CCC.mp3")
                .withFormat("MP3")
                .withSize(0L)
                .build();
        mediaFileRepository.save(mediaFile);
    }

    @Test
    @Order(3)
    void testTrackValidation() {
        List<MediaFileValidationDTO> mediaFileValidation =
                mediaFileRepository.getTrackMediaFileValidationMusic(ArtistType.ARTIST, artifactTypeRepository.getWithMP3());
        Assertions.assertEquals(1, mediaFileValidation.size());
    }

    @Test
    @Order(3)
    void testMediaAttributes () {
        MediaFile mediaFile = mediaFileRepository
                .getMediaFilesByArtifactType(artifactTypeRepository.getWithDVMovies())
                .get(0);
        assertThat(mediaFile.getName()).isEqualTo("Scare movie.MKV");
        assertThat(mediaFile.getFormat()).isEqualTo("MKV");
        assertThat(mediaFile.getSize()).isEqualTo(473453L);
        assertThat(mediaFile.getDuration()).isEqualTo(23146L);
        assertThat(mediaFile.getBitrate()).isEqualTo(2000L);
        assertThat(mediaFile.getWidth()).isEqualTo(640L);
        assertThat(mediaFile.getHeight()).isEqualTo(480L);
        assertThat(mediaFile.getExtra()).contains("extra");
    }

    @Test
    @Order(3)
    void testMediaFileValidation() {
        List<MediaFileValidationDTO> mediaFileValidation =
                mediaFileRepository.getArtifactMediaFileValidationMusic(
                        ArtistType.ARTIST, artifactTypeRepository.getWithMP3());
        assertThat(mediaFileValidation.size()).isEqualTo(3);
    }

    @Test
    @Order(4)
    void testMediaFileDTOById() {
        MediaFileDTO dto = mediaFileRepository.findDTOById(1L).orElseThrow();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("AAA.mp3");
    }

    @Test
    @Order(5)
    void testMediaFileAllDTO() {
        var audioDTOs = mediaFileRepository.findAllDTOByArtifactId(1L);
        assertThat(audioDTOs.size()).isEqualTo(3);
        assertThat(audioDTOs.get(0).getName()).isEqualTo("AAA.mp3");
        assertThat(audioDTOs.get(1).getName()).isEqualTo("BBB.mp3");
        assertThat(audioDTOs.get(2).getName()).isEqualTo("CCC.mp3");

        var videoDTOs = mediaFileRepository.findAllDTOByArtifactId(2L);
        assertThat(videoDTOs.size()).isEqualTo(1);
        assertThat(videoDTOs.get(0).getWidth()).isEqualTo(640L);
        assertThat(videoDTOs.get(0).getHeight()).isEqualTo(480L);
        assertThat(videoDTOs.get(0).getExtra()).isEqualTo("{\"extra\": [\"00:03:44\", \"01:01:22\"]}");
    }

    @Test
    @Order(6)
    void testGetIdNameDuration() {
        List<MediaFileDTO> idNameDurations = mediaFileRepository.findAllDTOIdNameDurationByArtifactId(1L);

        assertThat(idNameDurations.size()).isEqualTo(3);
        assertThat(idNameDurations.get(0).getName()).isEqualTo("AAA.mp3");
        assertThat(idNameDurations.get(0).getDuration()).isEqualTo(6234L);

        assertThat(idNameDurations.get(1).getName()).isEqualTo("BBB.mp3");
        assertThat(idNameDurations.get(1).getDuration()).isEqualTo(7234L);

        assertThat(idNameDurations.get(2).getName()).isEqualTo("CCC.mp3");
        assertThat(idNameDurations.get(2).getDuration()).isNull();
    }
}
