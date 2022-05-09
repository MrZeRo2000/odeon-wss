package com.romanpulov.odeonwss;

import com.romanpulov.odeonwss.utils.media.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class UnitFFMPEGMediaFileParserTest {
    FFMPEGMediaFileParser fp = new FFMPEGMediaFileParser("D:/prj/ffmpeg/");

    static final String TEST_FILE_NAME = "D:/temp/ok/MP3 Music/Aerosmith/2004 Honkin'On Bobo/01 - Road Runner.mp3";
    static final long TEST_FILE_DURATION = 226L;
    static final long TEST_FILE_SIZE = 9027961;
    static final long TEST_FILE_BITRATE = 320;

    @Test
    void testNonMediaFile() {
        Assertions.assertThrows(MediaFileInfoException.class, () -> fp.parseMediaFile(Path.of(fp.getExecutableFileName())));
    }

    @Test
    void testMP3RoadRunner() {
        AtomicReference<MediaFileInfo> atomicInfo = new AtomicReference<>();

        Assertions.assertDoesNotThrow(
                () -> atomicInfo.set(
                        fp.parseMediaFile(Path.of(TEST_FILE_NAME))
                )
        );

        MediaFileInfo info = atomicInfo.get();

        Assertions.assertEquals(MediaType.AUDIO, info.getPrimaryMediaType());

        MediaContentInfo contentInfo = info.getMediaContentInfo();
        Assertions.assertEquals(1, contentInfo.getMediaStreams().size());

        MediaStreamInfo streamInfo = contentInfo.getMediaStreams().get(0);
        Assertions.assertEquals(MediaType.AUDIO, streamInfo.getMediaType());
        Assertions.assertEquals(TEST_FILE_BITRATE, streamInfo.getBitRate());
        Assertions.assertEquals(TEST_FILE_DURATION, streamInfo.getDuration());

        MediaFormatInfo formatInfo = contentInfo.getMediaFormatInfo();
        Assertions.assertEquals("mp3", formatInfo.getFormatName());
        Assertions.assertEquals(TEST_FILE_BITRATE, formatInfo.getBitRate());
        Assertions.assertEquals(TEST_FILE_DURATION, formatInfo.getDuration());
        Assertions.assertEquals(TEST_FILE_SIZE, formatInfo.getSize());
    }
}
