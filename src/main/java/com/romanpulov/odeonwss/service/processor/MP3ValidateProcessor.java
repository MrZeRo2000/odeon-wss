package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTOBuilder;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.ArtistType;
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
        implements PathLoader.ArtistArtifactPathLoader {
    private static final Logger logger = LoggerFactory.getLogger(MP3ValidateProcessor.class);
    public static final ArtifactType ARTIFACT_TYPE = ArtifactType.withMP3();
    public static final String MEDIA_FILE_FORMAT = "mp3";

    private final MediaFileRepository mediaFileRepository;

    public MP3ValidateProcessor(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        List<MediaFileValidationDTO> pathValidation =
                PathLoader.loadFromPathArtistArtifacts(this, path, this);
        List<MediaFileValidationDTO> dbValidation = mediaFileRepository
                .getCompositionMediaFileValidationMusic(ArtistType.ARTIST, ARTIFACT_TYPE);

        if (PathValidator.validateArtistNamesArtifactsCompositions(this, pathValidation, dbValidation)) {
            infoHandler(ProcessorMessages.INFO_ARTISTS_VALIDATED);

            if (PathValidator.validateArtifactsMusic(this, pathValidation, dbValidation)) {
                infoHandler(ProcessorMessages.INFO_ARTIFACTS_VALIDATED);

                if (PathValidator.validateCompositionsMusic(this, pathValidation, dbValidation)) {
                    infoHandler(ProcessorMessages.INFO_COMPOSITIONS_VALIDATED);
                }

                if (PathValidator.validateMediaFilesMusic(this, pathValidation, dbValidation)) {
                    infoHandler(ProcessorMessages.INFO_MEDIA_FILES_VALIDATED);
                }

                List<MediaFileValidationDTO> dbArtifactValidation = mediaFileRepository
                        .getArtifactMediaFileValidationMusic(ARTIFACT_TYPE);

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
            errorHandler(ProcessorMessages.ERROR_PARSING_ARTIFACT_NAME, artifactPath.getFileName().toString());
            result.add(
                    new MediaFileValidationDTOBuilder()
                            .withArtistName(artistPath.getFileName().toString())
                            .build()
            );
        } else {
            int oldResultSize = result.size();

            List<Path> compositionPaths = new ArrayList<>();
            if (PathReader.readPathFilesOnly(this, artifactPath, compositionPaths)) {
                for (Path compositionPath: compositionPaths) {
                    String compositionFileName = compositionPath.getFileName().toString();

                    if (!compositionFileName.endsWith(MEDIA_FILE_FORMAT)) {
                        errorHandler(ProcessorMessages.ERROR_WRONG_FILE_TYPE, compositionPath.toAbsolutePath());
                    } else {
                        NamesParser.NumberTitle nt = NamesParser.parseMusicComposition(compositionFileName);
                        if (nt == null) {
                            errorHandler(ProcessorMessages.ERROR_PARSING_COMPOSITION_NAME, compositionPath.toAbsolutePath().getFileName());
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
                                    compositionPath.getFileName().toString(),
                                    MEDIA_FILE_FORMAT
                            ));
                        }
                    }
                }
            }

            if (result.size() == oldResultSize) {
                errorHandler(ProcessorMessages.ERROR_COMPOSITIONS_NOT_FOUND_FOR_ARTIFACT, artifactPath.getFileName().toString());
            }
        }
    }

}
