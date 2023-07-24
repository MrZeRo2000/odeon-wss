package com.romanpulov.odeonwss.unit;

import com.romanpulov.odeonwss.utils.media.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import static com.romanpulov.odeonwss.unit.MediaTestData.*;
import static com.romanpulov.odeonwss.unit.MediaTestData.TEST_MP3_ME_FILE_SIZE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UnitMediaInfoMediaFileParserTest {
    private static final Logger logger = Logger.getLogger(UnitMediaInfoMediaFileParserTest.class.getSimpleName());

    MediaInfoMediaFileParser parser = new MediaInfoMediaFileParser("../MediaInfo");

    static class TestMediaInfoMediaFileParser extends MediaInfoMediaFileParser {
        public TestMediaInfoMediaFileParser() {
            super("../MediaInfo");
        }

        @Override
        protected MediaContentInfo parseOutput(String text) throws MediaInfoParsingException {
            return super.parseOutput(text);
        }
    }

    TestMediaInfoMediaFileParser testParser = new TestMediaInfoMediaFileParser();

    @Test
    void testNonMediaFile() {
        Assertions.assertThrows(MediaFileInfoException.class, () -> parser.parseMediaFile(Path.of(parser.getExecutableFileName())));
    }

    @Test
    void testMP3RoadRunner() {
        AtomicReference<MediaFileInfo> atomicInfo = new AtomicReference<>();

        Assertions.assertDoesNotThrow(
                () -> atomicInfo.set(
                        parser.parseMediaFile(Path.of(TEST_MP3_FILE_NAME))
                )
        );

        MediaFileInfo info = atomicInfo.get();

        Assertions.assertEquals(MediaType.AUDIO, info.getPrimaryMediaType());

        MediaContentInfo contentInfo = info.getMediaContentInfo();
        Assertions.assertEquals(1, contentInfo.getMediaStreams().size());

        MediaStreamInfo streamInfo = contentInfo.getMediaStreams().get(0);
        Assertions.assertEquals(MediaType.AUDIO, streamInfo.getMediaType());
        Assertions.assertEquals(TEST_MP3_FILE_BITRATE, streamInfo.getBitRate());
        Assertions.assertEquals(TEST_MP3_FILE_DURATION, streamInfo.getDuration());

        MediaFormatInfo formatInfo = contentInfo.getMediaFormatInfo();
        Assertions.assertEquals("MPEG Audio", formatInfo.getFormatName());
        Assertions.assertEquals(TEST_MP3_FILE_BITRATE, formatInfo.getBitRate());
        Assertions.assertEquals(TEST_MP3_FILE_DURATION, formatInfo.getDuration());
        Assertions.assertEquals(TEST_MP3_FILE_SIZE, formatInfo.getSize());
    }

    @Test
    void testLAHope() {
        AtomicReference<MediaFileInfo> atomicInfo = new AtomicReference<>();

        Assertions.assertDoesNotThrow(
                () -> atomicInfo.set(
                        parser.parseMediaFile(Path.of(TEST_LA_FILE_NAME))
                )
        );

        MediaFileInfo info = atomicInfo.get();

        Assertions.assertEquals(MediaType.AUDIO, info.getPrimaryMediaType());

        MediaContentInfo contentInfo = info.getMediaContentInfo();
        Assertions.assertEquals(1, contentInfo.getMediaStreams().size());

        MediaFormatInfo formatInfo = contentInfo.getMediaFormatInfo();
        Assertions.assertEquals("FLAC", formatInfo.getFormatName());
        Assertions.assertEquals(TEST_LA_FILE_BITRATE, formatInfo.getBitRate());
        Assertions.assertEquals(TEST_LA_FILE_DURATION, formatInfo.getDuration());
        Assertions.assertEquals(TEST_LA_FILE_SIZE, formatInfo.getSize());
    }

    @Test
    void testMP3MultipleStreams() {
        AtomicReference<MediaFileInfo> atomicInfo = new AtomicReference<>();

        Assertions.assertDoesNotThrow(
                () -> atomicInfo.set(
                        parser.parseMediaFile(Path.of(TEST_MP3_MS_FILE_NAME))
                )
        );

        MediaFileInfo info = atomicInfo.get();
        assertThat(info.getPrimaryMediaType()).isEqualTo(MediaType.AUDIO);

        MediaContentInfo contentInfo = info.getMediaContentInfo();
        Assertions.assertEquals(1, contentInfo.getMediaStreams().size());

        MediaFormatInfo formatInfo = contentInfo.getMediaFormatInfo();

        Assertions.assertEquals("MPEG Audio", formatInfo.getFormatName());
        Assertions.assertEquals(TEST_MP3_MS_FILE_BITRATE, formatInfo.getBitRate());
        Assertions.assertEquals(TEST_MP3_MS_FILE_DURATION, formatInfo.getDuration());
        Assertions.assertEquals(TEST_MP3_MS_FILE_SIZE, formatInfo.getSize());
    }

    @Test
    void testMP3Extra() {
        AtomicReference<MediaFileInfo> atomicInfo = new AtomicReference<>();

        Assertions.assertDoesNotThrow(
                () -> atomicInfo.set(
                        parser.parseMediaFile(Path.of(TEST_MP3_EXTRA_FILE_NAME))
                )
        );

        MediaFileInfo info = atomicInfo.get();
        assertThat(info.getPrimaryMediaType()).isEqualTo(MediaType.AUDIO);

        MediaContentInfo contentInfo = info.getMediaContentInfo();
        Assertions.assertEquals(1, contentInfo.getMediaStreams().size());

        MediaFormatInfo formatInfo = contentInfo.getMediaFormatInfo();

        Assertions.assertEquals("MPEG Audio", formatInfo.getFormatName());
        Assertions.assertEquals(TEST_MP3_EXTRA_FILE_BITRATE, formatInfo.getBitRate());
        Assertions.assertEquals(TEST_MP3_EXTRA_FILE_DURATION, formatInfo.getDuration());
        Assertions.assertEquals(TEST_MP3_EXTRA_FILE_SIZE, formatInfo.getSize());
    }

    @Test
    void testMP3MultipleExtra() {
        AtomicReference<MediaFileInfo> atomicInfo = new AtomicReference<>();

        Assertions.assertDoesNotThrow(
                () -> atomicInfo.set(
                        parser.parseMediaFile(Path.of(TEST_MP3_ME_FILE_NAME))
                )
        );

        MediaFileInfo info = atomicInfo.get();
        assertThat(info.getPrimaryMediaType()).isEqualTo(MediaType.AUDIO);

        MediaContentInfo contentInfo = info.getMediaContentInfo();
        Assertions.assertEquals(1, contentInfo.getMediaStreams().size());

        MediaFormatInfo formatInfo = contentInfo.getMediaFormatInfo();

        Assertions.assertEquals("MPEG Audio", formatInfo.getFormatName());
        Assertions.assertEquals(TEST_MP3_ME_FILE_BITRATE, formatInfo.getBitRate());
        Assertions.assertEquals(TEST_MP3_ME_FILE_DURATION, formatInfo.getDuration());
        Assertions.assertEquals(TEST_MP3_ME_FILE_SIZE, formatInfo.getSize());
    }

    @Test
    void testVOB() {
        AtomicReference<MediaFileInfo> atomicInfo = new AtomicReference<>();

        Assertions.assertDoesNotThrow(
                () -> atomicInfo.set(
                        parser.parseMediaFile(Path.of(TEST_VOB_FILE_NAME))
                )
        );

        MediaFileInfo info = atomicInfo.get();
        logger.info("Got media info:" + info);
        assertThat(info.getPrimaryMediaType()).isEqualTo(MediaType.VIDEO);

        MediaContentInfo contentInfo = info.getMediaContentInfo();
        Assertions.assertEquals(2, contentInfo.getMediaStreams().size());

        MediaFormatInfo formatInfo = contentInfo.getMediaFormatInfo();
        assertThat(formatInfo.getDuration()).isEqualTo(TEST_VOB_FILE_DURATION);
        assertThat(formatInfo.getSize()).isEqualTo(TEST_VOB_FILE_SIZE);
    }

    @Test
    void testVBRMediaInfo() throws Exception {
        Path path = Path.of("../odeon-test-data/files/vbr_mediainfo_output.json");
        String content = Files.readString(path);

        var contentInfo = testParser.parseOutput(content);
        assertThat(contentInfo.getMediaFormatInfo().getBitRate()).isGreaterThan(0);
        assertThat(contentInfo.getMediaFormatInfo().getBitRate()).isEqualTo(Math.round(37999872/1000.));
    }

}
