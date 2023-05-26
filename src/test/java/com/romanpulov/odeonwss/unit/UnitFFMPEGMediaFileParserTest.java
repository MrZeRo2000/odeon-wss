package com.romanpulov.odeonwss.unit;

import com.romanpulov.odeonwss.utils.media.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UnitFFMPEGMediaFileParserTest {
    FFMPEGMediaFileParser fp = new FFMPEGMediaFileParser("../ffmpeg/");

    static final String TEST_MP3_FILE_NAME = "../odeon-test-data/ok/MP3 Music/Aerosmith/2004 Honkin'On Bobo/01 - Road Runner.mp3";
    static final long TEST_MP3_FILE_DURATION = 226L;
    static final long TEST_MP3_FILE_SIZE = 9027961;
    static final long TEST_MP3_FILE_BITRATE = 320;

    static final String TEST_LA_FILE_NAME= "../odeon-test-data/ok/Lossless/Abigail Williams/2010 In The Absence Of Light/01 Hope The Great Betrayal.flac";
    static final long TEST_LA_FILE_DURATION = 405;
    static final long TEST_LA_FILE_SIZE = 51873290L;
    static final long TEST_LA_FILE_BITRATE = 1025L;

    @Test
    void testNonMediaFile() {
        Assertions.assertThrows(MediaFileInfoException.class, () -> fp.parseMediaFile(Path.of(fp.getExecutableFileName())));
    }

    @Test
    void testMP3RoadRunner() {
        AtomicReference<MediaFileInfo> atomicInfo = new AtomicReference<>();

        Assertions.assertDoesNotThrow(
                () -> atomicInfo.set(
                        fp.parseMediaFile(Path.of(TEST_MP3_FILE_NAME))
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
                        fp.parseMediaFile(Path.of(TEST_LA_FILE_NAME))
                )
        );

        MediaFileInfo info = atomicInfo.get();

        Assertions.assertEquals(MediaType.AUDIO, info.getPrimaryMediaType());

        MediaContentInfo contentInfo = info.getMediaContentInfo();
        Assertions.assertEquals(1, contentInfo.getMediaStreams().size());

        MediaFormatInfo formatInfo = contentInfo.getMediaFormatInfo();
        Assertions.assertEquals("flac", formatInfo.getFormatName());
        Assertions.assertEquals(TEST_LA_FILE_BITRATE, formatInfo.getBitRate());
        Assertions.assertEquals(TEST_LA_FILE_DURATION, formatInfo.getDuration());
        Assertions.assertEquals(TEST_LA_FILE_SIZE, formatInfo.getSize());
    }

    @Test
    void testMP3MultipleStreams() {
        AtomicReference<MediaFileInfo> atomicInfo = new AtomicReference<>();

        Assertions.assertDoesNotThrow(
                () -> atomicInfo.set(
                        fp.parseMediaFile(Path.of("../odeon-test-data/files/07 - Пылает За Окном Звезда.mp3"))
                )
        );

        MediaFileInfo info = atomicInfo.get();
        assertThat(info.getPrimaryMediaType()).isEqualTo(MediaType.AUDIO);
    }
}
