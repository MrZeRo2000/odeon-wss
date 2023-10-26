package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.utils.TracksValidateUtil;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DVMusicValidateProcessor extends AbstractFileSystemProcessor {
    private ArtifactType artifactType;

    private final ArtifactTypeRepository artifactTypeRepository;

    private final ArtifactRepository artifactRepository;

    private final MediaFileRepository mediaFileRepository;

    public DVMusicValidateProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            MediaFileRepository mediaFileRepository) {
        this.artifactTypeRepository = artifactTypeRepository;
        this.artifactRepository = artifactRepository;
        this.mediaFileRepository = mediaFileRepository;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        this.artifactType = Optional.ofNullable(this.artifactType).orElse(artifactTypeRepository.getWithDVMusic());

        final List<MediaFileValidationDTO> dbValidation = mediaFileRepository.getTrackMediaFileValidationMusic(
                ArtistType.ARTIST, artifactType
        );
        final List<MediaFileValidationDTO> pathValidation = PathValidationLoader.loadFromPath(
                this,
                path,
                this.artifactType.getMediaFileFormats());

        if (validateEmptyMediaFiles()) {
            infoHandler(ProcessorMessages.INFO_MEDIA_FILES_SIZE_VALIDATED);
        }

        if (validateArtifactsWithoutArtists()) {
            if (MediaFileValidator.validateArtifacts(this, pathValidation, dbValidation)) {
                infoHandler(ProcessorMessages.INFO_ARTIFACTS_VALIDATED);

                if (MediaFileValidator.validateMediaFiles(this, pathValidation, dbValidation)) {
                    infoHandler(ProcessorMessages.INFO_MEDIA_FILES_VALIDATED);
                }

                List<MediaFileValidationDTO> dbArtifactValidation = mediaFileRepository
                        .getArtifactMediaFileValidationMusic(artifactType);

                if (MediaFileValidator.validateArtifactMediaFiles(this, pathValidation, dbArtifactValidation)) {
                    infoHandler(ProcessorMessages.INFO_ARTIFACT_MEDIA_FILES_VALIDATED);
                }

                if (MediaFileValidator.validateArtifactMediaFileSize(this, dbArtifactValidation)) {
                    infoHandler(ProcessorMessages.INFO_ARTIFACT_MEDIA_FILES_SIZE_VALIDATED);
                }

                if (ValueValidator.validateConditionValue(
                        this,
                        dbArtifactValidation,
                        ProcessorMessages.ERROR_MEDIA_FILES_EMPTY_BITRATE,
                        m -> Optional.ofNullable(m.getMediaFileBitrate()).orElse(0L).equals(0L),
                        m -> MediaFileValidator.DELIMITER_FORMAT.formatted(m.getArtifactTitle(), m.getMediaFileName()))) {
                    infoHandler(ProcessorMessages.INFO_MEDIA_FILES_BITRATE_VALIDATED);
                }

                if (MediaFileValidator.validateMediaFileSize(this, pathValidation, dbValidation)) {
                    infoHandler(ProcessorMessages.INFO_MEDIA_FILES_SIZE_MISMATCH_VALIDATED);
                }

                TracksValidateUtil.validateMonotonicallyIncreasingTrackNumbers(
                        this,
                        artifactRepository,
                        List.of(ArtistType.ARTIST, ArtistType.CLASSICS),
                        List.of(artifactType));
            }
        }
    }

    private boolean validateEmptyMediaFiles() {
        List<MediaFile> mediaFiles = mediaFileRepository
                .getMediaFilesWithEmptySizeByArtifactType(artifactType);
        if (mediaFiles.isEmpty()) {
            return true;
        } else {
            errorHandler(
                    ProcessorMessages.ERROR_MEDIA_FILES_EMPTY_SIZE,
                    mediaFiles.stream().map(MediaFile::getName).collect(Collectors.toList())
            );
            return false;
        }
    }

    private boolean validateArtifactsWithoutArtists() {
        List<String> artifactsWithoutArtists = artifactRepository.getAllByArtifactType(artifactType)
                .stream()
                .filter(a -> a.getArtist() == null)
                .map(Artifact::getTitle)
                .collect(Collectors.toList());
        if (!artifactsWithoutArtists.isEmpty()) {
            errorHandler(
                    ProcessorMessages.ERROR_ARTIFACTS_WITHOUT_ARTISTS,
                    artifactsWithoutArtists
            );
            return false;
        } else {
            return true;
        }

    }

}
