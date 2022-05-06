package com.romanpulov.odeonwss.utils.media;

import java.nio.file.Path;

public interface MediaFileParserInterface {
    MediaFileInfo parseMediaFile(Path file) throws MediaFileInfoException;
}
