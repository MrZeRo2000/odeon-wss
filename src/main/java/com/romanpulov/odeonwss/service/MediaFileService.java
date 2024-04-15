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
import com.romanpulov.odeonwss.service.processor.PathReader;
import com.romanpulov.odeonwss.service.processor.ProcessorException;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;
import com.romanpulov.odeonwss.utils.media.MediaFileInfoException;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MediaFileService
        extends AbstractEntityService<MediaFile, MediaFileDTO, MediaFileRepository>
        implements EditableObjectService <MediaFileDTO> {

    private final ArtifactRepository artifactRepository;
    private final ArtifactTypeRepository artifactTypeRepository;
    private final ArtifactTypeService artifactTypeService;
    private final MediaParser mediaParser;
    private final MediaFileMapper mediaFileMapper;

    public MediaFileService(
            MediaFileRepository mediaFileRepository,
            MediaParser mediaParser,
            MediaFileMapper mediaFileMapper,
            ArtifactRepository artifactRepository,
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactTypeService artifactTypeService) {
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
    }

    public List<MediaFileDTO> getTable(Long artifactId) throws CommonEntityNotFoundException {
        Optional<Artifact> existingArtifact = artifactRepository.findById(artifactId);
        if (existingArtifact.isPresent()) {
            return repository.findAllDTOByArtifactId(artifactId);
        } else {
            throw new CommonEntityNotFoundException("Artifact", artifactId);
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

        Path rootPath = Paths.get(artifactTypeService.getArtifactTypePath(artifact.getArtifactTypeId()));
        Path filePath = Path.of(rootPath.toAbsolutePath().toString(), artifact.getTitle(), mediaFileName);
        if (Files.exists(filePath)) {
            try {
                MediaFile mediaFile = mediaFileMapper.fromMediaFileInfo(mediaParser.parseTrack(filePath));
                return mediaFileMapper.toDTO(mediaFile);

            } catch (MediaFileInfoException e)  {
                throw new WrongParameterValueException("MediaFileName: parsing error", e.getMessage());
            }
        } else {
            throw new WrongParameterValueException("MediaFileName", mediaFileName);
        }
    }
}
