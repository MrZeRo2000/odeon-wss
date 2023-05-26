package com.romanpulov.odeonwss.utils.media;

import java.nio.file.Path;

public class MediaInfoMediaFileParser implements MediaFileParserInterface {
    private final static String MEDIA_INFO_FILE_NAME = "MediaInfo.exe";

    private final String executableFileName;

    public String getExecutableFileName() {
        return executableFileName;
    }

    public MediaInfoMediaFileParser(String mediaInfoPath) {
        this.executableFileName = Path.of(mediaInfoPath, MEDIA_INFO_FILE_NAME).toString();
    }

    @Override
    public MediaFileInfo parseMediaFile(Path file) throws MediaFileInfoException {
        return null;
    }
}
