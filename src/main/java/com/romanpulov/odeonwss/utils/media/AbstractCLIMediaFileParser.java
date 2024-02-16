package com.romanpulov.odeonwss.utils.media;

import com.romanpulov.odeonwss.utils.media.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractCLIMediaFileParser implements MediaFileParserInterface {

    protected final String executableFileName;

    public String getExecutableFileName() {
        return executableFileName;
    }

    public AbstractCLIMediaFileParser(String cliPath, String cliFileName) {
        this.executableFileName = Path.of(cliPath, cliFileName).toString();
    }

    protected abstract List<String> getProcessCommands(Path file);

    protected abstract MediaContentInfo parseOutput(String text) throws MediaInfoParsingException;

    @Override
    public MediaFileInfo parseMediaFile(Path file) throws MediaFileInfoException {
        try {
            Process process = new ProcessBuilder()
                    .command(getProcessCommands(file))
                    .start();

            String inputStreamText = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining());

            String errorStreamText = new BufferedReader(
                    new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining());

            if (errorStreamText.isEmpty()) {
                MediaContentInfo mediaContentInfo = parseOutput(inputStreamText);
                return new MediaFileInfo(
                        file.getFileName().toString(),
                        mediaContentInfo
                );
            } else {
                throw new MediaFileInfoException(file.getFileName().toString(), "Error during file processing:" + errorStreamText);
            }

        } catch (IOException e) {
            throw new MediaFileInfoException(file.getFileName().toString(), "IO Error:" + e.getMessage());
        } catch (MediaInfoParsingException e) {
            throw new MediaFileInfoException(file.getFileName().toString(), e.getMessage());
        }
    }
}
