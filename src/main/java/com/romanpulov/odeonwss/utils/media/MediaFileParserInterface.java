package com.romanpulov.odeonwss.utils.media;

import java.nio.file.Path;
import java.time.LocalTime;
import java.util.List;

public interface MediaFileParserInterface {
    String getExecutableFileName();
    MediaFileInfo parseMediaFile(Path file) throws MediaFileInfoException;
    List<LocalTime> getMediaFileChapters(Path file) throws MediaFileInfoException;
}
