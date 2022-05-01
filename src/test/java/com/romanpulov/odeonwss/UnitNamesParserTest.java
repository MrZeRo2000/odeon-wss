package com.romanpulov.odeonwss;


import com.romanpulov.odeonwss.service.processor.NamesParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Assertions.assertEquals(1980, yt.year);
        Assertions.assertEquals("Title", yt.title);

        yt = NamesParser.parseMusicArtifactTitle("198r Title");
        Assertions.assertNull(yt);

        yt = NamesParser.parseMusicArtifactTitle("2022 Title can be long");
        Assertions.assertNotNull(yt);
        Assertions.assertEquals(2022, yt.year);
        Assertions.assertEquals("Title can be long", yt.title);
    }

    @Test
    void testMusicCompositionValidator() {
        Assertions.assertTrue(NamesParser.validateMusicComposition("02 - Title"));
        Assertions.assertTrue(NamesParser.validateMusicComposition("21 - Title can be long"));

        Assertions.assertFalse(NamesParser.validateMusicComposition("1980 Title"));
        Assertions.assertFalse(NamesParser.validateMusicComposition(" 02 - Title"));
        Assertions.assertFalse(NamesParser.validateMusicComposition("02  - Title"));
        Assertions.assertFalse(NamesParser.validateMusicComposition("02- Title"));
        Assertions.assertFalse(NamesParser.validateMusicComposition("02 -Title"));
        Assertions.assertFalse(NamesParser.validateMusicComposition("02 -  Title"));
        Assertions.assertFalse(NamesParser.validateMusicComposition("102 -  Title"));
        Assertions.assertFalse(NamesParser.validateMusicComposition("d2 - Title"));
    }

    @Test
    void testMusicCompositionParser() {
        NamesParser.NumberTitle nt;
        nt = NamesParser.parseMusicComposition("02 - Title");
        Assertions.assertNotNull(nt);
        Assertions.assertEquals(2, nt.number);
        Assertions.assertEquals("Title", nt.title);

        Assertions.assertNull(NamesParser.parseMusicComposition("02  - Title"));
        Assertions.assertNull(NamesParser.parseMusicComposition("t2 - Title"));
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
}
