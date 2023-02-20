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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class LAValidateProcessor extends AbstractFileSystemProcessor implements PathLoader.ArtistArtifactPathLoader {
    private static final Set<String> MEDIA_FORMATS = Set.of("ape", "flac", "wv");

    private static final Logger logger = LoggerFactory.getLogger(LAValidateProcessor.class);
    public static final ArtifactType ARTIFACT_TYPE = ArtifactType.withLA();

    private final MediaFileRepository mediaFileRepository;

    public LAValidateProcessor(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    @Override
    public void execute() throws ProcessorException {
        logger.info("Started LAValidateProcessor execution");
        Path path = validateAndGetPath();

        List<MediaFileValidationDTO> dbValidation = mediaFileRepository
                .getCompositionMediaFileValidationMusic(ArtistType.ARTIST, ARTIFACT_TYPE);
        List<MediaFileValidationDTO> pathValidation = loadFromPath(path);

        logger.info("Validating ArtistNames");
        if (PathValidator.validateArtistNamesArtifactsCompositions(this, pathValidation, dbValidation)) {
            infoHandler(ProcessorMessages.INFO_ARTISTS_VALIDATED);

            if (PathValidator.validateArtifactsMusic(this, pathValidation, dbValidation)) {
                infoHandler(ProcessorMessages.INFO_ARTIFACTS_VALIDATED);

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
        logger.info("Completed LAValidateProcessor execution");
    }

    private List<MediaFileValidationDTO> loadFromPath(Path path) throws ProcessorException {
        return PathLoader.loadFromPathArtistArtifacts(this, path, this);
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
            if (parentYT == null) {
                try (Stream<Path> compositionPathStream = Files.list(artifactPath)) {
                    directoryFolderDisksPaths = compositionPathStream
                            .filter(p -> NamesParser.getDiskNumFromFolderName(p.getFileName().toString()) > 0)
                            .collect(Collectors.toList());
                } catch (IOException e) {
                    throw new ProcessorException(ProcessorMessages.ERROR_EXCEPTION, e.getMessage());
                }
            }

            if (directoryFolderDisksPaths != null && directoryFolderDisksPaths.size() > 0) {
                for (Path directoryFolderDiskPath: directoryFolderDisksPaths) {
                    loadFromArtifactPathParent(artistPath, directoryFolderDiskPath, yt, result);
                }
            } else {
                try (Stream<Path> compositionPathStream = Files.list(artifactPath)) {
                    List<String> compositionFileNames = compositionPathStream
                            .map(f -> f.getFileName().toString())
                            .filter(f -> f.contains("."))
                            .filter(f -> MEDIA_FORMATS.contains(f.substring(f.lastIndexOf(".") + 1).toLowerCase()))
                            .collect(Collectors.toList());
                    if (compositionFileNames.size() == 0) {
                        errorHandler(ProcessorMessages.ERROR_COMPOSITION_FILES_NOT_FOUND_FOR_ARTIFACT, artifactPath.getFileName().toString());
                    } else {
                        compositionFileNames.forEach(compositionFileName -> result.add(new MediaFileValidationDTO(
                                artistPath.getFileName().toString(),
                                yt.getTitle(),
                                (long) yt.getYear(),
                                null,
                                null,
                                compositionFileName,
                                null
                        )));
                    }
                }  catch (IOException e) {
                    throw new ProcessorException(ProcessorMessages.ERROR_EXCEPTION, e.getMessage());
                }
            }
        }
    }

}
