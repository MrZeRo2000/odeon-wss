package com.romanpulov.odeonwss.service.user;

import com.romanpulov.odeonwss.dto.IdNameDTO;
import com.romanpulov.odeonwss.dto.user.TrackUserImportDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.DVType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.exception.EmptyParameterException;
import com.romanpulov.odeonwss.exception.WrongParameterValueException;
import com.romanpulov.odeonwss.repository.*;
import com.romanpulov.odeonwss.service.DVProductService;
import com.romanpulov.odeonwss.utils.media.ChaptersParser;
import com.romanpulov.odeonwss.utils.media.ChaptersParsingException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TrackUserImportService {
    private final ArtifactRepository artifactRepository;
    private final ArtifactTypeRepository artifactTypeRepository;
    private final DVTypeRepository dvTypeRepository;
    private final TrackRepository trackRepository;
    private final DVProductService dvProductService;
    private final MediaFileRepository mediaFileRepository;

    public TrackUserImportService(
            ArtifactRepository artifactRepository,
            ArtifactTypeRepository artifactTypeRepository,
            DVTypeRepository dvTypeRepository,
            TrackRepository trackRepository,
            DVProductService dvProductService,
            MediaFileRepository mediaFileRepository) {
        this.artifactRepository = artifactRepository;
        this.artifactTypeRepository = artifactTypeRepository;
        this.dvTypeRepository = dvTypeRepository;
        this.trackRepository = trackRepository;
        this.dvProductService = dvProductService;
        this.mediaFileRepository = mediaFileRepository;
    }

    @Transactional
    public ImportStats executeImportTracks(TrackUserImportDTO data)
            throws CommonEntityNotFoundException, EmptyParameterException, WrongParameterValueException {
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
        long num = Optional.ofNullable(data.getNum()).orElse(1L);

        if (!mediaFile.getArtifact().getId().equals(artifact.getId())) {
            throw new WrongParameterValueException("MediaFile", "Artifact for media file does not match");
        }

        List<String> titles = data.getTitles();
        if (titles.isEmpty()) {
            throw new EmptyParameterException("Titles");
        }

        if (List.of(artifactTypeRepository.getWithDVAnimation(), artifactTypeRepository.getWithDVMovies())
                .contains(artifact.getArtifactType())) {
            importNonMusicArtifact(data, artifact, mediaFile, dvType, num, titles, result);
        } else {
            throw new WrongParameterValueException("Artifact", "Unsupported artifact type");
        }

        return result;
    }

    private void importNonMusicArtifact(
            TrackUserImportDTO data,
            Artifact artifact,
            MediaFile mediaFile,
            DVType dvType,
            long num,
            List<String> titles,
            ImportStats result)
            throws EmptyParameterException, WrongParameterValueException {

        List<String> chapters = data.getChapters();
        if (chapters.isEmpty()) {
            throw new EmptyParameterException("Chapters");
        }

        Collection<Long> durations;
        try {
            durations = ChaptersParser.parseLines(chapters);
        } catch (ChaptersParsingException e) {
            throw new WrongParameterValueException("Chapters", e.getMessage());
        }

        if (titles.size() != durations.size()) {
            throw new WrongParameterValueException("Chapters",
                    "Titles size:%d and chapters duration size:%d mismatch".formatted(titles.size(), durations.size()));
        }

        Iterator<Long> durationsIterator = durations.iterator();
        for (String title: titles) {
            Long duration = durationsIterator.next();

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
    }
}