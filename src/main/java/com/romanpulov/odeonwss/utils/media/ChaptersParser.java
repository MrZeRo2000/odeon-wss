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

    public Collection<Long> parseFile(Path file) throws ChaptersParsingException {
        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            throw new ChaptersParsingException("Error reading chapters file %s: %s"
                    .formatted(file.getFileName().toString(), e.getMessage()));
        }

        Long previousTimeStamp = null;
        List<Long> result = new ArrayList<>();

        for (String line: lines) {
            Matcher matcher = REGEXP_PATTERN_TIMESTAMP.matcher(line);
            if (matcher.find() && matcher.groupCount() == 4)
                try {
                    long timeStamp =
                            (long) Integer.parseInt(matcher.group(0)) * 60 * 60 +
                            (long) Integer.parseInt(matcher.group(1)) * 60 +
                            Integer.parseInt(matcher.group(2)) +
                            Math.round(Integer.parseInt(matcher.group(3)) / 1e4);

                    if (previousTimeStamp != null) {
                        result.add(timeStamp - previousTimeStamp);
                    }
                    previousTimeStamp = timeStamp;

                } catch (NumberFormatException e) {
                    throw new ChaptersParsingException("Error reading parsing line: %s in chapters file %s: %s"
                            .formatted(line, file.getFileName().toString(), e.getMessage()));
                }
        }

        return result;
    }
}
