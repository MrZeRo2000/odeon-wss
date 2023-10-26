package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.dto.TrackDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.TrackService;
import com.romanpulov.odeonwss.service.processor.utils.TracksValidateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractDVNonMusicValidateProcessor extends AbstractFileSystemProcessor {
    private static final Logger logger = LoggerFactory.getLogger(AbstractDVNonMusicValidateProcessor.class);

    private ArtifactType artifactType;

    private final ArtifactTypeRepository artifactTypeRepository;
    private final ArtifactRepository artifactRepository;
    private final MediaFileRepository mediaFileRepository;
    private final TrackService trackService;

    private final Function<ArtifactTypeRepository, ArtifactType> artifactTypeSupplier;

    public AbstractDVNonMusicValidateProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            MediaFileRepository mediaFileRepository,
            TrackService trackService,
            Function<ArtifactTypeRepository, ArtifactType> artifactTypeSupplier) {
        this.artifactTypeRepository = artifactTypeRepository;
        this.artifactRepository = artifactRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.trackService = trackService;
        this.artifactTypeSupplier = artifactTypeSupplier;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        this.artifactType = Optional
                .ofNullable(this.artifactType)
                .orElse(artifactTypeSupplier.apply(artifactTypeRepository));

        List<MediaFileValidationDTO> dbValidation = mediaFileRepository.getTrackMediaFileValidationDV(
                this.artifactType);
        logger.info("dbValidation:" + dbValidation);

        final List<MediaFileValidationDTO> pathValidation = PathValidationLoader.loadFromPath(
                this,
                path,
                this.artifactType.getMediaFileFormats());
        logger.info("pathValidation:" + pathValidation);

        if (MediaFileValidator.validateArtifacts(this, pathValidation, dbValidation)) {
            infoHandler(ProcessorMessages.INFO_ARTIFACTS_VALIDATED);

            if (MediaFileValidator.validateMediaFiles(this, pathValidation, dbValidation)) {
                infoHandler(ProcessorMessages.INFO_MEDIA_FILES_VALIDATED);
            }

            List<MediaFileValidationDTO> dbArtifactValidation = mediaFileRepository
                    .getArtifactMediaFileValidationDV(artifactType);

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
                    null,
                    List.of(artifactType));

            List<TrackDTO> tracks = trackService.getTableByArtifactTypeId(this.artifactType.getId());
            if (ValueValidator.validateConditionValue(
                    this,
                    tracks,
                    ProcessorMessages.ERROR_TRACKS_WITHOUT_PRODUCT,
                    t -> Objects.isNull(t.getDvProduct()),
                    t -> MediaFileValidator.DELIMITER_FORMAT.formatted(t.getArtifact().getTitle(), t.getTitle()))) {
                infoHandler(ProcessorMessages.INFO_PRODUCTS_FOR_TRACKS_VALIDATED);
            }
        }
    }
}
