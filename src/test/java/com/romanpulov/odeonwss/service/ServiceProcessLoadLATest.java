package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessLoadLATest {

    private static final Logger log = Logger.getLogger(ServiceProcessLoadLATest.class.getSimpleName());

    @Autowired
    private ProcessService processService;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private CompositionRepository compositionRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testNoArtists() throws Exception {
        List<ProgressDetail> progressDetail;

        // warnings - no artists exist
        processService.executeProcessor(ProcessorType.LA_LOADER, null);
        progressDetail = processService.getProcessInfo().getProgressDetails();

        Assertions.assertEquals(9, progressDetail.size());
        Assertions.assertEquals(ProcessingStatus.WARNING, processService.getProcessInfo().getProcessingStatus());

        // check processing progress
        for (int i = 1; i < 7; i++) {
            ProcessingAction pa = progressDetail.get(i).getProcessingAction();
            Assertions.assertNotNull(pa);
            Assertions.assertEquals(ProcessingActionType.ADD_ARTIST, pa.getActionType());
            Assertions.assertTrue(
                    pa.getValue().contains("Evanescence") ||
                            pa.getValue().contains("Pink Floyd") ||
                            pa.getValue().contains("Therapy") ||
                            pa.getValue().contains("Tori Amos") ||
                            pa.getValue().contains("Abigail Williams") ||
                            pa.getValue().contains("Agua De Annique") ||
                            pa.getValue().contains("Christina Aguilera")
            );
        }
    }

    void testOneMediaFilePerOneComposition(String artistName, String artifactName, int compositionCount) {
        Artist artist = artistRepository.findFirstByTypeAndName(ArtistType.ARTIST, artistName).orElseThrow();
        List<Artifact> artifacts = artifactRepository
                .getArtifactsByArtist(artist)
                .stream()
                .filter(a -> a.getTitle().equals(artifactName))
                .collect(Collectors.toList());
        Assertions.assertEquals(1, artifacts.size());

        List<MediaFile> mediaFiles = mediaFileRepository.findAllByArtifact(artifacts.get(0));
        Assertions.assertEquals(compositionCount, mediaFiles.size());

        List<Composition> compositions = compositionRepository.findAllByArtifact(artifacts.get(0));
        Assertions.assertEquals(compositionCount, compositions.size());

        Assertions.assertEquals(
                compositions.stream().map(Composition::getDuration).reduce(0L, (a, b) -> a != null && b != null ? Long.sum(a, b) : 0),
                mediaFiles.stream().map(MediaFile::getDuration).reduce(0L, Long::sum)
        )
        ;

    }

    void testOneMediaFilePerAllCompositions(String artistName, String artifactName, int compositionCount, int diskCount) {
        Artist artist = artistRepository.findFirstByTypeAndName(ArtistType.ARTIST, artistName).orElseThrow();
        List<Artifact> artifacts = artifactRepository
                .getArtifactsByArtist(artist)
                .stream()
                .filter(a -> a.getTitle().equals(artifactName))
                .collect(Collectors.toList());
        Assertions.assertEquals(1, artifacts.size());

        List<MediaFile> mediaFiles = mediaFileRepository.findAllByArtifact(artifacts.get(0));
        Assertions.assertEquals(diskCount, mediaFiles.size());

        List<Composition> compositions = compositionRepository.findAllByArtifact(artifacts.get(0));
        Assertions.assertEquals(compositionCount, compositions.size());

        Assertions.assertEquals(
                compositions.stream().map(Composition::getDuration).reduce(0L, (a, b) -> a != null && b != null ? Long.sum(a, b) : 0),
                mediaFiles.stream().map(MediaFile::getDuration).reduce(0L, Long::sum)
        );
    }


    @Test
    @Order(2)
    void testOk() throws Exception {
        List.of("Evanescence", "Pink Floyd", "Therapy", "Tori Amos", "Abigail Williams", "Agua De Annique", "Christina Aguilera")
                .forEach(s -> artistRepository.save(
                        new EntityArtistBuilder().withType(ArtistType.ARTIST).withName(s).build()
                ));

        List<ProgressDetail> progressDetail;

        processService.executeProcessor(ProcessorType.LA_LOADER, null);
        progressDetail = processService.getProcessInfo().getProgressDetails();

        Assertions.assertEquals(ProcessingStatus.SUCCESS, processService.getProcessInfo().getProcessingStatus());

        //Check Abigail Williams 2010 In The Absence Of Light
        /*
        Artist awArtist = artistRepository.findFirstByTypeAndName(ArtistType.ARTIST, "Abigail Williams").orElseThrow();
        List<Artifact> aolArtifacts = artifactRepository.getArtifactsByArtist(awArtist);
        Assertions.assertEquals(1, aolArtifacts.size());

        List<MediaFile> aolMediaFiles = mediaFileRepository.findAllByArtifact(aolArtifacts.get(0));
        Assertions.assertEquals(8, aolMediaFiles.size());

        List<Composition> aolCompositions = compositionRepository.findAllByArtifact(aolArtifacts.get(0));
        Assertions.assertEquals(8, aolCompositions.size());

         */

        testOneMediaFilePerOneComposition("Abigail Williams", "In The Absence Of Light", 8);
        testOneMediaFilePerAllCompositions("Agua De Annique", "Air", 13, 1);
        testOneMediaFilePerOneComposition("Christina Aguilera", "Back To Basics", 22);
        testOneMediaFilePerAllCompositions("Evanescence", "Origin", 11, 1);
        testOneMediaFilePerOneComposition("Evanescence", "Evanescence", 16);
        testOneMediaFilePerAllCompositions("Pink Floyd", "Delicate Sound Of Thunder", 15, 2);
        testOneMediaFilePerAllCompositions("Therapy", "Nurse", 10, 1);
        testOneMediaFilePerAllCompositions("Therapy", "Infernal Love", 11, 1);
        testOneMediaFilePerAllCompositions("Tori Amos", "Y Kant Tori Read", 10, 1);

    }
}
