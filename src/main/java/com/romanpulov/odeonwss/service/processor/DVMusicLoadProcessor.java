package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.mapper.MediaFileMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;
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
public class DVMusicLoadProcessor extends AbstractFileSystemProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DVMusicLoadProcessor.class);

    private ArtifactType artifactType;

    private final ArtifactTypeRepository artifactTypeRepository;

    private final ArtifactRepository artifactRepository;

    private final MediaFileRepository mediaFileRepository;

    private final MediaFileMapper mediaFileMapper;

    private final MediaParser mediaParser;

    public DVMusicLoadProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            MediaFileRepository mediaFileRepository,
            MediaFileMapper mediaFileMapper,
            MediaParser mediaParser) {
        this.artifactTypeRepository = artifactTypeRepository;
        this.artifactRepository = artifactRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.mediaFileMapper = mediaFileMapper;
        this.mediaParser = mediaParser;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        this.artifactType = Optional.ofNullable(this.artifactType).orElse(artifactTypeRepository.getWithDVMusic());

        infoHandler(ProcessorMessages.INFO_ARTIFACTS_LOADED,
                PathProcessUtil.processArtifactsPath(this, path, artifactRepository, artifactType));
        infoHandler(ProcessorMessages.INFO_MEDIA_FILES_LOADED, processMediaFiles(path));
    }

    private int processMediaFiles(Path path) throws ProcessorException {
        AtomicInteger counter = new AtomicInteger(0);

        for (Artifact a: artifactRepository.getAllByArtifactTypeWithTracks(artifactType)) {
            Path mediaFilesRootPath = Paths.get(path.toAbsolutePath().toString(), a.getTitle());

            List<Path> mediaFilesPaths = new ArrayList<>();
            if (!PathReader.readPathPredicateFilesOnly(
                    this,
                    mediaFilesRootPath,
                    p -> NamesParser.validateFileNameMediaFormat(
                            p.getFileName().toString(),
                            this.artifactType.getMediaFileFormats()),
                    mediaFilesPaths)) {
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

            long totalSize = mediaFiles.stream().collect(Collectors.summarizingLong(MediaFile::getSize)).getSum();
            long totalDuration = mediaFiles.stream().collect(Collectors.summarizingLong(MediaFile::getDuration)).getSum();

            if (ValueValidator.isEmpty(a.getSize()) || ValueValidator.isEmpty(a.getDuration())) {
                a.setSize(totalSize);
                a.setDuration(totalDuration);

                artifactRepository.save(a);
            }
        }

        return counter.get();
    }

}
