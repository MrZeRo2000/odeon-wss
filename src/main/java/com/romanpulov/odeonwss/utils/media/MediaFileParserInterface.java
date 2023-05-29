package com.romanpulov.odeonwss.utils.media;

import java.nio.file.Path;

public interface MediaFileParserInterface {
    String getExecutableFileName();
    MediaFileInfo parseMediaFile(Path file) throws MediaFileInfoException;
}
