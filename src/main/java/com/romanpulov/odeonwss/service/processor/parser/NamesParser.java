package com.romanpulov.odeonwss.service.processor.parser;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamesParser {
    private static final Pattern REGEXP_PATTERN_MUSIC_ARTIFACT = Pattern.compile("^((?:19|20)[0-9]{2})\\s(\\S.*)");
    private static final Pattern REGEXP_PATTERN_MUSIC_TRACK = Pattern.compile("^([0-9]{2,3})\\s-\\s(\\S.*)\\.\\S{2,4}$");
    private static final Pattern REGEXP_PATTERN_VIDEO_TRACK = Pattern.compile("^([0-9]{2,3})\\s(\\S.+?\\S)(?:\\s*\\([^)]+\\))*\\.\\S{2,4}$");
    private static final Pattern REGEXP_PATTERN_FOLDER_NAME_DISK_NUM = Pattern.compile("^CD(\\d+)$");
    private static final Pattern REGEXP_PATTERN_FILE_NAME_DISK_NUM = Pattern.compile("CD(\\d+)\\.");

    private static final String REGEXP_STRING_FILE_NAME_CLEANUP = "[?:\">/<*]";

    private static final String FORMAT_MUSIC_ARTIFACT = "%d %s";
    private static final String FORMAT_MUSIC_TRACK = "%d - %s";
    private static final String FORMAT_MUSIC_WITH_FILE_NAME = "%s - %s (%s)";
    private static final String FORMAT_MUSIC_WITHOUT_FILE_NAME = "%s - %s (no file name)";

    private static final String FORMAT_MEDIA_FORMATS_REGEXP = "\\.(%s)$";
    private static final Map<String, Pattern> MEDIA_FORMATS_PATTERN_MAP = new ConcurrentHashMap<>();

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

    public static boolean validateMusicTrack(String name) {
        return REGEXP_PATTERN_MUSIC_TRACK.matcher(name).matches();
    }

    public static NumberTitle parseMusicTrack(String name) {
        Matcher matcher = REGEXP_PATTERN_MUSIC_TRACK.matcher(name);
        if (matcher.find() && matcher.groupCount() == 2) {
            return new NumberTitle(Integer.parseInt(matcher.group(1)), matcher.group(2));
        } else {
            return null;
        }
    }

    public static boolean validateVideoTrack(String name) {
        return REGEXP_PATTERN_VIDEO_TRACK.matcher(name).matches();
    }

    public static NumberTitle parseVideoTrack(String name) {
        Matcher matcher = REGEXP_PATTERN_VIDEO_TRACK.matcher(name);
        if (matcher.find() && matcher.groupCount() == 2) {
            return new NumberTitle(Integer.parseInt(matcher.group(1)), matcher.group(2));
        } else {
            return null;
        }
    }

    public static String formatMusicTrack(long num, String title) {
        return String.format(FORMAT_MUSIC_TRACK, num, title);
    }

    public static String formatMusicTrackWithFile(Long num, String title, String fileName) {
        if (fileName == null) {
            return String.format(FORMAT_MUSIC_WITHOUT_FILE_NAME, Objects.toString(num, "-"), Objects.toString(title, "-"));
        } else {
            return String.format(FORMAT_MUSIC_WITH_FILE_NAME, Objects.toString(num, "-"), Objects.toString(title, "-"), fileName);
        }
    }

    public static int getDiskNumFromFileName(String fileName) {
        Matcher matcher = REGEXP_PATTERN_FILE_NAME_DISK_NUM.matcher(fileName);
        if (matcher.find() && matcher.groupCount() == 1) {
            return Integer.parseInt(matcher.group(1));
        } else {
            return 1;
        }
    }

    public static int getDiskNumFromFolderName(String folderName) {
        Matcher matcher = REGEXP_PATTERN_FOLDER_NAME_DISK_NUM.matcher(folderName);
        if (matcher.find() && matcher.groupCount() == 1) {
            return Integer.parseInt(matcher.group(1));
        } else {
            return 0;
        }
    }

    public static boolean validateFileNameMediaFormat(String fileName, String mediaFormats) {
        try {
            Pattern pattern = MEDIA_FORMATS_PATTERN_MAP.computeIfAbsent(
                    mediaFormats,
                    f -> Pattern.compile(
                            String.format(FORMAT_MEDIA_FORMATS_REGEXP, mediaFormats),
                            Pattern.CASE_INSENSITIVE));
            return pattern.matcher(fileName).find();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String cleanupFileName(String fileName) {
        return fileName.replaceAll(REGEXP_STRING_FILE_NAME_CLEANUP, "");
    }
}
