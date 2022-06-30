package com.romanpulov.odeonwss.service.processor;

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

@Component
public class MediaParser {

    private final MediaFileParserInterface mediaFileParser;

    public MediaParser(MediaFileParserInterface mediaFileParser) {
        this.mediaFileParser = mediaFileParser;
    }

    public MediaFileInfo parseComposition(Path compositionPath) throws MediaFileInfoException {
        return mediaFileParser.parseMediaFile(compositionPath);
    }

    public Map<String, MediaFileInfo> parseCompositions(List<Path> compositionPaths)
            throws ProcessorException {
        List<Callable<Pair<String, MediaFileInfo>>> callables = new ArrayList<>();

        for (Path path: compositionPaths) {
            Callable<Pair<String, MediaFileInfo>> callable = () -> {
                MediaFileInfo mediafileInfo = mediaFileParser.parseMediaFile(path);
                return Pair.of(path.getFileName().toString(), mediafileInfo);
            };
            callables.add(callable);
        }

        List<Future<Pair<String, MediaFileInfo>>> futures;
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

        Map<String, MediaFileInfo> result = new HashMap<>();

        try {
            for (Future<Pair<String, MediaFileInfo>> future : futures) {
                Pair<String, MediaFileInfo> futureData = future.get();
                result.put(futureData.getFirst(), futureData.getSecond());
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new ProcessorException(ProcessorMessages.ERROR_PARSING_FILE, e.getMessage());
        }

        return result;
    }
}
