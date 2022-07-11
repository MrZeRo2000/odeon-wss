package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTOBuilder;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class LAValidateProcessor extends AbstractValidateProcessor {

    private final MediaFileRepository mediaFileRepository;

    public LAValidateProcessor(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        List<MediaFileValidationDTO> pathValidation = loadFromPath(path);
        List<MediaFileValidationDTO> dbValidation = mediaFileRepository.getMediaFileValidationMusic(ArtifactType.withLA());

        if (validateArtistNames(pathValidation, dbValidation)) {
            infoHandler(ProcessorMessages.INFO_ARTISTS_VALIDATED);

            if (validateArtifacts(pathValidation, dbValidation)) {
                infoHandler(ProcessorMessages.INFO_ARTIFACTS_VALIDATED);

                if (validateMediaFiles(pathValidation, dbValidation)) {
                    infoHandler(ProcessorMessages.INFO_MEDIA_FILED_VALIDATED);
                }

            }
        }
    }

    @Override
    protected void loadFromArtifactPath(Path artistPath, Path artifactPath, List<MediaFileValidationDTO> result) throws ProcessorException {
        NamesParser.YearTitle yt = NamesParser.parseMusicArtifactTitle(artifactPath.getFileName().toString());
        if (yt == null) {
            errorHandler(ProcessorMessages.ERROR_PARSING_ARTIFACT_NAME, artifactPath.getFileName().toString());
            result.add(
                    new MediaFileValidationDTOBuilder()
                            .withArtistName(artistPath.getFileName().toString())
                            .build()
            );
        } else {

        }
    }

    private boolean validateMediaFiles(List<MediaFileValidationDTO> pathValidation, List<MediaFileValidationDTO> dbValidation) {
        return true;
    }
}
