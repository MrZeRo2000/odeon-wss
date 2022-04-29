package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.config.AppConfiguration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LoadMP3Processor extends AbstractProcessor {

    private Logger logger = LoggerFactory.getLogger(LoadMP3Processor.class);

    @Override
    public void execute() throws ProcessorException {
        Path path = Path.of(Optional.ofNullable(
                rootPath).orElseThrow(() -> new ProcessorException("MP3 file not specified")
        ));

        if (Files.notExists(path)) {
            throw new ProcessorException("Path not found:" + path);
        }

        try {
            for (Path p : Files.list(path).collect(Collectors.toList())) {
                logger.debug("Path:" + p.getFileName());
                if (!Files.isDirectory(p)) {
                    throw new ProcessorException("Expected directory, found " + p.getFileName());
                }
            }
        } catch (IOException e) {
            throw new ProcessorException("IOException:" + e.getMessage());
        }
    }
}
