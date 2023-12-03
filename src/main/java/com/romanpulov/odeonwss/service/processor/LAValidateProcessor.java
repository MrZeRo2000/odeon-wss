package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTOBuilder;
import com.romanpulov.odeonwss.dto.TrackFlatDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;
import com.romanpulov.odeonwss.service.processor.utils.MediaFilesValidateUtil;
import com.romanpulov.odeonwss.service.processor.utils.TracksValidateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class LAValidateProcessor extends AbstractFileSystemProcessor implements PathValidationLoader.ArtistArtifactPathLoader {
    private static final Logger logger = LoggerFactory.getLogger(LAValidateProcessor.class);

    private final ArtistType artistType = ArtistType.ARTIST;
    private ArtifactType artifactType;

    private final ArtifactTypeRepository artifactTypeRepository;
    private final ArtifactRepository artifactRepository;
    private final MediaFileRepository mediaFileRepository;
    private final TrackRepository trackRepository;

    public LAValidateProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            MediaFileRepository mediaFileRepository,
            TrackRepository trackRepository) {
        this.artifactTypeRepository = artifactTypeRepository;
        this.artifactRepository = artifactRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.trackRepository = trackRepository;
    }

    @Override
    public void execute() throws ProcessorException {
        logger.info("Started LAValidateProcessor execution");
        Path path = validateAndGetPath();

        this.artifactType = Optional.ofNullable(this.artifactType).orElse(artifactTypeRepository.getWithLA());

        processingEventHandler(ProcessorMessages.VALIDATING_LOADING_FROM_DB);
        List<MediaFileValidationDTO> dbValidation = mediaFileRepository
                .getTrackMediaFileValidationMusic(artistType, artifactType);

        processingEventHandler(ProcessorMessages.VALIDATING_LOADING_FROM_PATH, path.toAbsolutePath());
        List<MediaFileValidationDTO> pathValidation = loadFromPath(path);

        logger.info("Validating ArtistNames");
        processingEventHandler(ProcessorMessages.VALIDATING_ARTISTS);
        if (MediaFileValidator.validateArtistNamesArtifactsTracks(this, pathValidation, dbValidation)) {
            infoHandler(ProcessorMessages.INFO_ARTISTS_VALIDATED);

            processingEventHandler(ProcessorMessages.VALIDATING_ARTIFACTS);
            if (MediaFileValidator.validateArtifactsMusic(this, pathValidation, dbValidation)) {
                infoHandler(ProcessorMessages.INFO_ARTIFACTS_VALIDATED);

                MediaFilesValidateUtil.validateMediaFilesMusicAll(
                        this,
                        pathValidation,
                        dbValidation,
                        mediaFileRepository.getArtifactMediaFileValidationMusic(artistType, artifactType));

                TracksValidateUtil.validateMonotonicallyIncreasingTrackNumbers(
                        this,
                        artifactRepository,
                        List.of(artistType),
                        List.of(artifactType));

                List<TrackFlatDTO> tracks = trackRepository.findAllFlatDTOByArtifactTypeId(
                        artistType, artifactType.getId());

                TracksValidateUtil.validateTracksDuration(
                        this,
                        TrackValidator.ARTIFACT_MUSIC_MAPPER,
                        tracks);
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

                if (directoryFolderDisksPaths != null && !directoryFolderDisksPaths.isEmpty()) {
                    for (Path directoryFolderDiskPath: directoryFolderDisksPaths) {
                        loadFromArtifactPathParent(artistPath, directoryFolderDiskPath, yt, result);
                    }
                } else {
                    List<Path> mediaPaths = trackPaths
                            .stream()
                            .filter(f -> NamesParser.validateFileNameMediaFormat(
                                    f.getFileName().toString(), this.artifactType.getMediaFileFormats()))
                            .toList();
                    if (mediaPaths.isEmpty()) {
                        errorHandler(ProcessorMessages.ERROR_TRACK_FILES_NOT_FOUND_FOR_ARTIFACT, artifactPath.toString());
                    } else {
                        mediaPaths.forEach(mediaPath -> {
                            long mediaFileSize;
                            try {
                                mediaFileSize = Files.size(mediaPath);
                            } catch (IOException e) {
                                mediaFileSize = 0;
                            }

                            result.add(
                                    new MediaFileValidationDTOBuilder()
                                            .withArtistName(artistPath.getFileName().toString())
                                            .withArtifactTitle(yt.getTitle())
                                            .withArtifactYear(yt.getYear())
                                            .withMediaFileName(mediaPath.getFileName().toString())
                                            .withMediaFileSize(mediaFileSize)
                                            .build());
                            }
                        );
                    }
                }
            }
        }
    }

}
