package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.Composition;
import com.romanpulov.odeonwss.entity.MediaFile;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MediaFileRepository extends CrudRepository<MediaFile, Long> {
    List<MediaFile> findAllByComposition(Composition composition);

    List<MediaFile> findAllByArtifact(Artifact artifact);
}
