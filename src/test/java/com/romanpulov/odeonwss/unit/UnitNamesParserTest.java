package com.romanpulov.odeonwss.unit;


import com.romanpulov.odeonwss.service.processor.parser.NamesParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UnitNamesParserTest {

    private static final Logger log = Logger.getLogger(UnitNamesParserTest.class.getSimpleName());

    @Test
    void testMusicArtifactTitleValidator() {
        Assertions.assertTrue(NamesParser.validateMusicArtifactTitle("1980 Title"));
        Assertions.assertTrue(NamesParser.validateMusicArtifactTitle("2022 Title long title"));

        Assertions.assertFalse(NamesParser.validateMusicArtifactTitle("Stuff"));
        Assertions.assertFalse(NamesParser.validateMusicArtifactTitle("5980 Title"));
        Assertions.assertFalse(NamesParser.validateMusicArtifactTitle("2022 "));
        Assertions.assertFalse(NamesParser.validateMusicArtifactTitle("2122 Title"));
        Assertions.assertFalse(NamesParser.validateMusicArtifactTitle("2022  Title"));
        Assertions.assertFalse(NamesParser.validateMusicArtifactTitle(" 2022 Title"));
    }

    @Test
    void testMusicArtifactTitleParser() {
        NamesParser.YearTitle yt;

        yt = NamesParser.parseMusicArtifactTitle("1980 Title");
        Assertions.assertNotNull(yt);
        Assertions.assertEquals(1980, yt.year());
        Assertions.assertEquals("Title", yt.title());

        yt = NamesParser.parseMusicArtifactTitle("198r Title");
        Assertions.assertNull(yt);

        yt = NamesParser.parseMusicArtifactTitle("2022 Title can be long");
        Assertions.assertNotNull(yt);
        Assertions.assertEquals(2022, yt.year());
        Assertions.assertEquals("Title can be long", yt.title());
    }

    @Test
    void testMusicTrackValidator() {
        Assertions.assertTrue(NamesParser.validateMusicTrack("02 - Title.mp3"));
        Assertions.assertTrue(NamesParser.validateMusicTrack("21 - Title can be long.flac"));

        Assertions.assertFalse(NamesParser.validateMusicTrack("1980 Title"));
        Assertions.assertFalse(NamesParser.validateMusicTrack(" 02 - Title"));
        Assertions.assertFalse(NamesParser.validateMusicTrack("02  - Title"));
        Assertions.assertFalse(NamesParser.validateMusicTrack("02- Title"));
        Assertions.assertFalse(NamesParser.validateMusicTrack("02 -Title"));
        Assertions.assertFalse(NamesParser.validateMusicTrack("02 -  Title"));
        Assertions.assertFalse(NamesParser.validateMusicTrack("102 -  Title"));
        Assertions.assertFalse(NamesParser.validateMusicTrack("d2 - Title"));
        Assertions.assertFalse(NamesParser.validateMusicTrack("02 - Title"));
    }

    @Test
    void testMusicTrackParser() {
        NamesParser.NumberTitle nt;
        nt = NamesParser.parseMusicTrack("02 - Title.mp3");
        Assertions.assertNotNull(nt);
        Assertions.assertEquals(2, nt.getNumber());
        Assertions.assertEquals("Title", nt.getTitle());

        Assertions.assertNull(NamesParser.parseMusicTrack("02  - Title"));
        Assertions.assertNull(NamesParser.parseMusicTrack("t2 - Title"));

        nt = NamesParser.parseMusicTrack("22 - Title. Can have dots.mp3");
        Assertions.assertNotNull(nt);
        Assertions.assertEquals(22, nt.getNumber());
        Assertions.assertEquals("Title. Can have dots", nt.getTitle());

        nt = NamesParser.parseMusicTrack("001 - This is the painkiller.mp3");
        assert nt != null;
        assertThat(nt.getNumber()).isEqualTo(1);
        assertThat(nt.getTitle()).isEqualTo("This is the painkiller");

        assertThat(NamesParser.parseMusicTrack("01-First day.mp3")).isNull();
        assertThat(NamesParser.parseMusicTrack("0101-Too much.mp3")).isNull();
    }

    @Test
    void testMusicVideoTrackParser() {
        var ntTitle = NamesParser.parseMusicVideoTrack("02 Title with spaces.avi");
        Assertions.assertNotNull(ntTitle);
        assertThat(ntTitle.hasArtistName()).isFalse();
        assertThat(ntTitle.getNumber()).isEqualTo(2);
        assertThat(ntTitle.getTitle()).isEqualTo("Title with spaces");

        var ntArtistTitle = NamesParser.parseMusicVideoTrack("03 H-Blockx - Time to move.mkv");
        Assertions.assertNotNull(ntArtistTitle);
        assertThat(ntArtistTitle.getNumber()).isEqualTo(3);
        assertThat(ntArtistTitle.hasArtistName()).isTrue();
        assertThat(ntArtistTitle.getArtistName()).isEqualTo("H-Blockx");
        assertThat(ntArtistTitle.getTitle()).isEqualTo("Time to move");

        assertThat(NamesParser.parseMusicVideoTrack("0101-Too much.avi")).isNull();
    }

    @Test
    void testVideoTrackParser() {
        NamesParser.NumberTitle nt;
        nt = NamesParser.parseVideoTrack("02 Title.avi");
        Assertions.assertNotNull(nt);
        Assertions.assertEquals(2, nt.getNumber());
        Assertions.assertEquals("Title", nt.getTitle());

        Assertions.assertNull(NamesParser.parseVideoTrack("02  Title"));
        Assertions.assertNull(NamesParser.parseVideoTrack("t2 Title"));

        nt = NamesParser.parseVideoTrack("22 Title. Can have dots.MKV");
        Assertions.assertNotNull(nt);
        Assertions.assertEquals(22, nt.getNumber());
        Assertions.assertEquals("Title. Can have dots", nt.getTitle());

        nt = NamesParser.parseVideoTrack("001 This is the painkiller.MKV");
        assert nt != null;
        assertThat(nt.getNumber()).isEqualTo(1);
        assertThat(nt.getTitle()).isEqualTo("This is the painkiller");

        assertThat(NamesParser.parseVideoTrack("01-First day.mp3")).isNull();
        assertThat(NamesParser.parseVideoTrack("0101-Too much.mp3")).isNull();

        nt = NamesParser.parseVideoTrack("001 With 3 digits.MKV");
        assertThat(nt).isNotNull();
        assert nt != null;
        assertThat(nt.getNumber()).isEqualTo(1L);
        assertThat(nt.getTitle()).isEqualTo("With 3 digits");

        nt = NamesParser.parseVideoTrack("05 With brackets and space (part 1).mkv");
        assertThat(nt).isNotNull();
        assert nt != null;
        assertThat(nt.getNumber()).isEqualTo(5L);
        assertThat(nt.getTitle()).isEqualTo("With brackets and space");

        nt = NamesParser.parseVideoTrack("04 With. brackets(part 1).mkv");
        assertThat(nt).isNotNull();
        assert nt != null;
        assertThat(nt.getNumber()).isEqualTo(4L);
        assertThat(nt.getTitle()).isEqualTo("With. brackets");
    }

    @Test
    void testPattern() {
        Pattern pattern = Pattern.compile("((?:19|20)[0-9]{2})\\s(\\S.*)");
        Matcher matcher = pattern.matcher("1980 Title");
        if (matcher.find() && matcher.groupCount() == 2) {
            log.info("Group 1:" + Integer.parseInt(matcher.group(1)));
            log.info("Group 2:" + matcher.group(2));
        }
    }

    @Test
    void testDiskNumFromFileName() {
        Assertions.assertEquals(1, NamesParser.getDiskNumFromFileName("Name without digits.cue"));
        Assertions.assertEquals(1, NamesParser.getDiskNumFromFileName("Something CD1.cue"));
        Assertions.assertEquals(2, NamesParser.getDiskNumFromFileName("Something CD2.cue"));
        Assertions.assertEquals(2, NamesParser.getDiskNumFromFileName("Something 2013 CD2.cue"));
        Assertions.assertEquals(1, NamesParser.getDiskNumFromFileName("Something 2022.cue"));
        Assertions.assertEquals(1, NamesParser.getDiskNumFromFileName("Something CD2"));
    }

    @Test
    void testDiskNumFromFolderName() {
        Assertions.assertEquals(0, NamesParser.getDiskNumFromFolderName("Name without digits"));
        Assertions.assertEquals(0, NamesParser.getDiskNumFromFolderName("Name without digits CD1"));
        Assertions.assertEquals(1, NamesParser.getDiskNumFromFolderName("CD1"));
        Assertions.assertEquals(2, NamesParser.getDiskNumFromFolderName("CD2"));
        Assertions.assertEquals(0, NamesParser.getDiskNumFromFolderName("CD2x"));
        Assertions.assertEquals(0, NamesParser.getDiskNumFromFolderName("CD2.exe"));
        Assertions.assertEquals(12, NamesParser.getDiskNumFromFolderName("CD12"));
    }

    @Test
    void testFileExtensionPattern() {
        Pattern pattern = Pattern.compile("(AVI|MKV)$");
        Matcher matcher = pattern.matcher("something.MKV");
        assertThat(matcher.find()).isTrue();
    }

    @Test
    void testMediaFormatValidator() {
        assertThat(NamesParser.validateFileNameMediaFormat("abc.mkv", "MKV|AVI")).isTrue();
        assertThat(NamesParser.validateFileNameMediaFormat("abc.Avi", "MKV|AVI")).isTrue();
        assertThat(NamesParser.validateFileNameMediaFormat("mkv.abc", "MKV|AVI")).isFalse();
        assertThat(NamesParser.validateFileNameMediaFormat("abcmkv", "MKV|AVI")).isFalse();
    }

    @Test
    void testCleanupFileName() {
        assertThat(NamesParser.cleanupFileName("Who Killed Who?")).isEqualTo("Who Killed Who");
        assertThat(NamesParser.cleanupFileName("Attention: this is is")).isEqualTo("Attention this is is");
    }
}
