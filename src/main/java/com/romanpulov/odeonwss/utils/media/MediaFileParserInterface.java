package com.romanpulov.odeonwss.utils.media;

import com.romanpulov.odeonwss.utils.media.model.MediaFileInfo;

import java.nio.file.Path;

public interface MediaFileParserInterface {
    String getExecutableFileName();
    MediaFileInfo parseMediaFile(Path file) throws MediaFileInfoException;
}
