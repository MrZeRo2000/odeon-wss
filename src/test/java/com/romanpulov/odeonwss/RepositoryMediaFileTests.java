package com.romanpulov.odeonwss;

import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/schema.sql", "/data.sql"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryMediaFileTests {

    private static final Logger log = Logger.getLogger(RepositoryMediaFileTests.class.getSimpleName());

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Test
    void testCreateGet() {
        MediaFile mediaFile = new EntityMediaFileBuilder()
                .withName("AAA.mp3")
                .withFormat("MP3")
                .withSize(423L)
                .withDuration(6234L)
                .withBitrate(320L)
                .build();

        MediaFile savedMediaFile = mediaFileRepository.save(mediaFile);
        Assertions.assertNotNull(savedMediaFile.getId());
        Assertions.assertEquals(savedMediaFile.getName(), mediaFile.getName());
        Assertions.assertEquals(savedMediaFile.getFormat(), mediaFile.getFormat());
        Assertions.assertEquals(savedMediaFile.getSize(), mediaFile.getSize());
        Assertions.assertEquals(savedMediaFile.getDuration(), mediaFile.getDuration());
        Assertions.assertEquals(savedMediaFile.getBitrate(), mediaFile.getBitrate());

        log.info("Saved MediaFile:" + savedMediaFile);
    }
}
