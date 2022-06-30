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

    @Test
    void testManyFilesManyTracks() {
        List<CueParser.CueTrack> tracks = CueParser.parseFile(Path.of("D:/temp/ok/Lossless/Abigail Williams/2010 In The Absence Of Light/In The Absence Of Light.cue"));

        Assertions.assertEquals(8, tracks.size());

        Assertions.assertEquals(new CueParser.CueTrack("01 Hope The Great Betrayal.flac", 1, "Hope The Great Betrayal", 0), tracks.get(0));
        Assertions.assertEquals(new CueParser.CueTrack("02 Final Destiny Of The Gods.flac", 2, "Final Destiny Of The Gods", 0), tracks.get(1));
        Assertions.assertEquals(new CueParser.CueTrack("03 The Mysteries That Bind The Flesh.flac", 3, "The Mysteries That Bind The Flesh", 0), tracks.get(2));
        Assertions.assertEquals(new CueParser.CueTrack("04 Infernal Divide.flac", 4, "Infernal Divide", 0), tracks.get(3));
        Assertions.assertEquals(new CueParser.CueTrack("05 In Death Comes The Great Silence.flac", 5, "In Death Comes The Great Silence", 0), tracks.get(4));
        Assertions.assertEquals(new CueParser.CueTrack("06 What Hells Await Me.flac", 6, "What Hells Await Me", 0), tracks.get(5));
        Assertions.assertEquals(new CueParser.CueTrack("07 An Echo In Our Legends.flac", 7, "An Echo In Our Legends", 0), tracks.get(6));
        Assertions.assertEquals(new CueParser.CueTrack("08 Malediction.flac", 8, "Malediction", 0), tracks.get(7));

    }
}
