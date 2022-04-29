package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.config.AppConfiguration;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LoadMP3Processor extends AbstractProcessor {

    private Logger logger = LoggerFactory.getLogger(LoadMP3Processor.class);

    @Override
    public void execute() throws ProcessorException {
        logger.debug("Starting processor execution");

        Path path = Path.of(Optional.ofNullable(
                rootPath).orElseThrow(() -> new ProcessorException("MP3 file not specified")
        ));

        if (Files.notExists(path)) {
            throw new ProcessorException("Path not found:" + path);
        }
    }
}
