package com.romanpulov.odeonwss;

import com.romanpulov.odeonwss.utils.media.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class UnitFFMPEGMediaFileParserTest {
    FFMPEGMediaFileParser fp = new FFMPEGMediaFileParser("D:/prj/ffmpeg/ffprobe.exe");

    @Test
    void testNonMediaFile() {
        Assertions.assertThrows(MediaFileInfoException.class, () -> {
            fp.parseMediaFile(Path.of(fp.getExecutableFileName()));
        });
    }

    @Test
    void testMP3RoadRunner() {
        AtomicReference<MediaFileInfo> atomicInfo = new AtomicReference<>();

        Assertions.assertDoesNotThrow(() -> {
            atomicInfo.set(fp.parseMediaFile(Path.of("D:/temp/ok/MP3 Music/Aerosmith/2004 Honkin'On Bobo/01 - Road Runner.mp3")));
        });

        MediaFileInfo info = atomicInfo.get();

        Assertions.assertEquals(MediaType.AUDIO, info.getPrimaryMediaType());

        MediaContentInfo contentInfo = info.getMediaContentInfo();
        Assertions.assertEquals(1, contentInfo.getMediaStreams().size());

        MediaStreamInfo streamInfo = contentInfo.getMediaStreams().get(0);
        Assertions.assertEquals(MediaType.AUDIO, streamInfo.getMediaType());
        Assertions.assertEquals(320, streamInfo.getBitRate());
        Assertions.assertEquals(226, streamInfo.getDuration());

        MediaFormatInfo formatInfo = contentInfo.getMediaFormatInfo();
        Assertions.assertEquals("mp3", formatInfo.getFormatName());
        Assertions.assertEquals(320, formatInfo.getBitRate());
        Assertions.assertEquals(226, formatInfo.getDuration());
        Assertions.assertEquals(9027961, formatInfo.getSize());
    }
}
