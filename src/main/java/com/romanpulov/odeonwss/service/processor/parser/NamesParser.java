package com.romanpulov.odeonwss.service.processor.parser;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamesParser {
    private final static Logger logger = LoggerFactory.getLogger(NamesParser.class);

    private static final Pattern REGEXP_PATTERN_MUSIC_ARTIFACT = Pattern.compile("^((?:19|20)[0-9]{2})\\s(\\S.*)");
    private static final Pattern REGEXP_PATTERN_VIDEO_MUSIC_ARTIFACT = Pattern.compile("\\s(\\d{4})$");
    private static final Pattern REGEXP_PATTERN_MUSIC_TRACK = Pattern.compile("^([0-9]{2,3})\\s-\\s(\\S.*)\\.\\S{2,4}$");
    private static final Pattern REGEXP_PATTERN_VIDEO_TRACK = Pattern.compile("^([0-9]{2,3})\\s(\\S.+?\\S)(?:\\s*\\([^)]+\\))*\\.\\S{2,4}$");
    private static final Pattern REGEXP_PATTERN_MUSIC_VIDEO_TRACK = Pattern.compile("^([0-9]{2,3})\\s(\\S.+?\\S)(?:\\s-\\s(\\S.+?\\S))*(?:\\s*\\([\\d^)]{4}\\))*\\.\\S{2,4}$");
    private static final Pattern REGEXP_PATTERN_FOLDER_NAME_DISK_NUM = Pattern.compile("^CD(\\d+)$");
    private static final Pattern REGEXP_PATTERN_FILE_NAME_DISK_NUM = Pattern.compile("CD(\\d+)\\.");

    private static final String REGEXP_STRING_FILE_NAME_CLEANUP = "[?:\">/<*]";

    private static final String FORMAT_MUSIC_ARTIFACT = "%d %s";

    private static final String FORMAT_MEDIA_FORMATS_REGEXP = "\\.(%s)$";
    private static final Map<String, Pattern> MEDIA_FORMATS_PATTERN_MAP = new ConcurrentHashMap<>();

    public record YearTitle(int year, String title) { }

    public static class NumberTitle {
        private long number;
        private String artistName;
        private String title;

        public long getNumber() {
            return number;
        }

        public String getArtistName() {
            return artistName;
        }

        public String getTitle() {
            return title;
        }

        public boolean hasArtistName() {
            return artistName != null && !artistName.isEmpty();
        }

        public static NumberTitle fromTitle(int number, String title) {
            NumberTitle instance = new NumberTitle();
            instance.number = number;
            instance.title = title;
            return instance;
        }

        public static NumberTitle fromArtistAndTitle(int number, String artistName, String title) {
            NumberTitle instance = fromTitle(number, title);
            instance.artistName = artistName;
            return instance;
        }

        private NumberTitle() { }

        @Override
        public String toString() {
            return "NumberTitle{" +
                    "number=" + number +
                    ", artistName='" + artistName + '\'' +
                    ", title='" + title + '\'' +
                    '}';
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

    public static Long parseVideoMusicArtifactTitleYear(String title) {
        Matcher matcher = REGEXP_PATTERN_VIDEO_MUSIC_ARTIFACT.matcher(title);
        return matcher.find() && matcher.groupCount() == 1 ? Long.parseLong(matcher.group(1)) : null;
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
            return NumberTitle.fromTitle(Integer.parseInt(matcher.group(1)), matcher.group(2));
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
            return NumberTitle.fromTitle(Integer.parseInt(matcher.group(1)), matcher.group(2));
        } else {
            return null;
        }
    }

    public static NumberTitle parseMusicVideoTrack(String name) {
        Matcher matcher = REGEXP_PATTERN_MUSIC_VIDEO_TRACK.matcher(name);
        if (matcher.find() && (matcher.groupCount() == 3)) {
            if (matcher.group(3) == null) {
                return NumberTitle.fromTitle(Integer.parseInt(matcher.group(1)), matcher.group(2));
            } else {
                return NumberTitle.fromArtistAndTitle(Integer.parseInt(matcher.group(1)), matcher.group(2), matcher.group(3));
            }
        } else {
            return null;
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
            logger.debug(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    public static String cleanupFileName(String fileName) {
        return fileName.replaceAll(REGEXP_STRING_FILE_NAME_CLEANUP, "");
    }
}
