package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.IdTitleDTO;
import com.romanpulov.odeonwss.dto.MediaFileDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.mapper.MediaFileMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
import com.romanpulov.odeonwss.service.processor.vo.SizeDuration;
import com.romanpulov.odeonwss.utils.media.model.MediaFileInfo;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.romanpulov.odeonwss.service.processor.ProcessorMessages.INFO_MEDIA_FILES_LOADED;

public abstract class AbstractDVMediaFilesLoadProcessor extends AbstractFileSystemProcessor {
    private final ArtifactTypeRepository artifactTypeRepository;
    private final ArtifactRepository artifactRepository;
    private final TrackRepository trackRepository;
    private final MediaFileRepository mediaFileRepository;
    private final MediaFileMapper mediaFileMapper;
    private final MediaParser mediaParser;
    private final Function<ArtifactTypeRepository, ArtifactType> artifactTypeSupplier;

    public AbstractDVMediaFilesLoadProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            TrackRepository trackRepository,
            MediaFileRepository mediaFileRepository,
            MediaFileMapper mediaFileMapper,
            MediaParser mediaParser,
            Function<ArtifactTypeRepository, ArtifactType> artifactTypeSupplier
    ) {
        this.artifactTypeRepository = artifactTypeRepository;
        this.artifactRepository = artifactRepository;
        this.trackRepository = trackRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.mediaFileMapper = mediaFileMapper;
        this.mediaParser = mediaParser;
        this.artifactTypeSupplier = artifactTypeSupplier;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        infoHandler(INFO_MEDIA_FILES_LOADED, processArtifactsPath(path));
    }

    private Set<Long> getArtifactIdsToProcess(Path path) {
        Set<IdTitleDTO> incompleteArtifacts = artifactRepository.findArtifactIdTitleWithIncompleteMediaFilesByArtifactType(
                this.artifactTypeSupplier.apply(this.artifactTypeRepository));

        List<MediaFileDTO> mediaFiles = mediaFileRepository.findAllDTOByArtifactType(
                this.artifactTypeSupplier.apply(this.artifactTypeRepository));

        String rootMediaPath = path.toAbsolutePath().toString();

        Set<Long> incorrectSizeArtifactIds = mediaFiles
                .parallelStream()
                .filter(m -> {
                    Path mediaPath = Paths.get(rootMediaPath, m.getArtifactTitle(), m.getName());
                    synchronizedProcessingEventHandler(ProcessorMessages.PROCESSING_CHECKING_MEDIA_FILE, m.getName());
                    try {
                        return (m.getSize() == null) || (Files.exists(mediaPath) && Files.size(mediaPath) != m.getSize());
                        //return Files.exists(mediaPath) && Files.size(mediaPath)
                    } catch (IOException e) {
                        return false;
                    }
                })
                .map(MediaFileDTO::getArtifactId)
                .collect(Collectors.toSet());

        Set<Long> result = incompleteArtifacts.stream().map(IdTitleDTO::getId).collect(Collectors.toSet());
        result.addAll(incorrectSizeArtifactIds);

        return result;
    }

    public int processArtifactsPath(Path path) throws ProcessorException {
        AtomicInteger counter = new AtomicInteger(0);
        Map<Artifact, SizeDuration> artifactSizeDurationMap = new HashMap<>();
        String rootMediaPath = path.toAbsolutePath().toString();

        Set<Long> artifactIds = getArtifactIdsToProcess(path);
        for (long artifactId: artifactIds) {
            List<MediaFile> mediaFiles = mediaFileRepository.findAllByArtifactId(artifactId);
            Artifact artifact = artifactRepository.findById(artifactId).orElseThrow();

            Map<Path, MediaFile> mediaFilePathMap = mediaFiles
                    .stream()
                    .map(m -> Pair.of(Paths.get(rootMediaPath, artifact.getTitle(), m.getName()), m))
                    .filter(p -> Files.exists(p.getFirst()))
                    .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));

            Map<Path, MediaFileInfo> parsedMediaFileInfo = mediaParser.parseTracks(
                    mediaFilePathMap.keySet(),
                    (parsing -> synchronizedProcessingEventHandler(
                            ProcessorMessages.PROCESSING_PARSING_MEDIA_FILE, parsing)),
                    (parsed -> synchronizedProcessingEventHandler(
                            ProcessorMessages.PROCESSING_PARSED_MEDIA_FILE, parsed)));

            for (Map.Entry<Path, MediaFile> e: mediaFilePathMap.entrySet()) {
                MediaFile mediaFile = e.getValue();
                MediaFileInfo mediaFileInfo = parsedMediaFileInfo.get(e.getKey());

                mediaFileMapper.updateFromMediaFileInfo(mediaFile, mediaFileInfo);

                SizeDuration sd = artifactSizeDurationMap.getOrDefault(artifact, new SizeDuration());
                sd.setSize(sd.getSize() + mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getSize());
                sd.setDuration(sd.getDuration() + mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration());
                artifactSizeDurationMap.put(artifact, sd);

                mediaFileRepository.save(mediaFile);

                counter.getAndIncrement();
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
