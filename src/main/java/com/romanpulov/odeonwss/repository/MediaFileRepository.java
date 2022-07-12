package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.MediaFile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MediaFileRepository extends CrudRepository<MediaFile, Long> {
    Optional<MediaFile> findFirstByArtifact(Artifact artifact);

    Optional<MediaFile> findFirstByArtifactAndName(Artifact artifact, String name);

    List<MediaFile> findAllByArtifact(Artifact artifact);

    @Query("SELECT " +
            "new com.romanpulov.odeonwss.dto.MediaFileValidationDTO(" +
            "ar.name, " +
            "af.title, " +
            "af.year, " +
            "c.num, " +
            "c.title, " +
            "m.name, " +
            "m.format) " +
            "FROM Artist AS ar " +
            "INNER JOIN Artifact af ON af.artist = ar " +
            "LEFT OUTER JOIN Composition c ON c.artifact = af " +
            "LEFT OUTER JOIN CompositionMediaFile cm ON cm.compositionId = c.id " +
            "LEFT OUTER JOIN MediaFile m ON m.id = cm.mediaFileId " +
            "WHERE af.artifactType = ?1 " +
            "ORDER BY ar.name, af.year, af.title, c.num"
    )
    List<MediaFileValidationDTO> getCompositionMediaFileValidationMusic(ArtifactType artifactType);

    @Query("SELECT " +
            "new com.romanpulov.odeonwss.dto.MediaFileValidationDTO(" +
            "ar.name, " +
            "af.title, " +
            "af.year, " +
            "m.name, " +
            "m.format) " +
            "FROM Artist AS ar " +
            "INNER JOIN Artifact af ON af.artist = ar " +
            "LEFT OUTER JOIN MediaFile m ON m.artifact = af " +
            "WHERE af.artifactType = ?1 " +
            "ORDER BY ar.name, af.year, af.title, m.name"
    )
    List<MediaFileValidationDTO> getMediaFileValidationMusic(ArtifactType artifactType);
}
