package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.IdTitleDTO;
import com.romanpulov.odeonwss.dto.MediaFileDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.dto.TrackFlatDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.service.processor.utils.MediaFilesValidateUtil;
import com.romanpulov.odeonwss.service.processor.utils.TracksValidateUtil;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DVMusicValidateProcessor extends AbstractFileSystemProcessor {
    private final ArtistType artistType = ArtistType.ARTIST;
    private ArtifactType artifactType;

    private final ArtifactTypeRepository artifactTypeRepository;

    private final ArtifactRepository artifactRepository;

    private final MediaFileRepository mediaFileRepository;

    private final TrackRepository trackRepository;

    public DVMusicValidateProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            MediaFileRepository mediaFileRepository,
            TrackRepository trackRepository) {
        this.artifactTypeRepository = artifactTypeRepository;
        this.artifactRepository = artifactRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.trackRepository = trackRepository;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        this.artifactType = Optional.ofNullable(this.artifactType).orElse(artifactTypeRepository.getWithDVMusic());

        final List<IdTitleDTO> artifacts = artifactRepository.findAllArtifactsByArtifactType(artifactType);
        final Set<Long> artifactIds = artifacts.stream().map(IdTitleDTO::getId).collect(Collectors.toSet());

        final List<MediaFileValidationDTO> dbValidation = mediaFileRepository.getTrackMediaFileValidationMusic(
                artistType, artifactType
        );

        final List<MediaFileValidationDTO> pathValidation = PathValidationLoader.loadFromPath(
                this,
                path,
                this.artifactType.getMediaFileFormats());

        if (validateArtifactsWithoutArtists()) {
            if (MediaFileValidator.validateArtifacts(this, pathValidation, dbValidation)) {
                infoHandler(ProcessorMessages.INFO_ARTIFACTS_VALIDATED);

                final List<MediaFileDTO> mediaFiles = mediaFileRepository.findAllDTOByArtifactType(this.artifactType);

                if (MediaFilesValidateUtil.validateEmptyMediaFilesArtifacts(this, artifacts, artifactIds, mediaFiles)) {
                    if (validateEmptyMediaFiles()) {
                        infoHandler(ProcessorMessages.INFO_MEDIA_FILES_SIZE_VALIDATED);
                    }

                    List<TrackFlatDTO> tracks = trackRepository.findAllFlatDTOByArtifactTypeId(
                            artistType, this.artifactType.getId());

                    if (TracksValidateUtil.validateEmptyTracksArtifacts(
                            this,
                            artifacts,
                            artifactIds,
                            tracks)) {
                        List<MediaFileValidationDTO> dbArtifactValidation = mediaFileRepository
                                .getArtifactMediaFileValidationMusic(artistType, artifactType);

                        MediaFilesValidateUtil.validateMediaFilesVideoAll(
                                this,
                                pathValidation,
                                dbValidation,
                                dbArtifactValidation);

                        TracksValidateUtil.validateMonotonicallyIncreasingTrackNumbers(
                                this,
                                artifactRepository,
                                List.of(ArtistType.ARTIST, ArtistType.CLASSICS),
                                List.of(artifactType));


                        TracksValidateUtil.validateTracksDuration(
                                this,
                                TrackValidator.ARTIFACT_TITLE_MAPPER,
                                tracks);
                    }
                }
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
