package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ArtifactTypeService {
    private final AppConfiguration appConfiguration;
    private final ArtifactTypeRepository artifactTypeRepository;

    private final Map<Long, String> artifactTypePathMap = new HashMap<>();

    public ArtifactTypeService(AppConfiguration appConfiguration, ArtifactTypeRepository artifactTypeRepository) {
        this.appConfiguration = appConfiguration;
        this.artifactTypeRepository = artifactTypeRepository;
    }

    public String getArtifactTypePath(long artifactTypeId) {
        return artifactTypePathMap.computeIfAbsent(artifactTypeId, v -> {
            if (v.equals(artifactTypeRepository.getWithMP3().getId())) {
                return appConfiguration.getPathMap().get(AppConfiguration.PathType.PT_MP3);
            } else if (v.equals(artifactTypeRepository.getWithLA().getId())) {
                return appConfiguration.getPathMap().get(AppConfiguration.PathType.PT_LA);
            } else if (v.equals(artifactTypeRepository.getWithDVMusic().getId())) {
                return appConfiguration.getPathMap().get(AppConfiguration.PathType.PT_DV_MUSIC);
            } else if (v.equals(artifactTypeRepository.getWithDVMovies().getId())) {
                return appConfiguration.getPathMap().get(AppConfiguration.PathType.PT_DV_MOVIES);
            } else if (v.equals(artifactTypeRepository.getWithDVAnimation().getId())) {
                return appConfiguration.getPathMap().get(AppConfiguration.PathType.PT_DV_ANIMATION);
            } else {
                return null;
            }
        });
    }
}
