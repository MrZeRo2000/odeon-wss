package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.DisabledIf;

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

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testImportDVMovie() {
        service.executeProcessor(ProcessorType.DV_MOVIE_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(2)
    void testImportDVMovieRepeated() {
        int oldArtifacts = artifactRepository.getAllByArtifactType(ArtifactType.withDVMovies()).size();
        int oldCompositions = compositionRepository.getCompositionsByArtifactType(ArtifactType.withDVMovies()).size();

        Assertions.assertTrue(oldArtifacts > 0);
        Assertions.assertTrue(oldCompositions > 0);
        Assertions.assertEquals(oldCompositions, oldArtifacts);

        service.executeProcessor(ProcessorType.DV_MOVIE_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());

        int newArtifacts = artifactRepository.getAllByArtifactType(ArtifactType.withDVMovies()).size();
        int newCompositions = compositionRepository.getCompositionsByArtifactType(ArtifactType.withDVMovies()).size();

        Assertions.assertEquals(oldArtifacts, newArtifacts);
        Assertions.assertEquals(oldCompositions, newCompositions);
        Assertions.assertEquals(newCompositions, newArtifacts);
    }
}
