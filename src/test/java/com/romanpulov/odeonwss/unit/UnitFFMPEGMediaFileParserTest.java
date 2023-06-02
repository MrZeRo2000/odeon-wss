package com.romanpulov.odeonwss.unit;

import com.romanpulov.odeonwss.utils.media.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static com.romanpulov.odeonwss.unit.MediaTestData.*;

public class UnitFFMPEGMediaFileParserTest {
    FFMPEGMediaFileParser parser = new FFMPEGMediaFileParser("../ffmpeg/");

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
        Assertions.assertEquals("mp3", formatInfo.getFormatName());
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
        Assertions.assertEquals("flac", formatInfo.getFormatName());
        Assertions.assertEquals(TEST_LA_FILE_BITRATE_FFMPEG, formatInfo.getBitRate());
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
    }
}
