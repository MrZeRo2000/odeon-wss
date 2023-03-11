package com.romanpulov.odeonwss.service.processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathReader {
    private static final Predicate<Path> DEFAULT_PREDICATE_PATH = p -> true;

    public enum ReadRule {
        RR_DIRECTORY,
        RR_FILE;

        static boolean validateRule(Path path, ReadRule readRule) {
            return (readRule == null) ||
                    (Files.isDirectory(path) && readRule.equals(RR_DIRECTORY)) ||
                    (!Files.isDirectory(path) && readRule.equals(RR_FILE));
        }
    }

    private static boolean readPath(
            Path path,
            Predicate<Path> filterPath,
            ReadRule filterRule,
            ReadRule validationRule,
            Collection<Path> paths) throws ProcessorException
    {
        try (Stream<Path> stream = Files.list(path)) {
            for (Path p: stream.collect(Collectors.toList())) {
                if (ReadRule.validateRule(p, filterRule)) {
                    if (!ReadRule.validateRule(p, validationRule)) {
                        // add path to display in the error message
                        paths.add(p);
                        return false;
                    } else {
                        // add considering filters
                        if (filterPath.test(p)) {
                            paths.add(p);
                        }
                    }
                }
            }
            return true;
        } catch (IOException e) {
            throw new ProcessorException(ProcessorMessages.ERROR_PROCESSING_FILES, e.getMessage());
        }
    }

    public static boolean readPathAll(Path path, Collection<Path> paths) throws ProcessorException {
        return readPath(path, DEFAULT_PREDICATE_PATH, null, null, paths);
    }

    public static boolean readPathFoldersOnly(
            AbstractProcessor processor,
            Path path,
            Collection<Path> folders) throws ProcessorException
    {
        boolean result = readPath(path, DEFAULT_PREDICATE_PATH,null, ReadRule.RR_DIRECTORY, folders);
        if (!result) {
            processor.errorHandler(
                    ProcessorMessages.ERROR_EXPECTED_DIRECTORY,
                    folders.stream().findFirst().orElseThrow().toString());
        }
        return result;
    }

    public static boolean readPathPredicateFilesOnly(
            AbstractProcessor processor,
            Path path,
            Predicate<Path> pathPredicate,
            Collection<Path> files) throws ProcessorException
    {
        boolean result = readPath(path, pathPredicate, null, ReadRule.RR_FILE, files);
        if (!result) {
            processor.errorHandler(
                    ProcessorMessages.ERROR_EXPECTED_FILE,
                    files.stream().findFirst().orElseThrow().toString());
        }
        return result;
    }

    public static boolean readPathFilesOnly(
            AbstractProcessor processor,
            Path path,
            Collection<Path> files) throws ProcessorException
    {
        return readPathPredicateFilesOnly(processor, path, DEFAULT_PREDICATE_PATH, files);
    }
}
