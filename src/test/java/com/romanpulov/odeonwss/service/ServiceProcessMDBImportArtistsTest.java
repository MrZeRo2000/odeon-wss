package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtistCategoryRepository;
import com.romanpulov.odeonwss.repository.ArtistDetailRepository;
import com.romanpulov.odeonwss.repository.ArtistLyricsRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    @Autowired
    AppConfiguration appConfiguration;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testLoad() {
        service.executeProcessor(ProcessorType.ARTISTS_IMPORTER);
        ProcessInfo processInfo = service.getProcessInfo();
        List<ProcessDetail> processDetails = processInfo.getProcessDetails();

        assertThat(processInfo.getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);
        log.info("Artist Importer Processing info: " + service.getProcessInfo());

        assertThat(processDetails.get(0)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Started Artists importer"),
                        ProcessingStatus.INFO,
                        null,
                        null)
        );

        assertThat(processDetails.get(1).getInfo().getMessage()).isEqualTo("Artists imported");
        assertThat(processDetails.get(1).getRows()).isGreaterThan(0);

        assertThat(processDetails.get(2).getInfo().getMessage()).isEqualTo("Artist details imported");
        assertThat(processDetails.get(2).getRows()).isGreaterThan(0);

        assertThat(processDetails.get(3).getInfo().getMessage()).isEqualTo("Artist categories imported");
        assertThat(processDetails.get(3).getRows()).isGreaterThan(0);

        assertThat(processDetails.get(4).getInfo().getMessage()).isEqualTo("Artist lyrics imported");
        assertThat(processDetails.get(4).getRows()).isGreaterThan(0);

        assertThat(processDetails.get(5)).isEqualTo(
                new ProcessDetail(
                        ProcessDetailInfo.fromMessage("Task status"),
                        ProcessingStatus.SUCCESS,
                        null,
                        null)
        );
    }

    @Test
    @Order(2)
    void testValidate() {
        assertThat(artistRepository.getAllByType(ArtistType.CLASSICS).size()).isEqualTo(0);

        List<Artist> artists = artistRepository.getAllByType(ArtistType.ARTIST);
        assertThat(artists.size()).isGreaterThan(0);
        assertThat(artists.size()).isEqualTo(artists.stream().map(Artist::getMigrationId).collect(Collectors.toSet()).size());
    }

    @Test
    @Order(3)
    void testRunSecondTime() {
        long artistCount = StreamSupport.stream(artistRepository.findAll().spliterator(), false).count();
        long artistDetailCount = StreamSupport.stream(artistDetailRepository.findAll().spliterator(), false).count();
        long artistCategoryCount = StreamSupport.stream(artistCategoryRepository.findAll().spliterator(), false).count();
        long artistLyricsCount = StreamSupport.stream(artistLyricsRepository.findAll().spliterator(), false).count();

        service.executeProcessor(ProcessorType.ARTISTS_IMPORTER);
        assertThat(service.getProcessInfo().getProcessingStatus()).isEqualTo(ProcessingStatus.SUCCESS);

        assertThat(StreamSupport.stream(artistRepository.findAll().spliterator(), false).count()).isEqualTo(artistCount);
        assertThat(StreamSupport.stream(artistDetailRepository.findAll().spliterator(), false).count()).isEqualTo(artistDetailCount);
        assertThat(StreamSupport.stream(artistCategoryRepository.findAll().spliterator(), false).count()).isEqualTo(artistCategoryCount);
        assertThat(StreamSupport.stream(artistLyricsRepository.findAll().spliterator(), false).count()).isEqualTo(artistLyricsCount);
    }
}
