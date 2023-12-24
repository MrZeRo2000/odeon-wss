package com.romanpulov.odeonwss.utils.media;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChaptersParser {
    private static final Pattern REGEXP_PATTERN_TIMESTAMP =
            Pattern.compile("=(\\d{1,2}):(\\d{1,2}):(\\d{1,2})\\.(\\d{1,3})");

    public static Collection<Long> parseFile(Path file) throws ChaptersParsingException {
        String fileName = file.getFileName().toString();
        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            throw new ChaptersParsingException("Error reading chapters file %s: %s"
                    .formatted(fileName, e.getMessage()));
        }

        return parseLines(fileName, lines);
    }

    public static Collection<Long> parseLines(String fileName, Collection<String> lines) throws ChaptersParsingException {
        Long previousTimeStamp = null;
        List<Long> result = new ArrayList<>();

        for (String line: lines) {
            Matcher matcher = REGEXP_PATTERN_TIMESTAMP.matcher(line);
            if (matcher.find() && matcher.groupCount() == 4)
                try {
                    long timeStamp =
                            (long) Integer.parseInt(matcher.group(1)) * 60 * 60 +
                                    (long) Integer.parseInt(matcher.group(2)) * 60 +
                                    Integer.parseInt(matcher.group(3)) +
                                    Math.round(Integer.parseInt(matcher.group(4)) / 1e3);

                    if (previousTimeStamp != null) {
                        result.add(timeStamp - previousTimeStamp);
                    }
                    previousTimeStamp = timeStamp;

                } catch (NumberFormatException e) {
                    throw new ChaptersParsingException("Error reading parsing line: %s in chapters file %s: %s"
                            .formatted(line, fileName, e.getMessage()));
                }
        }

        return result;
    }
}
