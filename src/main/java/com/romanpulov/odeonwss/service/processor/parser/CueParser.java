package com.romanpulov.odeonwss.service.processor.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CueParser {
    private static final Pattern REGEXP_PATTERN_FILE = Pattern.compile("FILE\\s+\"(.*)\"");
    private static final Pattern REGEXP_PATTERN_TITLE = Pattern.compile("TITLE\\s+\"(.*)\"");
    private static final Pattern REGEXP_PATTERN_TRACK = Pattern.compile("TRACK\\s+(\\d+)");
    private static final Pattern REGEXP_PATTERN_INDEX = Pattern.compile("INDEX\\s+01\\s+(\\d{2}):(\\d{2})");

    private static final Map<CUE_STATE, Pattern> PATTERN_MAPPING = new HashMap<>();

    static {
        PATTERN_MAPPING.put(CUE_STATE.ST_FILE, REGEXP_PATTERN_FILE);
        PATTERN_MAPPING.put(CUE_STATE.ST_TRACK_TITLE, REGEXP_PATTERN_TITLE);
        PATTERN_MAPPING.put(CUE_STATE.ST_TRACK, REGEXP_PATTERN_TRACK);
        PATTERN_MAPPING.put(CUE_STATE.ST_TRACK_INDEX, REGEXP_PATTERN_INDEX);
    }

    public static class CueTrack {
        private String fileName;
        private int num;
        private String title;
        private int section;

        public String getFileName() {
            return fileName;
        }

        private void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public int getNum() {
            return num;
        }

        private void setNum(int num) {
            this.num = num;
        }

        public String getTitle() {
            return title;
        }

        private void setTitle(String title) {
            this.title = title;
        }

        public int getSection() {
            return section;
        }

        private void setSection(int section) {
            this.section = section;
        }

        public CueTrack() {}

        public CueTrack(String fileName, int num, String title, int section) {
            this.fileName = fileName;
            this.num = num;
            this.title = title;
            this.section = section;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CueTrack cueTrack = (CueTrack) o;
            return num == cueTrack.num && section == cueTrack.section && Objects.equals(fileName, cueTrack.fileName) && Objects.equals(title, cueTrack.title);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fileName, num, title, section);
        }

        @Override
        public String toString() {
            return "CueTrack{" +
                    "fileName='" + fileName + '\'' +
                    ", num=" + num +
                    ", title='" + title + '\'' +
                    ", section=" + section +
                    '}';
        }
    }

    static class StateData {
        final CUE_STATE state;
        final Matcher matcher;

        public StateData(CUE_STATE state, Matcher matcher) {
            this.state = state;
            this.matcher = matcher;
        }
    }

    enum CUE_STATE {
        ST_FILE,
        ST_TRACK,
        ST_TRACK_TITLE,
        ST_TRACK_INDEX
    }

    static StateData parseLine(String s) {
        AtomicReference<StateData> stateData = new AtomicReference<>(null);

        PATTERN_MAPPING.forEach((state, pattern) -> {
            if (stateData.get() == null) {
                Matcher findMatcher = pattern.matcher(s);
                if (findMatcher.find()) {
                    stateData.set(new StateData(state, findMatcher));
                }
            }
        });

        return stateData.get();
    }

    public static List<CueTrack> parseFile(Path file) {
        List<CueTrack> result = new ArrayList<>();

        StateData state;

        CueTrack prevTrack;
        CueTrack track = null;

        try (Stream<String> streamLines = Files.lines(file))  {
            List<String> lines = streamLines.collect(Collectors.toList());

            for (String s: lines) {
                state = parseLine(s);
                if (state == null) {
                    continue;
                } else if (state.state == CUE_STATE.ST_FILE) {
                    //save current track if exists
                    if (track != null) {
                        result.add(track);
                    }

                    //we expect that there will be a track
                    track = new CueTrack();
                    track.fileName = state.matcher.group(1);
                } else if (state.state == CUE_STATE.ST_TRACK) {
                    int trackNum = Integer.parseInt(state.matcher.group(1));

                    if (track != null) {
                        //save previous track if available
                        if (track.title != null) {
                            result.add(track);
                            prevTrack = track;

                            //create new track
                            track = new CueTrack();

                            if (track.fileName == null) {
                                //transfer previous file name if available
                                track.fileName = prevTrack.fileName;
                            }
                        }
                        track.num = trackNum;
                    }

                } else if (state.state == CUE_STATE.ST_TRACK_INDEX) {
                    if ((track != null) && (track.num > 0)) {
                        track.section = Integer.parseInt(state.matcher.group(1)) * 60 + Integer.parseInt(state.matcher.group(2));
                    }
                } else if (state.state == CUE_STATE.ST_TRACK_TITLE) {
                    if ((track != null) && (track.num > 0)) {
                        track.title = state.matcher.group(1);
                    }
                }
            }

            // save last track
            if (track != null) {
                result.add(track);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        //try to get from file name
        result.stream().filter(t -> Objects.isNull(t.title) || t.num == 0).forEach(t -> {
            NamesParser.NumberTitle nt = NamesParser.parseMusicTrack(t.fileName);
            if (nt != null) {
                if (t.num == 0) {
                    t.num = (int)nt.getNumber();
                }
                if (t.title == null) {
                    t.title = nt.getTitle();
                }
            }
        });

        return result;
    }
}
