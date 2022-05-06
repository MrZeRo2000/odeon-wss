package com.romanpulov.odeonwss.utils.media;

import java.nio.file.Path;

public class FFMPEGMediaFileParser implements MediaFileParserInterface {
    private final String executableFileName;

    public FFMPEGMediaFileParser(String executableFileName) {
        this.executableFileName = executableFileName;
    }

    @Override
    public MediaFileInfo parseMediaFile(Path file) throws MediaFileInfoException {
        return null;
    }
}
