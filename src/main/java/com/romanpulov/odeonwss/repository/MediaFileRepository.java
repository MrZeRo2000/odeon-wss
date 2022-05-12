package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.CompositionValidationDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Composition;
import com.romanpulov.odeonwss.entity.MediaFile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MediaFileRepository extends CrudRepository<MediaFile, Long> {
    List<MediaFile> findAllByComposition(Composition composition);

    List<MediaFile> findAllByArtifact(Artifact artifact);

    @Query("SELECT " +
            "new com.romanpulov.odeonwss.dto.MediaFileValidationDTO(" +
            "ar.name, " +
            "af.title, " +
            "af.year, " +
            "c.num, " +
            "c.title, " +
            "mf.name, " +
            "mf.format) " +
            "FROM Artist AS ar " +
            "INNER JOIN Artifact af ON af.artist = ar " +
            "LEFT OUTER JOIN Composition c ON c.artifact = af " +
            "LEFT OUTER JOIN MediaFile mf ON mf.composition = c " +
            "WHERE af.artifactType = ?1 " +
            "ORDER BY ar.name, af.year, af.title, c.num"
    )
    List<MediaFileValidationDTO> getMediaFileValidationMusic(ArtifactType artifactType);
}
