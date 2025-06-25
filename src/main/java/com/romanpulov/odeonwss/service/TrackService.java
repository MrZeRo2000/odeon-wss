package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.*;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.exception.WrongParameterValueException;
import com.romanpulov.odeonwss.mapper.TrackMapper;
import com.romanpulov.odeonwss.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrackService
        extends AbstractEntityService<Track, TrackDTO, TrackRepository>
        implements EditableObjectService<TrackDTO>{

    private final ArtifactTypeRepository artifactTypeRepository;
    private final ArtifactRepository artifactRepository;
    private final DVTypeRepository dvTypeRepository;
    private final TrackTransformer transformer;
    private final MediaFileRepository mediaFileRepository;
    private final TagRepository tagRepository;

    public TrackService(
            ArtifactRepository artifactRepository,
            ArtifactTypeRepository artifactTypeRepository,
            DVTypeRepository dvTypeRepository,
            TrackRepository trackRepository,
            TrackMapper trackMapper,
            TrackTransformer transformer,
            MediaFileRepository mediaFileRepository,
            DVProductRepository dvProductRepository,
            TagRepository tagRepository) {
        super(trackRepository, trackMapper);
        this.artifactRepository = artifactRepository;
        this.artifactTypeRepository = artifactTypeRepository;
        this.dvTypeRepository = dvTypeRepository;
        this.transformer = transformer;
        this.mediaFileRepository = mediaFileRepository;
        this.tagRepository = tagRepository;

        this.setOnBeforeSaveEntityHandler(entity -> {
            entity.setMediaFiles(
                entity
                        .getMediaFiles()
                        .stream()
                        .filter(v -> v.getId() != null)
                        .map(v -> mediaFileRepository.findById(v.getId()).orElse(null))
                        .filter(v -> !Objects.isNull(v))
                        .collect(Collectors.toSet())
            );
            entity.setDvProducts(
                    entity
                            .getDvProducts()
                            .stream()
                            .filter(v -> v.getId() != null)
                            .map(v -> dvProductRepository.findById(v.getId()).orElse(null))
                            .filter(v -> !Objects.isNull(v))
                            .collect(Collectors.toSet())
            );
        });
    }

    public List<TrackDTO>getTable(Long artifactId) throws CommonEntityNotFoundException {
        if (artifactRepository.existsById(artifactId)) {
            return transformer.transform(repository.findAllFlatDTOByArtifactId(artifactId));
        } else {
            throw new CommonEntityNotFoundException("Artifact", artifactId);
        }
    }

    public List<TrackDTO> getTableByArtifactTypeId(ArtistType artistType, Long artifactTypeId) {
        return transformer.transform(repository.findAllFlatDTOByArtifactTypeId(artistType, artifactTypeId));
    }

    public List<TrackDTO> getTableByProductId(Long productId) {
        return transformer.transform(repository.findAllFlatDTOByDvProductId(productId));
    }

    public List<TrackDTO> getTableByOptional(Collection<Long> artifactTypeIds, Collection<Long> artistIds) {
        return transformer.transform(repository.findAllFlatDTOByOptional(
                artifactTypeIds == null ? 0 : (long)artifactTypeIds.size(),
                artifactTypeIds,
                artistIds == null ? 0 : (long)artistIds.size(),
                artistIds
        ));
    }

    @Override
    @Transactional
    public TrackDTO getById(Long id) throws CommonEntityNotFoundException {
        List<TrackFlatDTO> flatDTOS = repository.findFlatDTOById(id);
        if (flatDTOS.isEmpty()) {
            throw new CommonEntityNotFoundException(this.entityName, id);
        } else {
            return transformer.transform(flatDTOS).get(0);
        }
    }

    @Transactional
    public void insertTrackWithMedia(Track track, MediaFile mediaFile) {
        if (mediaFile.getId() == null) {
            //get id from existing
            Optional<MediaFile> existingMediaFile = mediaFileRepository.findFirstByArtifactAndName(
                    mediaFile.getArtifact(), mediaFile.getName());
            if (existingMediaFile.isPresent()) {
                mediaFile.setId(existingMediaFile.get().getId());
                mediaFile.setInsertDateTime(existingMediaFile.get().getInsertDateTime());
            }

            //save
            mediaFileRepository.save(mediaFile);
        } else {
            mediaFile = mediaFileRepository.findById(mediaFile.getId()).orElseThrow();
        }
        track.setMediaFiles(Set.of(mediaFile));
        repository.save(track);
    }

    @Transactional
    public void insertTracksWithMedia(Iterable<Track> tracks, Iterable<MediaFile> mediaFiles) {
        mediaFileRepository.saveAll(mediaFiles);
        repository.saveAll(tracks);
    }

    @Transactional
    public RowsAffectedDTO resetTrackNumbers(long artifactId) throws CommonEntityNotFoundException {
        Artifact artifact = artifactRepository.findById(artifactId).orElseThrow
                (() -> new CommonEntityNotFoundException("Artifact", artifactId));
        long rowsAffected = 0;

        if (artifactTypeRepository.isVideo(artifact.getArtifactType().getId())) {
            List<Track> tracks = repository.findAllByArtifact(artifact)
                    .stream()
                    .filter(t -> t.getNum() != null)
                    .sorted(Comparator.comparingLong(t -> t.getNum() == null ? 0 : t.getNum()))
                    .toList();

            long num = 1;
            for (Track track : tracks) {
                if (track.getNum() != null && track.getNum() != num) {
                    track.setNum(num);
                    rowsAffected ++;
                }
                num++;
            }
        }
        return RowsAffectedDTO.from(rowsAffected);
    }

    @Transactional
    public RowsAffectedDTO updateDurationsFromMediaFile(long artifactId, long mediaFileId, List<Long> chapterDurations)
            throws CommonEntityNotFoundException, WrongParameterValueException {
        if (Objects.isNull(chapterDurations) || chapterDurations.isEmpty()) {
            throw new WrongParameterValueException("Chapters", "Empty chapters");
        }

        long rowsAffected = 0;

        MediaFileDTO mediaFileDTO = mediaFileRepository.findDTOById(mediaFileId).orElseThrow(
                () -> new CommonEntityNotFoundException("MediaFile", mediaFileId)
        );

        List<Long> validChapterDurations = chapterDurations
                .stream()
                .filter(c -> !Objects.isNull(c) && (c > 0))
                .toList();

        if (validChapterDurations.size() != chapterDurations.size()) {
            throw new WrongParameterValueException("Chapters", "Invalid chapters found");
        }

        long chaptersTotalDuration = validChapterDurations
                .stream()
                .mapToLong(Long::longValue).sum();

        if (chaptersTotalDuration > mediaFileDTO.getDuration()) {
            throw new WrongParameterValueException("Chapters", "Chapters duration exceeds media file duration");
        }

        List<Long> trackIds = repository
                .findAllFlatDTOByArtifactId(artifactId)
                .stream()
                .filter(t ->
                        !Objects.isNull(t) &&
                        !Objects.isNull(t.getMediaFileId()) &&
                        t.getMediaFileId().equals(mediaFileId) &&
                        !Objects.isNull(t.getNum()))
                .sorted(Comparator.comparingLong(TrackFlatDTO::getNum))
                .map(TrackFlatDTO::getId)
                .distinct()
                .toList();

        if (trackIds.size() != validChapterDurations.size() + 1) {
            throw new WrongParameterValueException(
                    "Chapters",
                    "Number of chapters: %d does not correspond to number of tracks: %d".formatted(
                            validChapterDurations.size(), trackIds.size())
            );
        }

        Iterator<Long> chapterDurationsIterator = validChapterDurations.iterator();
        long duration;

        for (Long trackId: trackIds) {
            if (chapterDurationsIterator.hasNext()) {
                duration = chapterDurationsIterator.next();
            } else {
                duration = mediaFileDTO.getDuration() - chaptersTotalDuration;
            }

            Track track = repository.findById(trackId).orElseThrow(
                    () -> new CommonEntityNotFoundException("Track", trackId)
            );
            track.setDuration(duration);
            repository.save(track);
            rowsAffected ++;
        }

        return RowsAffectedDTO.from(rowsAffected);
    }

    @Transactional
    public RowsAffectedDTO updateVideoTypes(long artifactId, long dvTypeId)
            throws CommonEntityNotFoundException {
        Artifact artifact = artifactRepository.findById(artifactId).orElseThrow
                (() -> new CommonEntityNotFoundException("Artifact", artifactId));
        DVType dvType = dvTypeRepository.findById(dvTypeId).orElseThrow
                (() -> new CommonEntityNotFoundException("DVType", dvTypeId));

        List<Track> tracks = repository
                .findAllByArtifact(artifact)
                .stream()
                .filter(t -> t.getDvType() != dvType)
                .toList();
        for (Track track: tracks) {
            track.setDvType(dvType);
        }

        repository.saveAll(tracks);

        return RowsAffectedDTO.from(tracks.size());
    }

    @Transactional
    public RowsAffectedDTO updateSelectedVideoTypes(
            long artifactId,
            List<Long> selectedTrackIds,
            long dvTypeId)
            throws CommonEntityNotFoundException {
        Artifact artifact = artifactRepository.findById(artifactId).orElseThrow
                (() -> new CommonEntityNotFoundException("Artifact", artifactId));
        DVType dvType = dvTypeRepository.findById(dvTypeId).orElseThrow
                (() -> new CommonEntityNotFoundException("DVType", dvTypeId));

        Set<Long> selectedTrackIdSet = new HashSet<>(selectedTrackIds);

        List<Track> tracks = repository
                .findAllByArtifact(artifact)
                .stream()
                .filter(t -> selectedTrackIdSet.contains(t.getId()) && (t.getDvType() != dvType))
                .toList();
        for (Track track: tracks) {
            track.setDvType(dvType);
        }

        repository.saveAll(tracks);

        return RowsAffectedDTO.from(tracks.size());
    }

    @Transactional
    public RowsAffectedDTO updateSelectedTags(
            long artifactId,
            List<Long> selectedTrackIds,
            List<String> tags)
        throws CommonEntityNotFoundException {
        artifactRepository.findById(artifactId).orElseThrow
                (() -> new CommonEntityNotFoundException("Artifact", artifactId));

        Set<Long> selectedTrackIdSet = new HashSet<>(selectedTrackIds);

        List<TrackDTO> tracks = transformer.transform(
                repository.findAllFlatDTOByArtifactId(artifactId)
                        .stream()
                        .filter(t -> selectedTrackIdSet.contains(t.getId()))
                        .toList());

        for (TrackDTO track: tracks) {
            TrackDTOImpl newTrack = new TrackDTOImpl();
            newTrack.setId(track.getId());
            newTrack.setTags(tags);

            updateTags(newTrack);
        }

        return RowsAffectedDTO.from(tracks.size());
    }

    public TrackDTO updateTags(TrackDTO dto) throws CommonEntityNotFoundException {
        Track track = repository.findById(dto.getId()).orElseThrow(
                () -> new CommonEntityNotFoundException("Track", dto.getId()));

        // ensure tags
        Set<Tag> tags = dto
                .getTags()
                .stream()
                .map(v -> tagRepository.findTagByName(v).orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(v);
                    tagRepository.save(newTag);
                    return newTag;
                }))
                .collect(Collectors.toSet());

        // perform changes
        track.setTags(tags);
        repository.save(track);

        List<TrackDTO> result = transformer.transform(repository.findAllFlatDTOTagsByTrackId(track.getId()));
        return result.isEmpty() ? new TrackDTOImpl() : result.get(0);
    }

}
