package com.romanpulov.odeonwss.service.processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamesParser {
    private static final Pattern REGEXP_PATTERN_MUSIC_ARTIFACT = Pattern.compile("^((?:19|20)[0-9]{2})\\s(\\S.*)");
    private static final Pattern REGEXP_PATTERN_MUSIC_COMPOSITION = Pattern.compile("^([0-9][0-9])\\s-\\s(\\S.*)(?:\\.\\S{2,4})$");
    private static final String FORMAT_MUSIC_ARTIFACT = "%d %s";
    private static final String FORMAT_MUSIC_COMPOSITION = "%d - %s";
    private static final String FORMAT_MUSIC_WITH_FILE_NAME = "%d - %s (%s)";
    private static final String FORMAT_MUSIC_WITHOUT_FILE_NAME = "%d - %s (no file name)";

    public static class YearTitle {
        private final int year;

        public int getYear() {
            return year;
        }

        private final String title;

        public String getTitle() {
            return title;
        }

        public YearTitle(int year, String title) {
            this.year = year;
            this.title = title;
        }
    }

    public static class NumberTitle {
        private final long number;

        public long getNumber() {
            return number;
        }

        private final String title;

        public String getTitle() {
            return title;
        }

        public NumberTitle(int number, String title) {
            this.number = number;
            this.title = title;
        }
    }

    public static boolean validateMusicArtifactTitle(String name) {
        return REGEXP_PATTERN_MUSIC_ARTIFACT.matcher(name).matches();
    }

    public static YearTitle parseMusicArtifactTitle(String title) {
        Matcher matcher = REGEXP_PATTERN_MUSIC_ARTIFACT.matcher(title);
        if (matcher.find() && matcher.groupCount() == 2) {
            return new YearTitle(Integer.parseInt(matcher.group(1)), matcher.group(2));
        } else {
            return null;
        }
    }

    public static String formatMusicArtifact(long year, String title) {
        return String.format(FORMAT_MUSIC_ARTIFACT, year, title);
    }

    public static boolean validateMusicComposition(String name) {
        return REGEXP_PATTERN_MUSIC_COMPOSITION.matcher(name).matches();
    }

    public static NumberTitle parseMusicComposition(String name) {
        Matcher matcher = REGEXP_PATTERN_MUSIC_COMPOSITION.matcher(name);
        if (matcher.find() && matcher.groupCount() == 2) {
            return new NumberTitle(Integer.parseInt(matcher.group(1)), matcher.group(2));
        } else {
            return null;
        }
    }

    public static String formatMusicComposition(long num, String title) {
        return String.format(FORMAT_MUSIC_COMPOSITION, num, title);
    }

    public static String formatMusicCompositionWithFile(long num, String title, String fileName) {
        if (fileName == null) {
            return String.format(FORMAT_MUSIC_WITHOUT_FILE_NAME, num, title);
        } else {
            return String.format(FORMAT_MUSIC_WITH_FILE_NAME, num, title, fileName);
        }
    }
}
