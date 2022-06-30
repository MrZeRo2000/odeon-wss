package com.romanpulov.odeonwss.unit;

import com.romanpulov.odeonwss.service.processor.CueParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

public class UnitCueParserTest {

    @Test
    void testOneFileManyTracks() {
        List<CueParser.CueTrack> tracks = CueParser.parseFile(Path.of("D:/temp/ok/Lossless/Agua De Annique/2007 Air/air.flac.cue"));

        Assertions.assertEquals(13, tracks.size());

        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 1, "Beautiful One", 0), tracks.get(0));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 2, "Witnesses", 283), tracks.get(1));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 3, "Yalin", 539), tracks.get(2));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 4, "Day After Yesterday", 743), tracks.get(3));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 5, "My Girl", 965), tracks.get(4));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 6, "Take Care of Me", 1219), tracks.get(5));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 7, "Ice Water", 1382), tracks.get(6));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 8, "You Are Nice", 1632), tracks.get(7));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 9, "Trail of Grief", 1828), tracks.get(8));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 10, "Come Wander with Me", 2102), tracks.get(9));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 11, "Sunken Soldiers Ball", 2314), tracks.get(10));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 12, "Lost and Found", 2621), tracks.get(11));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 13, "Asleep", 2937), tracks.get(12));
    }
}
