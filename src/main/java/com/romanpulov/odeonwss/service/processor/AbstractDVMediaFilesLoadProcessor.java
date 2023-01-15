package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.utils.media.MediaFileInfo;
import com.romanpulov.odeonwss.utils.media.MediaFileInfoException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.romanpulov.odeonwss.service.processor.ProcessorMessages.ERROR_PARSING_FILE;
import static com.romanpulov.odeonwss.service.processor.ProcessorMessages.INFO_MEDIA_FILES_LOADED;

public class AbstractDVMediaFilesLoadProcessor extends AbstractFileSystemProcessor {
    private final ArtifactType artifactType;

    private final ArtifactRepository artifactRepository;

    private final MediaFileRepository mediaFileRepository;

    private final MediaParser mediaParser;

    public AbstractDVMediaFilesLoadProcessor(
            ArtifactType artifactType,
            ArtifactRepository artifactRepository,
            MediaFileRepository mediaFileRepository,
            MediaParser mediaParser
    ) {
        this.artifactType = artifactType;
        this.artifactRepository = artifactRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.mediaParser = mediaParser;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        infoHandler(INFO_MEDIA_FILES_LOADED, processArtifactsPath(path));
    }

    private static class SizeDuration {
        long size;
        long duration;

        public SizeDuration(long size, long duration) {
            this.size = size;
            this.duration = duration;
        }

        public SizeDuration() {
        }
    }

    private int processArtifactsPath(Path path) throws ProcessorException {
        AtomicInteger counter = new AtomicInteger(0);

        List<MediaFile> emptyMediaFiles = mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(artifactType);
        Map<Long, SizeDuration> artifactSizeDurationMap = new HashMap<>();

        String rootMediaPath = path.toAbsolutePath().toString();

        // update media files
        for (MediaFile emptyMediaFile: emptyMediaFiles) {
            Path compositionPath = Paths.get(rootMediaPath, emptyMediaFile.getArtifact().getTitle(), emptyMediaFile.getName());
            if (Files.exists(compositionPath)) {
                try {
                    MediaFileInfo mediaFileInfo = mediaParser.parseComposition(compositionPath);
                    MediaFile getMediaFile = mediaFileRepository.findById(emptyMediaFile.getId()).orElseThrow();

                    getMediaFile.setSize(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getSize());
                    getMediaFile.setBitrate(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getBitRate());
                    getMediaFile.setDuration(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration());

                    SizeDuration sd = artifactSizeDurationMap.getOrDefault(getMediaFile.getArtifact().getId(), new SizeDuration());
                    sd.size = sd.size + mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getSize();
                    sd.duration = sd.duration + mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration();
                    artifactSizeDurationMap.put(getMediaFile.getArtifact().getId(), sd);

                    mediaFileRepository.save(getMediaFile);

                    counter.getAndIncrement();
                } catch (MediaFileInfoException e) {
                    errorHandler(ERROR_PARSING_FILE, compositionPath.toAbsolutePath().toString());
                }
            }
        }

        // update artifacts
        for (long artifactId: artifactSizeDurationMap.keySet()) {
            Artifact artifact = artifactRepository.findById(artifactId).orElseThrow();
            SizeDuration sd = artifactSizeDurationMap.get(artifactId);

            artifact.setSize(sd.size);
            artifact.setDuration(sd.duration);

            artifactRepository.save(artifact);
        }

        return counter.get();
    }

}
