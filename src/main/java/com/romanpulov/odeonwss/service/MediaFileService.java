package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.*;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.exception.WrongParameterValueException;
import com.romanpulov.odeonwss.mapper.MediaFileMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.repository.TrackRepository;
import com.romanpulov.odeonwss.service.processor.PathReader;
import com.romanpulov.odeonwss.service.processor.ProcessorException;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;
import com.romanpulov.odeonwss.utils.media.MediaFileInfoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MediaFileService
        extends AbstractEntityService<MediaFile, MediaFileDTO, MediaFileRepository>
        implements EditableObjectService <MediaFileDTO> {

    private final ArtifactRepository artifactRepository;
    private final ArtifactTypeRepository artifactTypeRepository;
    private final ArtifactTypeService artifactTypeService;
    private final MediaParser mediaParser;
    private final MediaFileMapper mediaFileMapper;
    private final TrackRepository trackRepository;

    public MediaFileService(
            MediaFileRepository mediaFileRepository,
            MediaParser mediaParser,
            MediaFileMapper mediaFileMapper,
            ArtifactRepository artifactRepository,
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactTypeService artifactTypeService, TrackRepository trackRepository) {
        super(mediaFileRepository, mediaFileMapper);
        this.artifactRepository = artifactRepository;
        this.artifactTypeRepository = artifactTypeRepository;
        this.artifactTypeService = artifactTypeService;

        this.setOnBeforeSaveEntityHandler(entity -> {
           if (!artifactRepository.existsById(entity.getArtifact().getId())) {
               throw new CommonEntityNotFoundException("MediaFile", entity.getArtifact().getId());
           }
        });
        this.mediaParser = mediaParser;
        this.mediaFileMapper = mediaFileMapper;
        this.trackRepository = trackRepository;
    }

    public List<MediaFileDTO> getTable(Long artifactId) throws CommonEntityNotFoundException {
        if (artifactRepository.existsById(artifactId)) {
            return repository.findAllDTOByArtifactId(artifactId);
        } else {
            throw new CommonEntityNotFoundException("Artifact", artifactId);
        }
    }

    public List<MediaFileDTO> getTableByTrackId(Long trackId) throws CommonEntityNotFoundException {
        if (trackRepository.existsById(trackId)) {
            return repository.findAllDTOByTrackId(trackId);
        } else {
            throw new CommonEntityNotFoundException("Track", trackId);
        }
    }

    public List<MediaFileDTO> getTableIdNameDuration(Long artifactId) throws CommonEntityNotFoundException {
        if (artifactRepository.existsById(artifactId)) {
            return repository.findAllDTOIdNameDurationByArtifactId(artifactId);
        } else {
            throw new CommonEntityNotFoundException("Artifact", artifactId);
        }
    }

    public List<TextDTO> getMediaFiles(Long artifactId) throws CommonEntityNotFoundException {
        ArtifactFlatDTO artifact = artifactRepository.findFlatDTOById(artifactId).orElseThrow (
                () -> new CommonEntityNotFoundException("Artifact", artifactId)
        );

        ArtifactTypeDTO artifactType = artifactTypeRepository.findDTOById(artifact.getArtifactTypeId()).orElseThrow(
                () -> new CommonEntityNotFoundException("ArtifactType", artifact.getArtifactTypeId())
        );

        Path rootPath = Paths.get(artifactTypeService.getArtifactTypePath(artifact.getArtifactTypeId()));

        try {
            List<Path> paths = new ArrayList<>();

            boolean result = PathReader.readPath(
                    Path.of(rootPath.toAbsolutePath().toString(), artifact.getTitle()),
                    p -> NamesParser.validateFileNameMediaFormat(
                            p.getFileName().toString(),
                            artifactType.getMediaFileFormats()),
                    null,
                    PathReader.ReadRule.RR_FILE, paths);
            if (result) {
                return paths
                        .stream()
                        .map(v -> v.getFileName().toString())
                        .map(TextDTOImpl::fromText)
                        .sorted(Comparator.comparing(TextDTOImpl::getText))
                        .collect(Collectors.toList());
            } else {
                return List.of();
            }

        } catch (ProcessorException e) {
            return List.of();
        }
    }

    public MediaFileDTO getMediaFileAttributes(Long artifactId, String mediaFileName)
            throws CommonEntityNotFoundException, WrongParameterValueException {
        ArtifactFlatDTO artifact = artifactRepository.findFlatDTOById(artifactId).orElseThrow(
                () -> new CommonEntityNotFoundException("Artifact", artifactId)
        );

        return mediaFileMapper.toDTO(getMediaFileAttributesByArtifact(
                artifact.getArtifactTypeId(), artifact.getTitle(), mediaFileName));
    }

    private MediaFile getMediaFileAttributesByArtifact(long artifactTypeId, String artifactTitle, String mediaFileName)
            throws WrongParameterValueException {
        Path rootPath = Paths.get(artifactTypeService.getArtifactTypePath(artifactTypeId));
        Path filePath = Path.of(rootPath.toAbsolutePath().toString(), artifactTitle, mediaFileName);
        if (Files.exists(filePath)) {
            try {
                return mediaFileMapper.fromMediaFileInfo(mediaParser.parseTrack(filePath));
            } catch (MediaFileInfoException e)  {
                throw new WrongParameterValueException("MediaFileName: parsing error", e.getMessage());
            }
        } else {
            throw new WrongParameterValueException("MediaFileName", mediaFileName);
        }
    }

    @Transactional
    public RowsAffectedDTO insertMediaFiles(Long artifactId, List<String> mediaFileNames)
            throws CommonEntityNotFoundException, WrongParameterValueException {
        Artifact artifact = artifactRepository.findById(artifactId).orElseThrow(
                () -> new CommonEntityNotFoundException("Artifact", artifactId)
        );

        Set<String> existingMediaFiles = getTableIdNameDuration(artifactId)
                .stream()
                .map(MediaFileDTO::getName)
                .collect(Collectors.toUnmodifiableSet());

        List<MediaFile> newMediaFiles = new ArrayList<>();

        for (String mediaFileName: mediaFileNames) {
            if (existingMediaFiles.contains(mediaFileName)) {
                throw new WrongParameterValueException("mediaFileNames",
                        "MediaFile " + mediaFileName + " already exists");
            } else {
                MediaFile mediaFile = this.getMediaFileAttributesByArtifact(
                        artifact.getArtifactType().getId(), artifact.getTitle(), mediaFileName);
                mediaFile.setArtifact(artifact);
                newMediaFiles.add(mediaFile);
            }
        }

        long savedCount = StreamSupport.stream(repository.saveAll(newMediaFiles).spliterator(), false).count();
        return RowsAffectedDTO.from(savedCount);
    }
}
