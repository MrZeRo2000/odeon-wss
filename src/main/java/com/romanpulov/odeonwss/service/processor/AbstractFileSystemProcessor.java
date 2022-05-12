package com.romanpulov.odeonwss.service.processor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public abstract class AbstractFileSystemProcessor extends AbstractProcessor {
    protected Path validateAndGetPath() throws ProcessorException {
        Path path = Path.of(Optional.ofNullable(
                rootFolder).orElseThrow(() -> new ProcessorException(ProcessorMessages.ERROR_ROOT_FOLDER_NOT_FOUND)
        ));

        if (Files.notExists(path)) {
            throw new ProcessorException(String.format(ProcessorMessages.ERROR_PATH_NOT_FOUND, path.toAbsolutePath().toString()));
        }

        return path;
    }
}
