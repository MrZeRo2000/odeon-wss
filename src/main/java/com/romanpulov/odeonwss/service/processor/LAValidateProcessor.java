package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTOBuilder;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;
import com.romanpulov.odeonwss.service.processor.utils.MediaFilesValidateUtil;
import com.romanpulov.odeonwss.service.processor.utils.TracksValidateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class LAValidateProcessor extends AbstractFileSystemProcessor implements PathValidationLoader.ArtistArtifactPathLoader {
    private static final Logger logger = LoggerFactory.getLogger(LAValidateProcessor.class);

    private ArtifactType artifactType;

    private final ArtifactTypeRepository artifactTypeRepository;
    private final ArtifactRepository artifactRepository;
    private final MediaFileRepository mediaFileRepository;

    public LAValidateProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            MediaFileRepository mediaFileRepository) {
        this.artifactTypeRepository = artifactTypeRepository;
        this.artifactRepository = artifactRepository;
        this.mediaFileRepository = mediaFileRepository;
    }

    @Override
    public void execute() throws ProcessorException {
        logger.info("Started LAValidateProcessor execution");
        Path path = validateAndGetPath();

        this.artifactType = Optional.ofNullable(this.artifactType).orElse(artifactTypeRepository.getWithLA());

        processingEventHandler(ProcessorMessages.VALIDATING_LOADING_FROM_DB);
        List<MediaFileValidationDTO> dbValidation = mediaFileRepository
                .getTrackMediaFileValidationMusic(ArtistType.ARTIST, artifactType);

        processingEventHandler(ProcessorMessages.VALIDATING_LOADING_FROM_PATH, path.toAbsolutePath());
        List<MediaFileValidationDTO> pathValidation = loadFromPath(path);

        logger.info("Validating ArtistNames");
        processingEventHandler(ProcessorMessages.VALIDATING_ARTISTS);
        if (PathValidator.validateArtistNamesArtifactsTracks(this, pathValidation, dbValidation)) {
            infoHandler(ProcessorMessages.INFO_ARTISTS_VALIDATED);

            processingEventHandler(ProcessorMessages.VALIDATING_ARTIFACTS);
            if (PathValidator.validateArtifactsMusic(this, pathValidation, dbValidation)) {
                infoHandler(ProcessorMessages.INFO_ARTIFACTS_VALIDATED);

                MediaFilesValidateUtil.validateMediaFilesAll(
                        this,
                        pathValidation,
                        dbValidation,
                        mediaFileRepository.getArtifactMediaFileValidationMusic(artifactType));

                TracksValidateUtil.validateMonotonicallyIncreasingTrackNumbers(
                        this,
                        artifactRepository,
                        List.of(ArtistType.ARTIST),
                        List.of(artifactType));
            }
        }
        logger.info("Completed LAValidateProcessor execution");
    }

    private List<MediaFileValidationDTO> loadFromPath(Path path) throws ProcessorException {
        return PathValidationLoader.loadFromPathArtistArtifacts(this, path, this);
    }

    @Override
    public void loadFromArtifactPath(Path artistPath, Path artifactPath, List<MediaFileValidationDTO> result)
            throws ProcessorException {
        loadFromArtifactPathParent(artistPath, artifactPath, null, result);
    }

    private void loadFromArtifactPathParent(Path artistPath, Path artifactPath, NamesParser.YearTitle parentYT, List<MediaFileValidationDTO> result) throws ProcessorException {
        NamesParser.YearTitle yt;
        if (parentYT == null) {
            yt = NamesParser.parseMusicArtifactTitle(artifactPath.getFileName().toString());
        } else {
            yt = parentYT;
        }
        if (yt == null) {
            errorHandler(ProcessorMessages.ERROR_PARSING_ARTIFACT_NAME, artifactPath.getFileName().toString());
            result.add(
                    new MediaFileValidationDTOBuilder()
                            .withArtistName(artistPath.getFileName().toString())
                            .build()
            );
        } else {
            List<Path> directoryFolderDisksPaths = null;
            List<Path> trackPaths = new ArrayList<>();
            if (PathReader.readPathAll(artifactPath, trackPaths)) {
                if (parentYT == null) {
                    directoryFolderDisksPaths = trackPaths
                            .stream()
                            .filter(p -> NamesParser.getDiskNumFromFolderName(p.getFileName().toString()) > 0)
                            .collect(Collectors.toList());
                }

                if (directoryFolderDisksPaths != null && directoryFolderDisksPaths.size() > 0) {
                    for (Path directoryFolderDiskPath: directoryFolderDisksPaths) {
                        loadFromArtifactPathParent(artistPath, directoryFolderDiskPath, yt, result);
                    }
                } else {
                    List<String> trackFileNames = trackPaths
                            .stream()
                            .map(f -> f.getFileName().toString())
                            .filter(f -> NamesParser.validateFileNameMediaFormat(f, this.artifactType.getMediaFileFormats()))
                            .toList();
                    if (trackFileNames.size() == 0) {
                        errorHandler(ProcessorMessages.ERROR_TRACK_FILES_NOT_FOUND_FOR_ARTIFACT, artifactPath.toString());
                    } else {
                        trackFileNames.forEach(trackFileName -> result.add(new MediaFileValidationDTO(
                                artistPath.getFileName().toString(),
                                yt.getTitle(),
                                (long) yt.getYear(),
                                null,
                                null,
                                trackFileName,
                                null
                        )));
                    }
                }
            }
        }
    }

}
