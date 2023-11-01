package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.dto.TrackFlatDTO;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.service.processor.utils.MediaFilesValidateUtil;
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
    private final TrackRepository trackRepository;

    private final Function<ArtifactTypeRepository, ArtifactType> artifactTypeSupplier;

    public AbstractDVNonMusicValidateProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            MediaFileRepository mediaFileRepository,
            TrackRepository trackRepository,
            Function<ArtifactTypeRepository, ArtifactType> artifactTypeSupplier) {
        this.artifactTypeRepository = artifactTypeRepository;
        this.artifactRepository = artifactRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.trackRepository = trackRepository;
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

            List<MediaFileValidationDTO> dbArtifactValidation = mediaFileRepository
                    .getArtifactMediaFileValidationDV(artifactType);

            MediaFilesValidateUtil.validateMediaFilesVideoAll(
                    this,
                    pathValidation,
                    dbValidation,
                    dbArtifactValidation);

            TracksValidateUtil.validateMonotonicallyIncreasingTrackNumbers(
                    this,
                    artifactRepository,
                    null,
                    List.of(artifactType));


            List<TrackFlatDTO> tracks = trackRepository.findAllFlatDTOByArtifactTypeId(this.artifactType.getId());

            TracksValidateUtil.validateTracksDuration(this, tracks);

            if (ValueValidator.validateConditionValue(
                    this,
                    tracks,
                    ProcessorMessages.ERROR_TRACKS_WITHOUT_PRODUCT,
                    t -> Objects.isNull(t.getDvProductId()),
                    t -> MediaFileValidator.DELIMITER_FORMAT.formatted(t.getArtifactTitle(), t.getTitle()))) {
                infoHandler(ProcessorMessages.INFO_PRODUCTS_FOR_TRACKS_VALIDATED);
            }
        }
    }
}
