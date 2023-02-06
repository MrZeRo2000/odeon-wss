package com.romanpulov.odeonwss.service.processor;

import java.util.*;
import java.util.stream.Collectors;

public class ValueValidator {
    public static boolean isEmpty(Long value) {
        return (value == null) || (value == 0L);
    }

    private static <T> List<T> sortedCollection(Collection<T> data) {
        return data.stream().sorted().collect(Collectors.toList());
    }

    private static <T> Set<T> nonNullSet(Set<T> data) {
        return (new HashSet<>(data)).stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public static boolean compareStringSets(
            AbstractProcessor processor,
            Set<String> pathStrings,
            Set<String> dbStrings,
            String pathErrorMessage,
            String dbErrorMessage)
    {
        boolean result = true;

        Set<String> pathStringsDiff = nonNullSet(pathStrings);
        pathStringsDiff.removeAll(nonNullSet(dbStrings));
        if (!pathStringsDiff.isEmpty()) {
            processor.errorHandler(
                    dbErrorMessage,
                    sortedCollection(pathStringsDiff));
            result = false;
        }

        Set<String> dbStringsDiff = nonNullSet(dbStrings);
        dbStringsDiff.removeAll(nonNullSet(pathStrings));
        if (!dbStringsDiff.isEmpty()) {
            processor.errorHandler(pathErrorMessage, sortedCollection(dbStringsDiff));
            result = false;
        }

        return result;
    }
}
