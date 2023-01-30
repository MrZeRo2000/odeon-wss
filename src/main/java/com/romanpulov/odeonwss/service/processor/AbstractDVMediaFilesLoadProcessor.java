package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Composition;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
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

public abstract class AbstractDVMediaFilesLoadProcessor extends AbstractFileSystemProcessor {
    private final ArtifactType artifactType;

    private final ArtifactRepository artifactRepository;

    private final CompositionRepository compositionRepository;

    private final MediaFileRepository mediaFileRepository;

    private final MediaParser mediaParser;

    public AbstractDVMediaFilesLoadProcessor(
            ArtifactType artifactType,
            ArtifactRepository artifactRepository,
            CompositionRepository compositionRepository,
            MediaFileRepository mediaFileRepository,
            MediaParser mediaParser
    ) {
        this.artifactType = artifactType;
        this.artifactRepository = artifactRepository;
        this.compositionRepository = compositionRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.mediaParser = mediaParser;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        infoHandler(INFO_MEDIA_FILES_LOADED, processArtifactsPath(path));
    }

    static class SizeDuration {
        long size;
        long duration;

        public SizeDuration(long size, long duration) {
            this.size = size;
            this.duration = duration;
        }

        public SizeDuration() {
        }
    }

    public int processArtifactsPath(Path path) {
        AtomicInteger counter = new AtomicInteger(0);

        List<MediaFile> emptyMediaFiles = mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(artifactType);
        Map<Artifact, SizeDuration> artifactSizeDurationMap = new HashMap<>();

        String rootMediaPath = path.toAbsolutePath().toString();

        // update media files
        for (MediaFile emptyMediaFile: emptyMediaFiles) {
            Path compositionPath = Paths.get(rootMediaPath, emptyMediaFile.getArtifact().getTitle(), emptyMediaFile.getName());
            if (Files.exists(compositionPath)) {
                try {
                    MediaFileInfo mediaFileInfo = mediaParser.parseComposition(compositionPath);
                    //MediaFile getMediaFile = mediaFileRepository.findById(emptyMediaFile.getId()).orElseThrow();

                    emptyMediaFile.setSize(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getSize());
                    emptyMediaFile.setBitrate(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getBitRate());
                    emptyMediaFile.setDuration(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration());

                    SizeDuration sd = artifactSizeDurationMap.getOrDefault(emptyMediaFile.getArtifact(), new SizeDuration());
                    sd.size = sd.size + mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getSize();
                    sd.duration = sd.duration + mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration();
                    artifactSizeDurationMap.put(emptyMediaFile.getArtifact(), sd);

                    mediaFileRepository.save(emptyMediaFile);

                    counter.getAndIncrement();
                } catch (MediaFileInfoException e) {
                    errorHandler(ERROR_PARSING_FILE, compositionPath.toAbsolutePath().toString());
                }
            }
        }

        processArtifactSizeDuration(artifactSizeDurationMap);

        return counter.get();
    }

    protected abstract void processArtifactSizeDuration(Map<Artifact, SizeDuration> artifactSizeDurationMap);

    protected void updateArtifactsDuration(Map<Artifact, SizeDuration> artifactSizeDurationMap) {
        for (Artifact artifact: artifactSizeDurationMap.keySet()) {
            SizeDuration sd = artifactSizeDurationMap.get(artifact);

            artifact.setSize(sd.size);
            artifact.setDuration(sd.duration);

            artifactRepository.save(artifact);
        }
    }

    protected void updateCompositionsDuration(Map<Artifact, SizeDuration> artifactSizeDurationMap) {
        artifactSizeDurationMap.keySet().forEach(artifact -> {
            List<Composition> compositions =
                    artifactRepository.getByIdsWithCompositions(artifact.getId()).orElseThrow().getCompositions();
            if (compositions.size() == 1) {
                Composition composition = compositions.get(0);
                composition.setDuration(artifact.getDuration());

                compositionRepository.save(composition);
            }
        });
    }
}
