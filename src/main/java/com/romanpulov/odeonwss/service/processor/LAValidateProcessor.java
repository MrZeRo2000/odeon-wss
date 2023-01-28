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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class LAValidateProcessor extends AbstractArtistBaseValidateProcessor {

    private static final Logger logger = LoggerFactory.getLogger(LAValidateProcessor.class);

    private final MediaFileRepository mediaFileRepository;

    private Set<String> mediaFormats;

    public LAValidateProcessor(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        List<MediaFileValidationDTO> dbValidation = mediaFileRepository.getCompositionMediaFileValidationMusic(ArtistType.ARTIST, ArtifactType.withLA());
        mediaFormats = dbValidation.stream().map(MediaFileValidationDTO::getMediaFileFormat).collect(Collectors.toSet());

        List<MediaFileValidationDTO> pathValidation = loadFromPath(path);

        if (validateArtistNames(pathValidation, dbValidation)) {
            infoHandler(ProcessorMessages.INFO_ARTISTS_VALIDATED);

            if (validateArtifacts(pathValidation, dbValidation)) {
                infoHandler(ProcessorMessages.INFO_ARTIFACTS_VALIDATED);

                if (validateCompositions(pathValidation, dbValidation)) {
                    infoHandler(ProcessorMessages.INFO_COMPOSITIONS_VALIDATED);
                }

                dbValidation = mediaFileRepository.getMediaFileValidationMusic(ArtifactType.withLA());

                if (validateMediaFiles(pathValidation, dbValidation)) {
                    infoHandler(ProcessorMessages.INFO_MEDIA_FILES_VALIDATED);
                }
            }
        }
    }

    protected void loadFromArtistPath(Path artistPath, List<MediaFileValidationDTO> result)
            throws ProcessorException {
        try (Stream<Path> artifactPathStream = Files.list(artistPath)) {
            for (Path artifactPath: artifactPathStream.collect(Collectors.toList())) {
                if (!Files.isDirectory(artifactPath)) {
                    errorHandler(ProcessorMessages.ERROR_EXPECTED_DIRECTORY, artistPath.getFileName().toString());
                } else {
                    loadFromArtifactPath(artistPath, artifactPath, null, result);
                }
            }
        } catch (IOException e) {
            throw new ProcessorException("Exception:" + e.getMessage());
        }
    }

    protected void loadFromArtifactPath(Path artistPath, Path artifactPath, NamesParser.YearTitle parentYT, List<MediaFileValidationDTO> result) throws ProcessorException {
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
                    loadFromArtifactPath(artistPath, directoryFolderDiskPath, yt, result);
                }
            } else {
                try (Stream<Path> compositionPathStream = Files.list(artifactPath)) {
                    List<String> compositionFileNames = compositionPathStream
                            .map(f -> f.getFileName().toString())
                            .filter(f -> f.contains("."))
                            .filter(f -> mediaFormats.contains(f.substring(f.lastIndexOf(".") + 1)))
                            .collect(Collectors.toList());
                    if (compositionFileNames.size() == 0) {
                        errorHandler(ProcessorMessages.ERROR_COMPOSITIONS_NOT_FOUND_FOR_ARTIFACT, artifactPath.getFileName().toString());
                    } else {
                        compositionFileNames.forEach(compositionFileName -> {
                            result.add(new MediaFileValidationDTO(
                                    artistPath.getFileName().toString(),
                                    yt.getTitle(),
                                    (long) yt.getYear(),
                                    null,
                                    null,
                                    compositionFileName,
                                    null
                            ));
                        });
                    }
                }  catch (IOException e) {
                    throw new ProcessorException(ProcessorMessages.ERROR_EXCEPTION, e.getMessage());
                }
            }
        }
    }

    private boolean validateCompositions(List<MediaFileValidationDTO> pathValidation, List<MediaFileValidationDTO> dbValidation) {
        Set<String> pathMediaFiles = MediaFileValidationDTO.getMusicMediaFiles(pathValidation);
        Set<String> dbMediaFiles = MediaFileValidationDTO.getMusicMediaFiles(dbValidation);

        return compareStringSets(
                pathMediaFiles,
                dbMediaFiles,
                ProcessorMessages.ERROR_COMPOSITIONS_NOT_IN_FILES,
                ProcessorMessages.ERROR_COMPOSITIONS_NOT_IN_DB
        );
    }

    private boolean validateMediaFiles(List<MediaFileValidationDTO> pathValidation, List<MediaFileValidationDTO> dbValidation) {
        Set<String> pathMediaFiles = MediaFileValidationDTO.getMusicMediaFiles(pathValidation);
        Set<String> dbMediaFiles = MediaFileValidationDTO.getMusicMediaFiles(dbValidation);

        return compareStringSets(
                pathMediaFiles,
                dbMediaFiles,
                ProcessorMessages.ERROR_MEDIA_FILES_NOT_IN_FILES,
                ProcessorMessages.ERROR_MEDIA_FILES_NOT_IN_DB
        );
    }
}
