package com.romanpulov.odeonwss.dto.user;

import com.romanpulov.odeonwss.exception.WrongParameterValueException;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ChapterDurationsTransformer {
    public List<Long> transform(List<String> chapters) throws WrongParameterValueException {
        List<Long> result = new ArrayList<>();

        List<String> cleanChapters = chapters
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        long lastTime = 0;
        for (String chapter: cleanChapters) {
            try {
                long time = LocalTime.parse(chapter, DateTimeFormatter.ofPattern("HH:mm:ss")).toSecondOfDay();
                result.add(time - lastTime);
                lastTime = time;
            } catch (DateTimeParseException ignore) { }
        }

        return result;
    }
}
