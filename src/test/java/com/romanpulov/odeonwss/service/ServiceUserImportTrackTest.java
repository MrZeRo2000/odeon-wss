package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.dtobuilder.*;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtifactBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVOriginBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityDVProductBuilder;
import com.romanpulov.odeonwss.builder.entitybuilder.EntityMediaFileBuilder;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.DVOrigin;
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

    private ArtifactType getArtifactType() {
        return artifactTypeRepository.getWithDVAnimation();
    }

    private void internalPrepare() {
        var artifactOne = new EntityArtifactBuilder()
                .withArtifactType(getArtifactType())
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
                .withArtifactType(getArtifactType())
                .withTitle("Number Two")
                .withDuration(7832L)
                .build();
        artifactRepository.save(artifactTwo);

        DVOrigin dvOrigin = dvOriginRepository.save((new EntityDVOriginBuilder()).withName("Spain").build());
        dvOriginRepository.save(dvOrigin);
        PRODUCT_NAMES.forEach(name -> {
            var product = new EntityDVProductBuilder()
                    .withOrigin(dvOrigin)
                    .withArtifactType(getArtifactType())
                    .withTitle(name)
                    .withOriginalTitle(name)
                    .build();
            dvProductRepository.save(product);
        });
    }

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testPrepareShouldBeOk() {
        internalPrepare();
        assertThat(artifactRepository.getAllByArtifactType(getArtifactType()).size()).isEqualTo(2L);
        assertThat(mediaFileRepository.findAllByArtifactId(1L).size()).isEqualTo(2L);
        assertThat(mediaFileRepository.findAllByArtifactId(2L).size()).isEqualTo(0L);
        assertThat(dvProductRepository.findAllIdTitle(getArtifactType()).size()).isEqualTo(PRODUCT_NAMES.size());
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
}
