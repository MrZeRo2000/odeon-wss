package com.romanpulov.odeonwss.unit;

import com.romanpulov.odeonwss.service.processor.parser.CueParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class UnitCueParserTest {
    Logger logger = Logger.getLogger(getClass().getSimpleName());

    @Test
    void testOneFileManyTracks() {
        List<CueParser.CueTrack> tracks = CueParser.parseFile(Path.of("../odeon-test-data/ok/Lossless/Tori Amos/1988 Y Kant Tori Read/Tori Amos - Y Kant Tori Read.wv.cue"));

        Assertions.assertEquals(10, tracks.size());

        Assertions.assertEquals(new CueParser.CueTrack("Tori Amos - Y Kant Tori Read.wv", 10, "Etienne Trilogy (The Highlands - Etienne - Skyeboat Song)", 61), tracks.get(9));
    }

    @Test
    void testOneFileManyTracksMinutes() {
        List<CueParser.CueTrack> tracks = CueParser.parseFile(Path.of("../odeon-test-data/ok/Lossless/Agua De Annique/2007 Air/air.flac.cue"));

        Assertions.assertEquals(13, tracks.size());

        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 1, "Beautiful One", 0), tracks.get(0));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 2, "Witnesses", 1), tracks.get(1));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 3, "Yalin", 2), tracks.get(2));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 4, "Day After Yesterday", 3), tracks.get(3));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 5, "My Girl", 4), tracks.get(4));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 6, "Take Care of Me", 5), tracks.get(5));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 7, "Ice Water", 6), tracks.get(6));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 8, "You Are Nice", 7), tracks.get(7));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 9, "Trail of Grief", 8), tracks.get(8));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 10, "Come Wander with Me", 9), tracks.get(9));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 11, "Sunken Soldiers Ball", 10), tracks.get(10));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 12, "Lost and Found", 11), tracks.get(11));
        Assertions.assertEquals(new CueParser.CueTrack("air.flac", 13, "Asleep", 15), tracks.get(12));
    }

    @Test
    void testManyFilesManyTracks() {
        List<CueParser.CueTrack> tracks = CueParser.parseFile(Path.of("../odeon-test-data/ok/Lossless/Abigail Williams/2010 In The Absence Of Light/In The Absence Of Light.cue"));

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

    @Test
    void testCUEWithoutNames() {
        List<CueParser.CueTrack> tracks = CueParser.parseFile(Path.of("../odeon-test-data/files/Celestial Completion.cue"));

        assertThat(tracks.size()).isEqualTo(11);
        assertThat(tracks.get(0)).isEqualTo(new CueParser.CueTrack("01 - The Resonant Frequency of Flesh.flac", 1, "The Resonant Frequency of Flesh", 0));
        assertThat(tracks.get(1)).isEqualTo(new CueParser.CueTrack("02 - The Magnetic Sky.ape", 2, "The Magnetic Sky", 0));
        assertThat(tracks.get(2)).isEqualTo(new CueParser.CueTrack("03 - Internal Illumination.m4a", 3, "Internal Illumination", 0));
        assertThat(tracks.get(3)).isEqualTo(new CueParser.CueTrack("04 - Path of the Beam.flac", 4, "Path of the Beam", 0));
        assertThat(tracks.get(4)).isEqualTo(new CueParser.CueTrack("05 - Music of the Spheres- Requiem Aeternam I.flac", 5, "Music of the Spheres- Requiem Aeternam I", 0));
        assertThat(tracks.get(5)).isEqualTo(new CueParser.CueTrack("06 - Elemental Wrath- Requiem Aeternam II.flac", 6, "Elemental Wrath- Requiem Aeternam II", 0));
        assertThat(tracks.get(6)).isEqualTo(new CueParser.CueTrack("07 - Xenosynthesis- Requiem Aeternam III.flac", 7, "Xenosynthesis- Requiem Aeternam III", 0));
        assertThat(tracks.get(7)).isEqualTo(new CueParser.CueTrack("08 - Invisible Creature.flac", 8, "Invisible Creature", 0));
        assertThat(tracks.get(8)).isEqualTo(new CueParser.CueTrack("09 - Cardiac Rebellion.flac", 9, "Cardiac Rebellion", 0));
        assertThat(tracks.get(9)).isEqualTo(new CueParser.CueTrack("10 - Reflect,Refract.flac", 10, "Reflect,Refract", 0));
        assertThat(tracks.get(10)).isEqualTo(new CueParser.CueTrack("11 - Breathing Light.flac", 11, "Breathing Light", 0));
    }

    @Test
    @Disabled
    void testCUEWithUnicode() {
        assertThatThrownBy(() -> CueParser.parseFile(Path.of("../odeon-test-data/files/Happier Than Ever.cue"))).isInstanceOf(UncheckedIOException.class);
    }
}
