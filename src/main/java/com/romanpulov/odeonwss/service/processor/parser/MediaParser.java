package com.romanpulov.odeonwss.service.processor.parser;

import com.romanpulov.odeonwss.service.processor.ProcessorException;
import com.romanpulov.odeonwss.utils.media.MediaFileInfo;
import com.romanpulov.odeonwss.utils.media.MediaFileInfoException;
import com.romanpulov.odeonwss.utils.media.MediaFileParserInterface;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.romanpulov.odeonwss.service.processor.parser.ParserMessages.*;

@Component
public class MediaParser {

    private final MediaFileParserInterface mediaFileParser;

    public MediaParser(MediaFileParserInterface mediaFileParser) {
        this.mediaFileParser = mediaFileParser;
    }

    public MediaFileInfo parseTrack(Path trackPath) throws MediaFileInfoException {
        return mediaFileParser.parseMediaFile(trackPath);
    }

    public Map<Path, MediaFileInfo> parseTracks(List<Path> trackPaths)
            throws ProcessorException {
        List<Callable<Pair<Path, MediaFileInfo>>> callables = new ArrayList<>();

        for (Path path: trackPaths) {
            Callable<Pair<Path, MediaFileInfo>> callable = () -> {
                MediaFileInfo mediafileInfo = mediaFileParser.parseMediaFile(path);
                return Pair.of(path, mediafileInfo);
            };
            callables.add(callable);
        }

        List<Future<Pair<Path, MediaFileInfo>>> futures;
        ExecutorService executorService = Executors.newFixedThreadPool(4);
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
}
