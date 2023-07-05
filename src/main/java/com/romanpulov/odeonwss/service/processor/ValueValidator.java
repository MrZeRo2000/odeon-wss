package com.romanpulov.odeonwss.service.processor;

import java.util.*;
import java.util.function.Function;
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

    public static <T> boolean validateEmptyValue(
            AbstractProcessor processor,
            Collection<T> list,
            String errorMessage,
            Function<T, Object> valueMapper,
            Function<T, String> errorMessageMapper
            ) {
        List<T> emptyList = list.stream().filter(v -> Objects.isNull(valueMapper.apply(v))).collect(Collectors.toList());
        if (emptyList.size() > 0) {
            processor.errorHandler(errorMessage, sortedCollection(
                    emptyList.stream().map(errorMessageMapper).collect(Collectors.toList())
            ));
            return false;
        }

        return true;
    }

    public static <T> boolean validateNonEmptyCollection(
            AbstractProcessor processor,
            Collection<T> collection,
            String errorMessage,
            Function<T, String> errorMessageMapper
    ) {
        List<T> list = collection.stream().toList();
        if (list.size() > 0) {
            processor.errorHandler(errorMessage, sortedCollection(
                    list.stream().map(errorMessageMapper).collect(Collectors.toList())
            ));
            return false;
        }

        return true;
    }

}
