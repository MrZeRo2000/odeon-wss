package com.romanpulov.odeonwss.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityMediaFileBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityTrackBuilder;
import com.romanpulov.odeonwss.dto.MediaFileDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.mapper.MediaFileMapper;
import com.romanpulov.odeonwss.utils.media.MediaInfoMediaFileParser;
import com.romanpulov.odeonwss.utils.media.MediaInfoParsingException;
import com.romanpulov.odeonwss.utils.media.model.MediaContentInfo;
import com.romanpulov.odeonwss.utils.media.model.MediaFileInfo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.nio.file.Files;
import java.nio.file.Path;
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

    @Autowired
    MediaFileMapper mediaFileMapper;

    @Autowired
    ObjectMapper mapper;

    static class TestMediaInfoMediaFileParser extends MediaInfoMediaFileParser {
        public TestMediaInfoMediaFileParser() {
            super("../MediaInfo");
        }

        @Override
        protected MediaContentInfo parseOutput(String text) throws MediaInfoParsingException {
            return super.parseOutput(text);
        }
    }

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testCreateGet() throws Exception {
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

        Path path = Path.of("../odeon-test-data/files/mediainfo_output_1280_720_with_chapters.json");
        String content = Files.readString(path);
        var parser = new TestMediaInfoMediaFileParser();

        MediaContentInfo mediaContentInfo = parser.parseOutput(content);
        MediaFileInfo mediaFileInfo = new MediaFileInfo("Scare movie.MKV", mediaContentInfo);

        MediaFile chaptersMediaFile = new EntityMediaFileBuilder()
                .withArtifact(movieArtifact)
                .withName("Scare movie.MKV")
                .withFormat("MKV")
                .withSize(473453L)
                .build();

        mediaFileMapper.updateFromMediaFileInfo(chaptersMediaFile, mediaFileInfo);
        mediaFileRepository.save(chaptersMediaFile);
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
        assertThat(mediaFile.getSize()).isEqualTo(17433330L);
        assertThat(mediaFile.getDuration()).isEqualTo(28L);
        assertThat(mediaFile.getBitrate()).isEqualTo(4841L);
        assertThat(mediaFile.getWidth()).isEqualTo(1280L);
        assertThat(mediaFile.getHeight()).isEqualTo(720L);
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
        var audioDTO = mediaFileRepository.findDTOById(1L).orElseThrow();
        assertThat(audioDTO.getId()).isEqualTo(1L);
        assertThat(audioDTO.getName()).isEqualTo("AAA.mp3");

        var videoDTO = mediaFileRepository.findDTOById(3L).orElseThrow();
        assertThat(videoDTO.getId()).isEqualTo(3L);
        assertThat(videoDTO.getName()).isEqualTo("Scare movie.MKV");
        assertThat(videoDTO.getFormat()).isEqualTo("MKV");
        assertThat(videoDTO.getSize()).isEqualTo(17433330L);
        assertThat(videoDTO.getDuration()).isEqualTo(28L);
        assertThat(videoDTO.getBitrate()).isEqualTo(4841L);
        assertThat(videoDTO.getWidth()).isEqualTo(1280L);
        assertThat(videoDTO.getHeight()).isEqualTo(720L);

        var chapters = mediaFileMapper.extraToChapters(videoDTO.getExtra());
        assertThat(chapters.get(0)).isEqualTo("00:00:04");
        assertThat(chapters.get(1)).isEqualTo("00:26:00");
        assertThat(chapters.get(2)).isEqualTo("01:35:19");
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
        assertThat(videoDTOs.get(0).getWidth()).isEqualTo(1280L);
        assertThat(videoDTOs.get(0).getHeight()).isEqualTo(720L);
        assertThat(videoDTOs.get(0).getExtra()).isNull();
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
