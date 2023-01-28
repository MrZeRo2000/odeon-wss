package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.CompositionValidationDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DVMoviesValidateProcessor extends AbstractFileSystemProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DVMoviesValidateProcessor.class);

    private final MediaFileRepository mediaFileRepository;

    public DVMoviesValidateProcessor(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        List<MediaFileValidationDTO> dbValidation = mediaFileRepository.getCompositionMediaFileValidationDV(
                ArtifactType.withDVMovies());
        logger.info("dbValidation:" + dbValidation);

        List<MediaFileValidationDTO> pathValidation = loadFromPath(path);
        logger.info("pathValidation:" + pathValidation);

        if (validateArtifacts(pathValidation, dbValidation)) {
            infoHandler(ProcessorMessages.INFO_ARTIFACTS_VALIDATED);
        }
    }

    private List<MediaFileValidationDTO> loadFromPath(Path path) throws ProcessorException {
        List<MediaFileValidationDTO> result = new ArrayList<>();

        List<Path> artifactPaths = new ArrayList<>();
        if (PathReader.readPathFoldersOnly(this, path, artifactPaths)) {
            for (Path artifactPath: artifactPaths) {
                List<Path> compositionPaths = new ArrayList<>();
                if (PathReader.readPathFilesOnly(this, artifactPath, compositionPaths)) {
                    compositionPaths.forEach(compositionPath ->
                            result.add(MediaFileValidationDTO.fromDVMediaFile(
                                    artifactPath.getFileName().toString(),
                                    null,
                                    compositionPath.getFileName().toString())));
                } else {
                    break;
                }
            }
        }

        return result;
    }

    protected boolean validateArtifacts(List<MediaFileValidationDTO> pathValidation, List<MediaFileValidationDTO> dbValidation) {
        Set<String> pathArtifacts = pathValidation.stream()
                .map(CompositionValidationDTO::getArtifactTitle)
                .collect(Collectors.toSet());
        Set<String> dbArtifacts = dbValidation.stream()
                .map(CompositionValidationDTO::getArtifactTitle)
                .collect(Collectors.toSet());

        return ValueValidator.compareStringSets(
                this,
                pathArtifacts,
                dbArtifacts,
                ProcessorMessages.ERROR_ARTIFACTS_NOT_IN_FILES,
                ProcessorMessages.ERROR_ARTIFACTS_NOT_IN_DB
        );
    }
}
