package com.romanpulov.odeonwss.service.user;

import com.romanpulov.odeonwss.dto.IdNameDTO;
import com.romanpulov.odeonwss.dto.user.TrackUserImportDTO;
import com.romanpulov.odeonwss.dto.user.TrackUserImportDetailDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.DVType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.exception.EmptyParameterException;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.DVTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.service.DVProductService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class TrackUserImportService {
    private final ArtifactRepository artifactRepository;
    private final DVTypeRepository dvTypeRepository;
    private final TrackRepository trackRepository;
    private final DVProductService dvProductService;
    private final MediaFileRepository mediaFileRepository;

    public TrackUserImportService(
            ArtifactRepository artifactRepository,
            DVTypeRepository dvTypeRepository,
            TrackRepository trackRepository,
            DVProductService dvProductService,
            MediaFileRepository mediaFileRepository) {
        this.artifactRepository = artifactRepository;
        this.dvTypeRepository = dvTypeRepository;
        this.trackRepository = trackRepository;
        this.dvProductService = dvProductService;
        this.mediaFileRepository = mediaFileRepository;
    }

    @Transactional
    public ImportStats executeImportTracks(TrackUserImportDTO data)
            throws CommonEntityNotFoundException, EmptyParameterException {
        ImportStats result = ImportStats.empty();

        Artifact artifact = artifactRepository
                .findById(data.getArtifact().getId())
                .orElseThrow(() -> new CommonEntityNotFoundException("Artifact", data.getArtifact().getId()));
        MediaFile mediaFile = mediaFileRepository
                .findById(data.getMediaFile().getId())
                .orElseThrow(() -> new CommonEntityNotFoundException("MediaFile", data.getMediaFile().getId()));
        IdNameDTO dvTypeDTO = Optional
                .ofNullable(data.getDvType())
                .orElseThrow(() -> new EmptyParameterException("dvType"));
        DVType dvType = dvTypeRepository
                .findById(dvTypeDTO.getId())
                .orElseThrow(() -> new CommonEntityNotFoundException("DVType", dvTypeDTO.getId()));
        Long num = Optional.ofNullable(data.getNum()).orElse(1L);

        for (TrackUserImportDetailDTO det: data.getTrackDetails()) {
            String title = Optional
                    .ofNullable(det.getTitle())
                    .orElseThrow(() -> new EmptyParameterException("Title"));
            Long duration = Optional
                    .ofNullable(det.getDuration())
                    .orElseThrow(() -> new EmptyParameterException("Duration"));

            Track track = new Track();
            track.setArtifact(artifact);
            track.setDvType(dvType);
            track.setTitle(title);
            track.setNum(num ++);
            track.setDuration(duration);
            dvProductService.findProductByArtifactTypeAndTitle(artifact.getArtifactType(), track.getTitle())
                    .ifPresent(p -> track.setDvProducts(Set.of(p)));
            track.setMediaFiles(Set.of(mediaFile));

            trackRepository.save(track);

            artifact.getTracks().add(track);
            artifactRepository.save(artifact);

            result.addRowInserted(title);
        }

        return result;
    }
}
