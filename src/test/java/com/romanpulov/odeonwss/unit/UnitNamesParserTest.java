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
        Assertions.assertEquals(1980, yt.getYear());
        Assertions.assertEquals("Title", yt.getTitle());

        yt = NamesParser.parseMusicArtifactTitle("198r Title");
        Assertions.assertNull(yt);

        yt = NamesParser.parseMusicArtifactTitle("2022 Title can be long");
        Assertions.assertNotNull(yt);
        Assertions.assertEquals(2022, yt.getYear());
        Assertions.assertEquals("Title can be long", yt.getTitle());
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
}
