package com.romanpulov.odeonwss.service.processor.parser;

import com.romanpulov.odeonwss.service.processor.ProcessorException;
import com.romanpulov.odeonwss.utils.media.model.MediaFileInfo;
import com.romanpulov.odeonwss.utils.media.MediaFileInfoException;
import com.romanpulov.odeonwss.utils.media.MediaFileParserInterface;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static com.romanpulov.odeonwss.service.processor.parser.ParserMessages.*;

@Component
public class MediaParser {
    private static final Logger logger = LoggerFactory.getLogger(MediaParser.class);

    private final MediaFileParserInterface mediaFileParser;

    public MediaParser(MediaFileParserInterface mediaFileParser) {
        this.mediaFileParser = mediaFileParser;
    }

    public MediaFileInfo parseTrack(Path trackPath) throws MediaFileInfoException {
        logger.debug("Parsing track: " + trackPath);
        return mediaFileParser.parseMediaFile(trackPath);
    }

    public Map<Path, MediaFileInfo> parseTracks(
            Collection<Path> trackPaths,
            Consumer<String> parsingFileCallback,
            Consumer<String> parsedFileCallback) throws ProcessorException {
        Collection<Callable<Pair<Path, MediaFileInfo>>> callables = getParseCallables(
                trackPaths,
                parsingFileCallback,
                parsedFileCallback);

        Collection<Future<Pair<Path, MediaFileInfo>>> futures;
        ExecutorService executorService = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors() + 1);
        try {
            futures = executorService.invokeAll(callables);
        } catch (InterruptedException e) {
            throw new ProcessorException("Error executing callables for media file info retrieval:" + e.getMessage());
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        Map<Path, MediaFileInfo> result = new HashMap<>();

        try {
            for (Future<Pair<Path, MediaFileInfo>> future : futures) {
                Pair<Path, MediaFileInfo> futureData = future.get();
                result.put(futureData.getFirst(), futureData.getSecond());
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new ProcessorException(ERROR_PARSING_FILE, e.getMessage());
        }

        return result;
    }

    @NotNull
    private Collection<Callable<Pair<Path, MediaFileInfo>>> getParseCallables(
            Collection<Path> trackPaths,
            Consumer<String> parsingFileCallback,
            Consumer<String> parsedFileCallback) {
        List<Callable<Pair<Path, MediaFileInfo>>> callables = new ArrayList<>();

        for (Path path: trackPaths) {
            Callable<Pair<Path, MediaFileInfo>> callable = () -> {
                logger.debug("Parsing track: " + path);
                parsingFileCallback.accept(path.toString());

                MediaFileInfo mediafileInfo = mediaFileParser.parseMediaFile(path);

                parsedFileCallback.accept(path.toString());
                logger.debug("Parsed track: " + path);

                return Pair.of(path, mediafileInfo);
            };
            callables.add(callable);
        }
        return callables;
    }
}
