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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class MP3ValidateProcessor extends AbstractFileSystemProcessor
        implements PathValidationLoader.ArtistArtifactPathLoader {
    private static final Logger logger = LoggerFactory.getLogger(MP3ValidateProcessor.class);
    public static final String MEDIA_FILE_FORMAT = "MP3";

    private final ArtifactTypeRepository artifactTypeRepository;
    private final ArtifactRepository artifactRepository;
    private final MediaFileRepository mediaFileRepository;

    public MP3ValidateProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            MediaFileRepository mediaFileRepository) {
        this.artifactTypeRepository = artifactTypeRepository;
        this.artifactRepository = artifactRepository;
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
                            long mediaFileSize;
                            try {
                                mediaFileSize = Files.size(trackPath);
                            } catch (IOException e) {
                                mediaFileSize = 0;
                            }

                            result.add(
                              new MediaFileValidationDTOBuilder()
                                      .withArtifactTitle(yt.getTitle())
                                      .withArtifactYear(yt.getYear())
                                      .withArtistName(artistPath.getFileName().toString())
                                      .withTrackNum(nt.getNumber())
                                      .withTrackTitle(nt.getTitle())
                                      .withMediaFileName(trackPath.getFileName().toString())
                                      .withMediaFileFormat(MEDIA_FILE_FORMAT)
                                      .withMediaFileSize(mediaFileSize)
                                      .build()
                            );
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
