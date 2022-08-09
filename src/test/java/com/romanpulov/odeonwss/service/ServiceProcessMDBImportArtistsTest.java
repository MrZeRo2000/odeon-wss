package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistDetail;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtistCategoryRepository;
import com.romanpulov.odeonwss.repository.ArtistDetailRepository;
import com.romanpulov.odeonwss.repository.ArtistLyricsRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Disabled
public class ServiceProcessMDBImportArtistsTest {

    private static final Logger log = Logger.getLogger(ServiceProcessMDBImportArtistsTest.class.getSimpleName());

    @Autowired
    ProcessService service;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtistDetailRepository artistDetailRepository;

    @Autowired
    ArtistCategoryRepository artistCategoryRepository;

    @Autowired
    ArtistLyricsRepository artistLyricsRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testLoad() {
        service.executeProcessor(ProcessorType.ARTISTS_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());
        Assertions.assertTrue(service.getProcessInfo().getProgressDetails().stream().anyMatch(p -> p.getInfo().contains("Artists imported")));
        Assertions.assertTrue(service.getProcessInfo().getProgressDetails().stream().anyMatch(p -> p.getInfo().contains("Artist details imported")));
        Assertions.assertTrue(service.getProcessInfo().getProgressDetails().stream().anyMatch(p -> p.getInfo().contains("Artist categories imported")));
        Assertions.assertTrue(service.getProcessInfo().getProgressDetails().stream().anyMatch(p -> p.getInfo().contains("Artist lyrics imported")));
        log.info("Processing info: " + service.getProcessInfo());
    }

    @Test
    @Order(2)
    void testValidate() {
        Assertions.assertEquals(0, artistRepository.getAllByType(ArtistType.CLASSICS).size());

        List<Artist> artists = artistRepository.getAllByType(ArtistType.ARTIST);
        Assertions.assertTrue(artists.size() > 0);
        Assertions.assertEquals(artists.size(), artists.stream().map(Artist::getMigrationId).collect(Collectors.toSet()).size());
    }

    @Test
    @Order(3)
    void testRunSecondTime() {
        long artistCount = artistRepository.getAllByType(ArtistType.ARTIST).size();
        long artistDetailCount = StreamSupport.stream(artistDetailRepository.findAll().spliterator(), false).count();
        long artistCategoryCount = StreamSupport.stream(artistCategoryRepository.findAll().spliterator(), false).count();
        long artistLyricsCount = StreamSupport.stream(artistLyricsRepository.findAll().spliterator(), false).count();

        service.executeProcessor(ProcessorType.CLASSICS_IMPORTER);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, service.getProcessInfo().getProcessingStatus());

        Assertions.assertEquals(artistCount, artistRepository.getAllByType(ArtistType.ARTIST).size());
        Assertions.assertEquals(artistDetailCount, StreamSupport.stream(artistDetailRepository.findAll().spliterator(), false).count());
        Assertions.assertEquals(artistCategoryCount, StreamSupport.stream(artistCategoryRepository.findAll().spliterator(), false).count());
        Assertions.assertEquals(artistLyricsCount, StreamSupport.stream(artistLyricsRepository.findAll().spliterator(), false).count());
    }

}
