package com.romanpulov.odeonwss.unit;

import com.romanpulov.odeonwss.utils.media.MediaFileInfoException;
import com.romanpulov.odeonwss.utils.media.MediaInfoMediaFileParser;
import com.romanpulov.odeonwss.utils.media.MediaInfoParsingException;
import com.romanpulov.odeonwss.utils.media.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import static com.romanpulov.odeonwss.unit.MediaTestData.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UnitMediaInfoMediaFileParserTest {
    private static final Logger logger = Logger.getLogger(UnitMediaInfoMediaFileParserTest.class.getSimpleName());

    final MediaInfoMediaFileParser parser = new MediaInfoMediaFileParser("../MediaInfo");

    static class TestMediaInfoMediaFileParser extends MediaInfoMediaFileParser {
        public TestMediaInfoMediaFileParser() {
            super("../MediaInfo");
        }

        @Override
        protected MediaContentInfo parseOutput(String text) throws MediaInfoParsingException {
            return super.parseOutput(text);
        }
    }

    final TestMediaInfoMediaFileParser testParser = new TestMediaInfoMediaFileParser();

    private String readFileContent(String fileName) throws IOException {
        Path path = Path.of(UnitTestConfiguration.getFullTestFilesPath(fileName));
        return Files.readString(path);
    }

    @Test
    void testNonMediaFile() {
        Assertions.assertThrows(
                MediaFileInfoException.class,
                () -> parser.parseMediaFile(Path.of(parser.getExecutableFileName())));
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

        assertThat(info.getMediaContentInfo().getMediaStreams().getFirst()).isInstanceOf(MediaStreamAudioInfo.class);

        MediaContentInfo contentInfo = info.getMediaContentInfo();
        Assertions.assertEquals(1, contentInfo.getMediaStreams().size());

        AbstractMediaStreamInfo streamInfo = contentInfo.getMediaStreams().getFirst();
        assertThat(streamInfo).isInstanceOf(MediaStreamAudioInfo.class);
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

        assertThat(info.getMediaContentInfo().getMediaStreams().getFirst()).isInstanceOf(MediaStreamAudioInfo.class);

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
        assertThat(info.getMediaContentInfo().getMediaStreams().getFirst()).isInstanceOf(MediaStreamAudioInfo.class);

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
        assertThat(info.getMediaContentInfo().getMediaStreams().getFirst()).isInstanceOf(MediaStreamAudioInfo.class);

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
        assertThat(info.getMediaContentInfo().getMediaStreams().getFirst()).isInstanceOf(MediaStreamAudioInfo.class);

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
        assertThat(info.getMediaContentInfo().getMediaStreams().getFirst()).isInstanceOf(MediaStreamVideoInfo.class);

        MediaContentInfo contentInfo = info.getMediaContentInfo();
        Assertions.assertEquals(2, contentInfo.getMediaStreams().size());

        MediaFormatInfo formatInfo = contentInfo.getMediaFormatInfo();
        assertThat(formatInfo.getDuration()).isEqualTo(TEST_VOB_FILE_DURATION);
        assertThat(formatInfo.getSize()).isEqualTo(TEST_VOB_FILE_SIZE);
        assertThat(formatInfo.getHeight()).isEqualTo(TEST_VOB_HEIGHT);
        assertThat(formatInfo.getWidth()).isEqualTo(TEST_VOB_WIDTH);
    }

    @Test
    void testVBRMediaInfo() throws Exception {
        String content = readFileContent("mediainfo_output_vbr.json");

        var contentInfo = testParser.parseOutput(content);
        assertThat(contentInfo.getMediaFormatInfo().getBitRate()).isGreaterThan(0);
        assertThat(contentInfo.getMediaFormatInfo().getBitRate()).isEqualTo(Math.round(37999872/1000.));
    }

    @Test
    void testNBRMediaInfo() throws Exception {
        String content = readFileContent("mediainfo_output_nbr.json");

        var contentInfo = testParser.parseOutput(content);
        assertThat(contentInfo.getMediaFormatInfo().getBitRate()).isGreaterThan(0);
        assertThat(contentInfo.getMediaFormatInfo().getBitRate()).isEqualTo(Math.round(1300000/1000.));
        assertThat(contentInfo.getChapters().size()).isEqualTo(0);
    }

    @Test
    void testNoVideoBitrate() throws Exception {
        String content = readFileContent("mediainfo_output_nbr_mkv.json");

        var contentInfo = testParser.parseOutput(content);
        assertThat(contentInfo.getMediaFormatInfo().getBitRate()).isGreaterThan(0);
        assertThat(contentInfo.getMediaFormatInfo().getBitRate()).isEqualTo(796L);

        assertThat(contentInfo.getMediaStreams().getFirst()).isInstanceOf(MediaStreamVideoInfo.class);
        MediaStreamVideoInfo videoStream = (MediaStreamVideoInfo)contentInfo.getMediaStreams().getFirst();
        assertThat(videoStream.getWidth()).isEqualTo(630);
        assertThat(videoStream.getHeight()).isEqualTo(480);
    }

    @Test
    void testVideoWithChapters() throws Exception {
        String content = readFileContent("mediainfo_output_1280_720_with_chapters.json");

        var contentInfo = testParser.parseOutput(content);
        assertThat(contentInfo.getChapters().size()).isEqualTo(3);
        assertThat(contentInfo.getChapters().get(0)).isEqualTo(LocalTime.of(0, 0, 4));
        assertThat(contentInfo.getChapters().get(1)).isEqualTo(LocalTime.of(0, 26, 0));
        assertThat(contentInfo.getChapters().get(2)).isEqualTo(LocalTime.of(1, 35, 19));

        assertThat(contentInfo.getMediaStreams().getFirst()).isInstanceOf(MediaStreamVideoInfo.class);
        MediaStreamVideoInfo videoStream = (MediaStreamVideoInfo)contentInfo.getMediaStreams().getFirst();
        assertThat(videoStream.getWidth()).isEqualTo(1280);
        assertThat(videoStream.getHeight()).isEqualTo(720);
    }

    @Test
    void testVideoWithMultipleStreams() throws Exception {
        String content = readFileContent("mediainfo_output_multiple_streams_bitrate.json");

        var contentInfo = testParser.parseOutput(content);

        assertThat(contentInfo.getMediaFormatInfo().getBitRate()).isEqualTo(7000L);
        assertThat(contentInfo.getMediaFormatInfo().getSize()).isEqualTo(8608304859L);
        assertThat(contentInfo.getMediaFormatInfo().getDuration()).isEqualTo(8001L);
        assertThat(contentInfo.getMediaStreams().getFirst()).isInstanceOf(MediaStreamVideoInfo.class);
        MediaStreamVideoInfo videoStream = (MediaStreamVideoInfo)contentInfo.getMediaStreams().getFirst();
        assertThat(videoStream.getWidth()).isEqualTo(1280);
        assertThat(videoStream.getHeight()).isEqualTo(540);
    }

}
