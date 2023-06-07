package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessMDBImportLAUpdateAttributesTest {
    private static final Logger log = Logger.getLogger(ServiceProcessMDBImportLAUpdateAttributesTest.class.getSimpleName());

    private static final String[] ARTIST_LIST = {
            "Evanescence",
            "Pink Floyd",
            "Therapy",
            "Tori Amos",
            "Abigail Williams",
            "Agua De Annique",
            "Christina Aguilera",
            "The Sisters Of Mercy"
    };


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
        List.of(ARTIST_LIST)
                .forEach(s -> artistRepository.save(
                        new EntityArtistBuilder().withType(ArtistType.ARTIST).withName(s).build()
                ));

        service.executeProcessor(ProcessorType.LA_LOADER, null);

        ProcessInfo pi = service.getProcessInfo();
        List<ProcessDetail> processDetails = pi.getProcessDetails();
        AssertionsForClassTypes.assertThat(pi.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
    }

    @Test
    @Order(2)
    void testUpdateOk() {
        Assertions.assertDoesNotThrow(() -> service.executeProcessor(ProcessorType.LA_UPDATE_ATTRIBUTES_IMPORTER));

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

        var absenceArtifact = artifactRepository.findFirstByArtifactTypeAndArtistNameAndTitleAndYear(
                artifactTypeRepository.getWithLA(),
                "Abigail Williams",
                "In The Absence Of Light",
                2010L
        ).orElseThrow();
        assertThat(absenceArtifact.getInsertDateTime()).isEqualTo(LocalDateTime.of(2020, 5, 30, 13, 25, 24));

        var b2bArtifact = artifactRepository.findFirstByArtifactTypeAndArtistNameAndTitleAndYear(
                artifactTypeRepository.getWithLA(),
                "Christina Aguilera",
                "Back To Basics",
                2006L
        ).orElseThrow();
        assertThat(b2bArtifact.getInsertDateTime()).isEqualTo(LocalDateTime.of(2018, 3, 1, 22, 39, 4));

        var evArtifact = artifactRepository.findFirstByArtifactTypeAndArtistNameAndTitleAndYear(
                artifactTypeRepository.getWithLA(),
                "Evanescence",
                "Evanescence",
                2011L
        ).orElseThrow();
        assertThat(evArtifact.getInsertDateTime()).isEqualTo(LocalDateTime.of(2011, 10, 31, 21, 12, 10));

        var anacondaArtifact = artifactRepository.findFirstByArtifactTypeAndArtistNameAndTitleAndYear(
                artifactTypeRepository.getWithLA(),
                "The Sisters Of Mercy",
                "Anaconda 7 Inch Single",
                1983L
        ).orElseThrow();
        assertThat(anacondaArtifact.getInsertDateTime()).isEqualTo(LocalDateTime.of(2012, 3, 25, 21, 40, 38));
    }
}
