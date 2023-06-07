package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessMDBImportMP3UpdateAttributesTest {
    private static final Logger log = Logger.getLogger(ServiceProcessMDBImportMP3UpdateAttributesTest.class.getSimpleName());

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    ProcessService service;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testLoadOk() {
        Arrays.asList("Aerosmith", "Kosheen", "Various Artists").forEach(s ->
                artistRepository.save(
                        new EntityArtistBuilder()
                                .withType(ArtistType.ARTIST)
                                .withName(s)
                                .build()
                ));

        log.info("Created artists");
        Assertions.assertDoesNotThrow(() -> service.executeProcessor(ProcessorType.MP3_LOADER));

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
    }

    @Test
    @Order(2)
    void testUpdateOk() {
        Assertions.assertDoesNotThrow(() -> service.executeProcessor(ProcessorType.MP3_UPDATE_ATTRIBUTES_IMPORTER));

        ProcessInfo pi = service.getProcessInfo();
        assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        List<ProcessDetail> processDetails = pi.getProcessDetails();

        assertThat(processDetails.get(1)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Artifacts updated"),
                        ProcessingStatus.INFO,
                        4,
                        null)
        );

        var honkinArtifact = artifactRepository.findFirstByArtifactTypeAndArtistNameAndTitleAndYear(
                artifactTypeRepository.getWithMP3(),
                "Aerosmith",
                "Honkin'On Bobo",
                2004L
        ).orElseThrow();
        assertThat(honkinArtifact.getInsertDateTime()).isEqualTo(LocalDateTime.of(2010, 11, 27, 18, 12, 28));

        var kokopelliArtifact = artifactRepository.findFirstByArtifactTypeAndArtistNameAndTitleAndYear(
                artifactTypeRepository.getWithMP3(),
                "Kosheen",
                "Kokopelli",
                2004L
        ).orElseThrow();
        assertThat(kokopelliArtifact.getInsertDateTime()).isEqualTo(LocalDateTime.of(2010, 11, 27, 17, 21, 28));

        var damageArtifact = artifactRepository.findFirstByArtifactTypeAndArtistNameAndTitleAndYear(
                artifactTypeRepository.getWithMP3(),
                "Kosheen",
                "Damage",
                2007L
        ).orElseThrow();
        assertThat(damageArtifact.getInsertDateTime()).isEqualTo(LocalDateTime.of(2010, 11, 29, 20, 27, 38));

        var rnrArtifact = artifactRepository.findFirstByArtifactTypeAndArtistNameAndTitleAndYear(
                artifactTypeRepository.getWithMP3(),
                "Various Artists",
                "Rock N' Roll Fantastic",
                2000L
        ).orElseThrow();
        assertThat(rnrArtifact.getInsertDateTime()).isEqualTo(LocalDateTime.of(2010, 11, 27, 18, 20, 36));
    }

}
