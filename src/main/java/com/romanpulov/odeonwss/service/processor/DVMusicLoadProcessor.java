package com.romanpulov.odeonwss.service.processor;

import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class DVMusicLoadProcessor extends AbstractFileSystemProcessor {
    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

    }
}
