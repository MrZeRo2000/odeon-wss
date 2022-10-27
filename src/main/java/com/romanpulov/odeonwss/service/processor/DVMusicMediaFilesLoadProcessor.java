package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.utils.media.MediaFileInfo;
import com.romanpulov.odeonwss.utils.media.MediaFileInfoException;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.romanpulov.odeonwss.service.processor.ProcessorMessages.ERROR_PARSING_FILE;
import static com.romanpulov.odeonwss.service.processor.ProcessorMessages.INFO_MEDIA_FILES_LOADED;

@Component
public class DVMusicMediaFilesLoadProcessor extends AbstractFileSystemProcessor {

    private final MediaFileRepository mediaFileRepository;

    private final MediaParser mediaParser;

    public DVMusicMediaFilesLoadProcessor(MediaFileRepository mediaFileRepository, MediaParser mediaParser) {
        this.mediaFileRepository = mediaFileRepository;
        this.mediaParser = mediaParser;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        infoHandler(INFO_MEDIA_FILES_LOADED, processArtifactsPath(path));
    }

    private int processArtifactsPath(Path path) throws ProcessorException {
        AtomicInteger counter = new AtomicInteger(0);

        List<MediaFile> emptyMediaFiles = mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(ArtifactType.withDVMusic());
        String rootMediaPath = path.toAbsolutePath().toString();

        for (MediaFile emptyMediaFile: emptyMediaFiles) {
            Path compositionPath = Paths.get(rootMediaPath, emptyMediaFile.getArtifact().getTitle(), emptyMediaFile.getName());
            if (Files.exists(compositionPath)) {
                try {
                    MediaFileInfo mediaFileInfo = mediaParser.parseComposition(compositionPath);
                    MediaFile getMediaFile = mediaFileRepository.findById(emptyMediaFile.getId()).orElseThrow();

                    getMediaFile.setSize(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getSize());
                    getMediaFile.setBitrate(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getBitRate());
                    getMediaFile.setDuration(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration());

                    mediaFileRepository.save(getMediaFile);

                    counter.getAndIncrement();
                } catch (MediaFileInfoException e) {
                    errorHandler(ERROR_PARSING_FILE, compositionPath.toAbsolutePath().toString());
                }
            }
        }

        return counter.get();
    }
}
