package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTOBuilder;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class MP3ValidateProcessor extends AbstractFileSystemProcessor
        implements PathValidationLoader.ArtistArtifactPathLoader {
    private static final Logger logger = LoggerFactory.getLogger(MP3ValidateProcessor.class);
    public static final String MEDIA_FILE_FORMAT = "MP3";

    private final ArtifactTypeRepository artifactTypeRepository;

    private final MediaFileRepository mediaFileRepository;

    public MP3ValidateProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            MediaFileRepository mediaFileRepository) {
        this.artifactTypeRepository = artifactTypeRepository;
        this.mediaFileRepository = mediaFileRepository;
    }

    @Override
    public void execute() throws ProcessorException {
        ArtifactType artifactType = artifactTypeRepository.getWithMP3();

        Path path = validateAndGetPath();

        processingEventHandler(ProcessorMessages.VALIDATING_LOADING_FROM_PATH, path.toAbsolutePath());
        List<MediaFileValidationDTO> pathValidation =
                PathValidationLoader.loadFromPathArtistArtifacts(this, path, this);

        processingEventHandler(ProcessorMessages.VALIDATING_LOADING_FROM_DB);
        List<MediaFileValidationDTO> dbValidation = mediaFileRepository
                .getTrackMediaFileValidationMusic(ArtistType.ARTIST, artifactType);

        processingEventHandler(ProcessorMessages.VALIDATING_ARTISTS);
        if (PathValidator.validateArtistNamesArtifactsTracks(this, pathValidation, dbValidation)) {
            infoHandler(ProcessorMessages.INFO_ARTISTS_VALIDATED);

            processingEventHandler(ProcessorMessages.VALIDATING_ARTIFACTS);
            if (PathValidator.validateArtifactsMusic(this, pathValidation, dbValidation)) {
                infoHandler(ProcessorMessages.INFO_ARTIFACTS_VALIDATED);

                processingEventHandler(ProcessorMessages.VALIDATING_TRACKS);
                if (PathValidator.validateTracksMusic(this, pathValidation, dbValidation)) {
                    infoHandler(ProcessorMessages.INFO_TRACKS_VALIDATED);
                }

                processingEventHandler(ProcessorMessages.VALIDATING_MEDIA_FILES);
                if (PathValidator.validateMediaFilesMusic(this, pathValidation, dbValidation)) {
                    infoHandler(ProcessorMessages.INFO_MEDIA_FILES_VALIDATED);
                }

                processingEventHandler(ProcessorMessages.VALIDATING_ARTIFACT_MEDIA_FILES);
                List<MediaFileValidationDTO> dbArtifactValidation = mediaFileRepository
                        .getArtifactMediaFileValidationMusic(artifactType);
                if (PathValidator.validateArtifactMediaFilesMusic(this, pathValidation, dbArtifactValidation)) {
                    infoHandler(ProcessorMessages.INFO_ARTIFACT_MEDIA_FILES_VALIDATED);
                }
            }
        }
    }

    @Override
    public void loadFromArtifactPath(Path artistPath, Path artifactPath, List<MediaFileValidationDTO> result)
            throws ProcessorException {
        NamesParser.YearTitle yt = NamesParser.parseMusicArtifactTitle(artifactPath.getFileName().toString());
        if (yt == null) {
            errorHandlerItem(
                    ProcessorMessages.ERROR_PARSING_ARTIFACT_NAME,
                    String.format(PathValidator.DELIMITER_FORMAT, artistPath.getFileName().toString(),
                    artifactPath.getFileName().toString()));
            result.add(
                    new MediaFileValidationDTOBuilder()
                            .withArtistName(artistPath.getFileName().toString())
                            .build()
            );
        } else {
            int oldResultSize = result.size();

            List<Path> trackPaths = new ArrayList<>();
            if (PathReader.readPathFilesOnly(this, artifactPath, trackPaths)) {
                for (Path trackPath: trackPaths) {
                    String trackFileName = trackPath.getFileName().toString();

                    if (!trackFileName.toUpperCase().endsWith(MEDIA_FILE_FORMAT)) {
                        errorHandler(ProcessorMessages.ERROR_WRONG_FILE_TYPE, trackPath.toAbsolutePath());
                    } else {
                        NamesParser.NumberTitle nt = NamesParser.parseMusicTrack(trackFileName);
                        if (nt == null) {
                            errorHandler(
                                    ProcessorMessages.ERROR_PARSING_MUSIC_TRACK_NAME,
                                    trackPath.toAbsolutePath().toString()

                            );
                            result.add(
                                    new MediaFileValidationDTOBuilder()
                                            .withArtistName(artistPath.getFileName().toString())
                                            .withArtifactTitle(yt.getTitle())
                                            .withArtifactYear(yt.getYear())
                                            .build()
                            );
                        } else {
                            result.add(new MediaFileValidationDTO(
                                    artistPath.getFileName().toString(),
                                    yt.getTitle(),
                                    (long)yt.getYear(),
                                    nt.getNumber(),
                                    nt.getTitle(),
                                    trackPath.getFileName().toString(),
                                    MEDIA_FILE_FORMAT
                            ));
                        }
                    }
                }
            }

            if (result.size() == oldResultSize) {
                errorHandler(ProcessorMessages.ERROR_TRACKS_NOT_FOUND_FOR_ARTIFACT, artifactPath.getFileName().toString());
            }
        }
    }

}
