package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@DisabledIf(value = "${full.tests.disabled}", loadContext = true)
public class ServiceProcessMDBImportDVMovieTest {

    @Autowired
    ProcessService service;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    CompositionRepository compositionRepository;

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testImportDVMovie() {
        service.executeProcessor(ProcessorType.DV_MOVIES_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(2)
    void testImportDVMovieRepeated() {
        int oldArtifacts = artifactRepository.getAllByArtifactType(ArtifactType.withDVMovies()).size();
        int oldCompositions = compositionRepository.getCompositionsByArtifactType(ArtifactType.withDVMovies()).size();
        int oldMediaFiles = mediaFileRepository.getMediaFilesByArtifactType(ArtifactType.withDVMovies()).size();

        Assertions.assertTrue(oldArtifacts > 0);
        Assertions.assertTrue(oldCompositions > 0);
        Assertions.assertTrue(oldMediaFiles > 0);
        Assertions.assertEquals(oldCompositions, oldArtifacts);
        Assertions.assertTrue(oldMediaFiles >= oldCompositions);

        service.executeProcessor(ProcessorType.DV_MOVIES_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());

        int newArtifacts = artifactRepository.getAllByArtifactType(ArtifactType.withDVMovies()).size();
        int newCompositions = compositionRepository.getCompositionsByArtifactType(ArtifactType.withDVMovies()).size();
        int newMediaFiles = mediaFileRepository.getMediaFilesByArtifactType(ArtifactType.withDVMovies()).size();

        Assertions.assertEquals(oldArtifacts, newArtifacts);
        Assertions.assertEquals(oldCompositions, newCompositions);
        Assertions.assertEquals(oldMediaFiles, newMediaFiles);
        Assertions.assertEquals(newCompositions, newArtifacts);
    }
}
