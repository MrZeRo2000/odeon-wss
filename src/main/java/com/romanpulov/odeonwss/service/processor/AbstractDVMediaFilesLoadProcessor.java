package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
import com.romanpulov.odeonwss.service.processor.vo.SizeDuration;
import com.romanpulov.odeonwss.utils.media.MediaFileInfo;
import com.romanpulov.odeonwss.utils.media.MediaFileInfoException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static com.romanpulov.odeonwss.service.processor.ProcessorMessages.ERROR_PARSING_FILE;
import static com.romanpulov.odeonwss.service.processor.ProcessorMessages.INFO_MEDIA_FILES_LOADED;

public abstract class AbstractDVMediaFilesLoadProcessor extends AbstractFileSystemProcessor {
    private final ArtifactTypeRepository artifactTypeRepository;

    private final ArtifactRepository artifactRepository;

    private final TrackRepository trackRepository;

    private final MediaFileRepository mediaFileRepository;

    private final MediaParser mediaParser;

    private final Function<ArtifactTypeRepository, ArtifactType> artifactTypeSupplier;

    public AbstractDVMediaFilesLoadProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            TrackRepository trackRepository,
            MediaFileRepository mediaFileRepository,
            MediaParser mediaParser,
            Function<ArtifactTypeRepository, ArtifactType> artifactTypeSupplier
    ) {
        this.artifactTypeRepository = artifactTypeRepository;
        this.artifactRepository = artifactRepository;
        this.trackRepository = trackRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.mediaParser = mediaParser;
        this.artifactTypeSupplier = artifactTypeSupplier;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        infoHandler(INFO_MEDIA_FILES_LOADED, processArtifactsPath(path));
    }

    public int processArtifactsPath(Path path) {
        AtomicInteger counter = new AtomicInteger(0);

        List<MediaFile> emptyMediaFiles = mediaFileRepository.getMediaFilesWithEmptySizeByArtifactType(
                this.artifactTypeSupplier.apply(this.artifactTypeRepository));
        Map<Artifact, SizeDuration> artifactSizeDurationMap = new HashMap<>();

        String rootMediaPath = path.toAbsolutePath().toString();

        // update media files
        for (MediaFile emptyMediaFile: emptyMediaFiles) {
            Path trackPath = Paths.get(rootMediaPath, emptyMediaFile.getArtifact().getTitle(), emptyMediaFile.getName());
            if (Files.exists(trackPath)) {
                try {
                    MediaFileInfo mediaFileInfo = mediaParser.parseTrack(trackPath);
                    //MediaFile getMediaFile = mediaFileRepository.findById(emptyMediaFile.getId()).orElseThrow();

                    emptyMediaFile.setSize(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getSize());
                    emptyMediaFile.setBitrate(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getBitRate());
                    emptyMediaFile.setDuration(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration());

                    SizeDuration sd = artifactSizeDurationMap.getOrDefault(emptyMediaFile.getArtifact(), new SizeDuration());
                    sd.setSize(sd.getSize() + mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getSize());
                    sd.setDuration(sd.getDuration() + mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration());
                    artifactSizeDurationMap.put(emptyMediaFile.getArtifact(), sd);

                    mediaFileRepository.save(emptyMediaFile);

                    counter.getAndIncrement();
                } catch (MediaFileInfoException e) {
                    errorHandler(ERROR_PARSING_FILE, trackPath.toAbsolutePath().toString());
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

            artifact.setSize(sd.getSize());
            artifact.setDuration(sd.getDuration());

            artifactRepository.save(artifact);
        }
    }

    protected void updateTracksDuration(Map<Artifact, SizeDuration> artifactSizeDurationMap) {
        artifactSizeDurationMap.keySet().forEach(artifact -> {
            List<Track> tracks =
                    artifactRepository.getByIdsWithTracks(artifact.getId()).orElseThrow().getTracks();
            if (tracks.size() == 1) {
                Track track = tracks.get(0);
                track.setDuration(artifact.getDuration());

                trackRepository.save(track);
            }
        });
    }
}
