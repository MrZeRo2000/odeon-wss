package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.mapper.MediaFileMapper;
import com.romanpulov.odeonwss.repository.*;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
import com.romanpulov.odeonwss.utils.media.MediaFileInfo;
import com.romanpulov.odeonwss.utils.media.MediaFileInfoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.romanpulov.odeonwss.service.processor.ProcessorMessages.ERROR_PARSING_FILE;

@Component
public class DVMoviesLoadProcessor extends AbstractFileSystemProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DVMoviesLoadProcessor.class);

    private ArtifactType artifactType;

    private final ArtifactTypeRepository artifactTypeRepository;

    private final ArtifactRepository artifactRepository;

    private final TrackRepository trackRepository;

    private final MediaFileRepository mediaFileRepository;

    private final MediaFileMapper mediaFileMapper;

    private final DVTypeRepository dvTypeRepository;

    private final DVProductRepository dVProductRepository;

    private final MediaParser mediaParser;

    public DVMoviesLoadProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            TrackRepository trackRepository,
            MediaFileRepository mediaFileRepository,
            MediaFileMapper mediaFileMapper,
            DVTypeRepository dvTypeRepository,
            DVProductRepository dVProductRepository,
            MediaParser mediaParser)
    {
        this.artifactTypeRepository = artifactTypeRepository;
        this.artifactRepository = artifactRepository;
        this.trackRepository = trackRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.mediaFileMapper = mediaFileMapper;
        this.dvTypeRepository = dvTypeRepository;
        this.dVProductRepository = dVProductRepository;
        this.mediaParser = mediaParser;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        if (this.artifactType == null) {
             this.artifactType = artifactTypeRepository.getWithDVMovies();
        }

        infoHandler(ProcessorMessages.INFO_ARTIFACTS_LOADED, PathProcessUtil.processArtifactsPath(this, path, artifactRepository, artifactType));
        infoHandler(ProcessorMessages.INFO_TRACKS_LOADED, processTracks());
        infoHandler(ProcessorMessages.INFO_MEDIA_FILES_LOADED, processMediaFiles(path));
    }

    private int processTracks() {
        AtomicInteger counter = new AtomicInteger(0);
        DVType dvType = dvTypeRepository.getById(7L);

        this.artifactRepository.getAllByArtifactTypeWithTracks(artifactType)
                .stream()
                .filter(a -> a.getTracks().size() == 0)
                .forEach(artifact -> {
                    Track track = new Track();
                    track.setArtifact(artifact);
                    track.setDvType(dvType);
                    track.setTitle(artifact.getTitle());
                    track.setNum(1L);
                    track.setDuration(artifact.getDuration());
                    dVProductRepository.findFirstByArtifactTypeAndTitle(artifactType, track.getTitle())
                            .ifPresent(p -> track.setDvProducts(Set.of(p)));

                    trackRepository.save(track);

                    artifact.getTracks().add(track);
                    artifactRepository.save(artifact);

                    counter.getAndIncrement();
                });

        return counter.get();
    }

    private int processMediaFiles(Path path) throws ProcessorException {
        AtomicInteger counter = new AtomicInteger(0);

        for (Artifact a: artifactRepository.getAllByArtifactTypeWithTracks(artifactType)) {
            Path mediaFilesRootPath = Paths.get(path.toAbsolutePath().toString(), a.getTitle());

            List<Path> mediaFilesPaths = new ArrayList<>();
            if (!PathReader.readPathFilesOnly(this, mediaFilesRootPath, mediaFilesPaths)) {
                return counter.get();
            }

            Set<MediaFile> mediaFiles = new HashSet<>();
            for (Path mediaFilePath: mediaFilesPaths) {
                String fileName = mediaFilePath.getFileName().toString();
                MediaFile mediaFile = null;

                if (mediaFileRepository.findFirstByArtifactAndName(a, fileName).isEmpty()) {
                    try {
                        MediaFileInfo mediaFileInfo = mediaParser.parseTrack(mediaFilePath);

                        mediaFile = mediaFileMapper.fromMediaFileInfo(mediaFileInfo);
                        mediaFile.setArtifact(a);
                        mediaFile.setName(fileName);

                        mediaFileRepository.save(mediaFile);
                        counter.getAndIncrement();

                    } catch (MediaFileInfoException e) {
                        errorHandler(ERROR_PARSING_FILE, mediaFilePath.toAbsolutePath().toString());
                    }
                } else {
                    mediaFile = mediaFileRepository.findFirstByArtifactAndName(a, fileName).get();
                }
                if (mediaFile != null) {
                    mediaFiles.add(mediaFile);
                }
            }

            if (a.getTracks().size() == 1) {
                Track track = trackRepository
                        .findByIdWithMediaFiles(a.getTracks().get(0).getId()).orElseThrow();
                if (!track.getMediaFiles().equals(mediaFiles)) {
                    track.setMediaFiles(mediaFiles);

                    trackRepository.save(track);
                }
            }

            long totalSize = mediaFiles.stream().collect(Collectors.summarizingLong(MediaFile::getSize)).getSum();
            long totalDuration = mediaFiles.stream().collect(Collectors.summarizingLong(MediaFile::getDuration)).getSum();

            if (
                    ValueValidator.isEmpty(a.getSize()) ||
                    ValueValidator.isEmpty(a.getDuration())) {
                a.setSize(totalSize);
                a.setDuration(totalDuration);

                artifactRepository.save(a);
            }

            if (
                    (a.getTracks().size() == 1) &&
                    ValueValidator.isEmpty(a.getTracks().get(0).getDuration())) {
                a.getTracks().get(0).setDuration(totalDuration);

                trackRepository.save(a.getTracks().get(0));
            }
        }

        return counter.get();
    }
}
