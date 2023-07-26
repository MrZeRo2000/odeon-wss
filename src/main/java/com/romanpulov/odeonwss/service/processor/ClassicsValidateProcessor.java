package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.IdTitleDTO;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.service.processor.utils.TracksValidateUtil;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ClassicsValidateProcessor extends AbstractFileSystemProcessor {

    private final ArtifactRepository artifactRepository;
    private final ArtifactTypeRepository artifactTypeRepository;

    public ClassicsValidateProcessor(
            ArtifactRepository artifactRepository,
            ArtifactTypeRepository artifactTypeRepository) {
        this.artifactRepository = artifactRepository;
        this.artifactTypeRepository = artifactTypeRepository;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        Set<String> pathValidation = loadFromPath(path);
        Set<String> dbValidation = artifactRepository.getArtifactsByArtistType(ArtistType.CLASSICS)
                .stream()
                .map(IdTitleDTO::getTitle)
                .collect(Collectors.toSet());

        if (validateArtifacts(pathValidation, dbValidation)) {
            infoHandler(ProcessorMessages.INFO_ARTIFACTS_VALIDATED);

            TracksValidateUtil.validateMonotonicallyIncreasingTrackNumbers(
                    this,
                    artifactRepository,
                    List.of(ArtistType.CLASSICS),
                    List.of(artifactTypeRepository.getWithMP3(), artifactTypeRepository.getWithLA()));
        }
    }

    private Set<String> loadFromPath(Path path) throws ProcessorException {
        List<Path> artifactFiles = new ArrayList<>();
        if (!PathReader.readPathFoldersOnly(this, path, artifactFiles)) {
            return Set.of();
        } else {
            return artifactFiles.stream().map(p -> p.getFileName().toString()).collect(Collectors.toSet());
        }
    }

    private boolean validateArtifacts(Set<String> pathValidation, Set<String> dbValidation) {
        return ValueValidator.compareStringSets(
                this,
                pathValidation,
                dbValidation,
                ProcessorMessages.ERROR_ARTIFACTS_NOT_IN_FILES,
                ProcessorMessages.ERROR_ARTIFACTS_NOT_IN_DB);
    }
}
