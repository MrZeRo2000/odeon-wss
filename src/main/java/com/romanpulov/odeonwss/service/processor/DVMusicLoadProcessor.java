package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.IdNameDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.mapper.MediaFileMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;
import com.romanpulov.odeonwss.service.processor.utils.MediaFilesProcessUtil;
import com.romanpulov.odeonwss.service.processor.utils.PathProcessUtil;
import com.romanpulov.odeonwss.service.processor.vo.SizeDuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class DVMusicLoadProcessor extends AbstractFileSystemProcessor {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(DVMusicLoadProcessor.class);

    private ArtifactType artifactType;

    private final ArtistRepository artistRepository;
    private final ArtifactTypeRepository artifactTypeRepository;
    private final ArtifactRepository artifactRepository;
    private final MediaFileRepository mediaFileRepository;

    private final MediaFileMapper mediaFileMapper;

    private final MediaParser mediaParser;

    public DVMusicLoadProcessor(
            ArtistRepository artistRepository,
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            MediaFileRepository mediaFileRepository,
            MediaFileMapper mediaFileMapper,
            MediaParser mediaParser) {
        this.artistRepository = artistRepository;
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

        Map<String, Long> artists = artistRepository
                .getByTypeOrderByName(ArtistType.ARTIST)
                .stream()
                .collect(Collectors.toMap(IdNameDTO::getName, IdNameDTO::getId));

        infoHandler(ProcessorMessages.INFO_ARTIFACTS_LOADED,
                PathProcessUtil.processArtifactsPath(
                        this,
                        path,
                        artists,
                        artifactRepository,
                        artifactType,
                        s -> processingEventHandler(ProcessorMessages.PROCESSING_ARTIFACT, s),
                        s -> errorHandler(ProcessorMessages.ERROR_EXPECTED_DIRECTORY, s)));
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

            Set<MediaFile> mediaFiles = MediaFilesProcessUtil.loadFromMediaFilesPaths(
                    mediaFilesPaths,
                    a,
                    mediaParser,
                    mediaFileRepository,
                    mediaFileMapper,
                    counter,
                    p -> processingEventHandler(ProcessorMessages.PROCESSING_PARSING_MEDIA_FILE, p),
                    p -> errorHandler(ProcessorMessages.ERROR_PARSING_FILE, p));

            SizeDuration sizeDuration = MediaFilesProcessUtil.getMediaFilesSizeDuration(mediaFiles);

            if (ValueValidator.isEmpty(a.getSize()) || ValueValidator.isEmpty(a.getDuration())) {
                a.setSize(sizeDuration.getSize());
                a.setDuration(sizeDuration.getDuration());

                artifactRepository.save(a);
            }
        }

        return counter.get();
    }

}
