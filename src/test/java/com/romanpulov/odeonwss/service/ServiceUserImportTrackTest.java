package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.dtobuilder.*;
import com.romanpulov.odeonwss.builder.entitybuilder.*;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.entity.DVOrigin;
import com.romanpulov.odeonwss.exception.EmptyParameterException;
import com.romanpulov.odeonwss.exception.WrongParameterValueException;
import com.romanpulov.odeonwss.repository.*;
import com.romanpulov.odeonwss.service.user.TrackUserImportService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceUserImportTrackTest {
    static final List<String> PRODUCT_NAMES = List.of(
            "Yellow",
            "Green",
            "Brown",
            "Black",
            "White"
            );

    private static final List<String> ARTISTS = List.of(
            "Nightwish",
            "Clawfinger",
            "Kylie Minogue",
            "Various Artists"
    );

    @Autowired
    TrackUserImportService service;

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Autowired
    TrackRepository trackRepository;

    @Autowired
    DVOriginRepository dvOriginRepository;

    @Autowired
    DVProductRepository dvProductRepository;

    @Autowired
    ArtistRepository artistRepository;

    private ArtifactType getNonMusicArtifactType() {
        return artifactTypeRepository.getWithDVAnimation();
    }
    private ArtifactType getMusicArtifactType() {
        return artifactTypeRepository.getWithDVMusic();
    }

    private void internalPrepare() {
        var artifactOne = new EntityArtifactBuilder()
                .withArtifactType(getNonMusicArtifactType())
                .withTitle("Number One")
                .withDuration(2500L)
                .build();
        artifactRepository.save(artifactOne);

        var mediaFileFirst = new EntityMediaFileBuilder()
                .withArtifact(artifactOne)
                .withDuration(2000L)
                .withName("Number One File Name First")
                .withBitrate(1000L)
                .withFormat("MKV")
                .withSize(524456L)
                .build();
        mediaFileRepository.save(mediaFileFirst);

        var mediaFileSecond = new EntityMediaFileBuilder()
                .withArtifact(artifactOne)
                .withDuration(500L)
                .withName("Number One File Name Second")
                .withBitrate(1000L)
                .withFormat("MKV")
                .withSize(84553L)
                .build();
        mediaFileRepository.save(mediaFileSecond);

        var artifactTwo = new EntityArtifactBuilder()
                .withArtifactType(getNonMusicArtifactType())
                .withTitle("Number Two")
                .withDuration(7832L)
                .build();
        artifactRepository.save(artifactTwo);

        DVOrigin dvOrigin = dvOriginRepository.save((new EntityDVOriginBuilder()).withName("Spain").build());
        dvOriginRepository.save(dvOrigin);
        PRODUCT_NAMES.forEach(name -> {
            var product = new EntityDVProductBuilder()
                    .withOrigin(dvOrigin)
                    .withArtifactType(getNonMusicArtifactType())
                    .withTitle(name)
                    .withOriginalTitle(name)
                    .build();
            dvProductRepository.save(product);
        });

        // Music

        var artifactMusicOne = new EntityArtifactBuilder()
                .withArtifactType(getMusicArtifactType())
                .withTitle("Music One")
                .withDuration(48394L)
                .build();
        artifactRepository.save(artifactMusicOne);

        var mediaFileMusicFirst = new EntityMediaFileBuilder()
                .withArtifact(artifactMusicOne)
                .withDuration(48394L)
                .withName("Music One File Name First")
                .withBitrate(6000L)
                .withFormat("MKV")
                .withSize(73495L)
                .build();
        mediaFileRepository.save(mediaFileMusicFirst);

        var mediaFileMusicSecond = new EntityMediaFileBuilder()
                .withArtifact(artifactMusicOne)
                .withDuration(3842L)
                .withName("Music One File Name Second")
                .withBitrate(8000L)
                .withFormat("MKV")
                .withSize(28384L)
                .build();
        mediaFileRepository.save(mediaFileMusicSecond);

        ARTISTS.forEach(s -> artistRepository.save(
                        new EntityArtistBuilder().withType(ArtistType.ARTIST).withName(s).build()
                ));
    }

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testPrepareShouldBeOk() {
        internalPrepare();
        assertThat(artifactRepository.getAllByArtifactType(getNonMusicArtifactType()).size()).isEqualTo(2L);
        assertThat(artifactRepository.getAllByArtifactType(getMusicArtifactType()).size()).isEqualTo(1L);
        assertThat(mediaFileRepository.findAllByArtifactId(1L).size()).isEqualTo(2L);
        assertThat(mediaFileRepository.findAllByArtifactId(2L).size()).isEqualTo(0L);
        assertThat(dvProductRepository.findAllIdTitle(getNonMusicArtifactType()).size()).isEqualTo(PRODUCT_NAMES.size());
    }

    @Test
    @Order(2)
    void testThreeTracksShouldBeOk() throws Exception {
        var chapters = new String[] {
                "CHAPTER01=00:00:00.000",
                "CHAPTER01NAME=Chapter 01",
                "CHAPTER02=00:06:28.160",
                "CHAPTER02NAME=Chapter 02",
                "CHAPTER03=00:12:26.160",
                "CHAPTER03NAME=Chapter 03",
                "CHAPTER04=00:19:30.160",
                "CHAPTER04NAME=Chapter 04"
        };

        var data = new TrackUserImportDTOBuilder()
                .withArtifact(new ArtifactDTOBuilder().withId(1L).build())
                .withDVType(new IdNameDTOBuilder().withId(2L).build())
                .withMediaFile(new MediaFileDTOBuilder().withId(1L).build())
                .withTitles(List.of("White", "Green", "Magenta"))
                .withChapters(Lists.list(chapters))
                .build();

        var result = service.executeImportTracks(data);
        assertThat(result.getRowsInserted().size()).isEqualTo(3L);

        var tmf1 = trackRepository.findByIdWithMediaFiles(1L).orElseThrow();
        assertThat(tmf1.getMediaFiles().size()).isEqualTo(1);
        assertThat(tmf1.getDuration()).isEqualTo(6 * 60 + 28);
        var tp1 = trackRepository.findByIdWithProducts(1L).orElseThrow();
        assertThat(tp1.getDvProducts().size()).isEqualTo(1);
        assertThat(tp1.getDvProducts().iterator().next().getTitle()).isEqualTo("White");

        var tmf3 = trackRepository.findByIdWithMediaFiles(3L).orElseThrow();
        assertThat(tmf3.getMediaFiles().size()).isEqualTo(1);
        assertThat(tmf3.getNum()).isEqualTo(3L);
        assertThat(tmf3.getDuration()).isEqualTo((19 - 12) * 60 + (30 - 26));
        var tp3 = trackRepository.findByIdWithProducts(3L).orElseThrow();
        assertThat(tp3.getDvProducts().size()).isEqualTo(0);
    }

    @Test
    @Order(3)
    @Sql({"/schema.sql", "/data.sql"})
    void testChaptersTitlesMismatchShouldFail() {
        internalPrepare();

        var chapters = new String[] {
                "CHAPTER01=00:00:00.000",
                "CHAPTER01NAME=Chapter 01",
                "CHAPTER02=00:06:28.160",
                "CHAPTER02NAME=Chapter 02",
                "CHAPTER03=00:12:26.160",
                "CHAPTER03NAME=Chapter 03",
                "CHAPTER04=00:19:30.160",
                "CHAPTER04NAME=Chapter 04"
        };

        var data = new TrackUserImportDTOBuilder()
                .withArtifact(new ArtifactDTOBuilder().withId(1L).build())
                .withDVType(new IdNameDTOBuilder().withId(2L).build())
                .withMediaFile(new MediaFileDTOBuilder().withId(1L).build())
                .withTitles(List.of("White", "Green", "Magenta", "Black"))
                .withChapters(Lists.list(chapters))
                .build();

        assertThatThrownBy(() -> service.executeImportTracks(data)).isInstanceOf(WrongParameterValueException.class);
    }

    @Test
    @Order(3)
    @Sql({"/schema.sql", "/data.sql"})
    void testChaptersWrongFormatShouldFail() {
        internalPrepare();

        var chapters = new String[] {
                "CHAPTER01=00:00:00.000",
                "CHAPTER01NAME=Chapter 01",
                "CHAPTER02=00:06:28.160",
                "CHAPTER02NAME=Chapter 02",
                "CHAPTER03=00:12:26.160",
                "CHAPTER03NAME=Chapter 03",
                "CHAPTER04=00:Q9:30.160",
                "CHAPTER04NAME=Chapter 04"
        };

        var data = new TrackUserImportDTOBuilder()
                .withArtifact(new ArtifactDTOBuilder().withId(1L).build())
                .withDVType(new IdNameDTOBuilder().withId(2L).build())
                .withMediaFile(new MediaFileDTOBuilder().withId(1L).build())
                .withTitles(List.of("White", "Green", "Magenta"))
                .withChapters(Lists.list(chapters))
                .build();

        assertThatThrownBy(() -> service.executeImportTracks(data)).isInstanceOf(WrongParameterValueException.class);
    }

    @Test
    @Order(10)
    @Sql({"/schema.sql", "/data.sql"})
    void testWrongArtifactTypeShouldFail() {
        internalPrepare();

        var artifact = new EntityArtifactBuilder()
                .withArtifactType(artifactTypeRepository.getWithMP3())
                .withTitle("MP3 music")
                .withDuration(48394L)
                .build();
        artifactRepository.save(artifact);

        var mediaFile = new EntityMediaFileBuilder()
                .withArtifact(artifact)
                .withDuration(48394L)
                .withName("MP3 File Name")
                .withBitrate(320L)
                .withFormat("MP3")
                .withSize(73495L)
                .build();
        mediaFileRepository.save(mediaFile);

        var dataWrongMediaFile = new TrackUserImportDTOBuilder()
                .withArtifact(new ArtifactDTOBuilder().withId(artifact.getId()).build())
                .withDVType(new IdNameDTOBuilder().withId(2L).build())
                .withMediaFile(new MediaFileDTOBuilder().withId(1L).build())
                .withTitles(List.of("White", "Green", "Magenta"))
                .build();

        var dataWrongArtifactType = new TrackUserImportDTOBuilder()
                .withArtifact(new ArtifactDTOBuilder().withId(artifact.getId()).build())
                .withDVType(new IdNameDTOBuilder().withId(2L).build())
                .withMediaFile(new MediaFileDTOBuilder().withId(mediaFile.getId()).build())
                .withTitles(List.of("White", "Green", "Magenta"))
                .build();

        assertThatThrownBy(() -> service.executeImportTracks(dataWrongMediaFile))
                .isInstanceOf(WrongParameterValueException.class)
                .hasMessageContaining("Artifact for media file does not match");

        assertThatThrownBy(() -> service.executeImportTracks(dataWrongArtifactType))
                .isInstanceOf(WrongParameterValueException.class)
                .hasMessageContaining("Unsupported artifact type");
    }

    @Test
    @Order(11)
    @Sql({"/schema.sql", "/data.sql"})
    void testMusicArtifactWrongTitlesArtistsShouldFail() {
        internalPrepare();

        var artifact = new EntityArtifactBuilder()
                .withArtifactType(artifactTypeRepository.getWithDVMusic())
                .withTitle("DV music")
                .withDuration(37654L)
                .build();
        artifactRepository.save(artifact);

        var mediaFile = new EntityMediaFileBuilder()
                .withArtifact(artifact)
                .withDuration(48394L)
                .withName("DV Music File Name")
                .withBitrate(1500L)
                .withFormat("MKV")
                .withSize(73495L)
                .build();
        mediaFileRepository.save(mediaFile);

        assertThatThrownBy(() -> service.executeImportTracks(
            new TrackUserImportDTOBuilder()
                    .withArtifact(new ArtifactDTOBuilder().withId(artifact.getId()).build())
                    .withDVType(new IdNameDTOBuilder().withId(2L).build())
                    .withMediaFile(new MediaFileDTOBuilder().withId(mediaFile.getId()).build())
                    .withTitles(List.of())
                    .build()))
                .isInstanceOf(EmptyParameterException.class)
                .hasMessageContaining("Titles")
                .hasMessage("Empty parameter: Titles");

        assertThatThrownBy(() -> service.executeImportTracks(
            new TrackUserImportDTOBuilder()
                    .withArtifact(new ArtifactDTOBuilder().withId(artifact.getId()).build())
                    .withDVType(new IdNameDTOBuilder().withId(2L).build())
                    .withMediaFile(new MediaFileDTOBuilder().withId(mediaFile.getId()).build())
                    .withTitles(List.of("Dumb", "Loser"))
                    .build()))
                .isInstanceOf(EmptyParameterException.class)
                .hasMessageContaining("Artists");

        assertThatThrownBy(() -> service.executeImportTracks(
            new TrackUserImportDTOBuilder()
                    .withArtifact(new ArtifactDTOBuilder().withId(artifact.getId()).build())
                    .withDVType(new IdNameDTOBuilder().withId(2L).build())
                    .withMediaFile(new MediaFileDTOBuilder().withId(mediaFile.getId()).build())
                    .withTitles(List.of("Dumb", "Loser"))
                    .withArtists(List.of("Clawfinger", "Joe Dassin"))
                    .build()))
                .isInstanceOf(WrongParameterValueException.class)
                .hasMessageContaining("Artists")
                .hasMessageContaining("not found")
                .hasMessageContaining("Joe Dassin")
                .hasMessageNotContaining("Clawfinger");
    }

    @Test
    @Order(20)
    @Sql({"/schema.sql", "/data.sql"})
    void testMusicArtifactOneArtistShouldBeOk() throws Exception {
        internalPrepare();

        var artist = artistRepository.findById(2L).orElseThrow();

        var artifact = new EntityArtifactBuilder()
                .withArtifactType(artifactTypeRepository.getWithDVMusic())
                .withArtist(artist)
                .withTitle("DV music")
                .withDuration(37654L)
                .build();
        artifactRepository.save(artifact);

        var mediaFile = new EntityMediaFileBuilder()
                .withArtifact(artifact)
                .withDuration(48394L)
                .withName("DV Music File Name")
                .withBitrate(1500L)
                .withFormat("MKV")
                .withSize(73495L)
                .build();
        mediaFileRepository.save(mediaFile);

        var result = service.executeImportTracks(
                new TrackUserImportDTOBuilder()
                        .withArtifact(new ArtifactDTOBuilder().withId(artifact.getId()).build())
                        .withDVType(new IdNameDTOBuilder().withId(2L).build())
                        .withMediaFile(new MediaFileDTOBuilder().withId(mediaFile.getId()).build())
                        .withTitles(List.of("Dumb", "Nigger", "Recipe for hate"))
                        .withArtists(List.of())
                        .build());
        assertThat(result.getRowsInserted().size()).isEqualTo(3);
        assertThat(result.getRowsInserted()).containsAll(List.of("Dumb", "Nigger", "Recipe for hate"));
    }

    @Test
    @Order(21)
    @Sql({"/schema.sql", "/data.sql"})
    void testMusicArtifactMultipleArtistsShouldBeOk() throws Exception {
        internalPrepare();

        var artist = artistRepository.findFirstByTypeAndName(ArtistType.ARTIST, "Various Artists").orElseThrow();

        var artifact = new EntityArtifactBuilder()
                .withArtifactType(artifactTypeRepository.getWithDVMusic())
                .withArtist(artist)
                .withTitle("Various music")
                .withDuration(84934L)
                .build();
        artifactRepository.save(artifact);

        var mediaFile = new EntityMediaFileBuilder()
                .withArtifact(artifact)
                .withDuration(48334L)
                .withName("Music Collection File Name")
                .withBitrate(1500L)
                .withFormat("MKV")
                .withSize(737495L)
                .build();
        mediaFileRepository.save(mediaFile);

        var result = service.executeImportTracks(
                new TrackUserImportDTOBuilder()
                        .withArtifact(new ArtifactDTOBuilder().withId(artifact.getId()).build())
                        .withDVType(new IdNameDTOBuilder().withId(2L).build())
                        .withMediaFile(new MediaFileDTOBuilder().withId(mediaFile.getId()).build())
                        .withTitles(List.of("Dumb", "Spinning around"))
                        .withArtists(List.of("Clawfinger", "Kylie Minogue"))
                        .build());
        assertThat(result.getRowsInserted().size()).isEqualTo(2);
        assertThat(result.getRowsInserted()).containsAll(List.of("Dumb", "Spinning around"));

        var tracks = trackRepository.findAllFlatDTOByArtifactId(artifact.getId());

        assertThat(tracks.get(0).getNum()).isEqualTo(1L);
        assertThat(tracks.get(0).getArtistName()).isEqualTo("Clawfinger");
        assertThat(tracks.get(0).getTitle()).isEqualTo("Dumb");
        assertThat(tracks.get(0).getDvTypeId()).isEqualTo(2L);
        assertThat(tracks.get(0).getMediaFileName()).isEqualTo("Music Collection File Name");
        assertThat(tracks.get(0).getDuration()).isNull();

        assertThat(tracks.get(1).getNum()).isEqualTo(2L);
        assertThat(tracks.get(1).getArtistName()).isEqualTo("Kylie Minogue");
        assertThat(tracks.get(1).getTitle()).isEqualTo("Spinning around");
        assertThat(tracks.get(1).getDvTypeId()).isEqualTo(2L);
        assertThat(tracks.get(1).getMediaFileName()).isEqualTo("Music Collection File Name");
        assertThat(tracks.get(1).getDuration()).isNull();
    }
}
