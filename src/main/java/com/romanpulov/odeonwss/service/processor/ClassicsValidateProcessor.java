package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.view.IdTitleView;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ClassicsValidateProcessor extends AbstractFileSystemProcessor {

    private final ArtifactRepository artifactRepository;

    public ClassicsValidateProcessor(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        Set<String> pathValidation = loadFromPath(path);
        Set<String> dbValidation = artifactRepository.getArtifactsByArtistType(ArtistType.CLASSICS)
                .stream()
                .map(IdTitleView::getTitle)
                .collect(Collectors.toSet());

        if (validateArtifacts(pathValidation, dbValidation)) {
            infoHandler(ProcessorMessages.INFO_ARTIFACTS_VALIDATED);
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
