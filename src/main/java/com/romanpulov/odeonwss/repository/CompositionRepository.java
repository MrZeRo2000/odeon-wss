package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.CompositionTableDTO;
import com.romanpulov.odeonwss.dto.CompositionValidationDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Composition;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CompositionRepository extends CrudRepository<Composition, Long> {

    List<Composition> findAllByArtifact(Artifact artifact);

    @Query("SELECT " +
            "new com.romanpulov.odeonwss.dto.CompositionValidationDTO(" +
            "ar.name, " +
            "af.title, " +
            "af.year, " +
            "c.num, " +
            "c.title) " +
            "FROM Artist AS ar " +
            "INNER JOIN Artifact af ON af.artist = ar " +
            "LEFT OUTER JOIN Composition c ON c.artifact = af " +
            "WHERE af.artifactType = ?1 " +
            "ORDER BY ar.name, af.year, af.title, c.num"
    )
    List<CompositionValidationDTO> getCompositionValidationMusic(ArtifactType artifactType);

    @Query("SELECT new com.romanpulov.odeonwss.dto.CompositionTableDTO(" +
            "c.id, " +
            "c.diskNum, " +
            "c.num, " +
            "ar.id, " +
            "ar.name, " +
            "par.id, " +
            "par.name, " +
            "c.title, " +
            "c.duration, " +
            "m.size, " +
            "m.bitrate, " +
            "m.name " +
            ") " +
            "FROM Composition c " +
            "LEFT OUTER JOIN CompositionMediaFile cm ON cm.compositionId = c.id " +
            "LEFT OUTER JOIN MediaFile m ON m.id = cm.mediaFileId " +
            "LEFT OUTER JOIN Artist ar ON ar = c.artist " +
            "LEFT OUTER JOIN Artist par ON par = c.performerArtist " +
            "WHERE c.artifact.id = :id " +
            "ORDER BY c.diskNum, c.num, ar.name, c.title")
    List<CompositionTableDTO> getCompositionTableByArtifactId(Long id);

    @Query("SELECT new com.romanpulov.odeonwss.dto.CompositionTableDTO(" +
            "c.id, " +
            "c.diskNum, " +
            "c.num, " +
            "ar.id, " +
            "ar.name, " +
            "par.id, " +
            "par.name, " +
            "c.title, " +
            "c.duration, " +
            "m.size, " +
            "m.bitrate, " +
            "m.name " +
            ") " +
            "FROM Composition c " +
            "LEFT OUTER JOIN CompositionMediaFile cm ON cm.compositionId = c.id " +
            "LEFT OUTER JOIN MediaFile m ON m.id = cm.mediaFileId " +
            "LEFT OUTER JOIN Artist ar ON ar = c.artist " +
            "LEFT OUTER JOIN Artist par ON par = c.performerArtist " +
            "WHERE c.artifact = :artifact " +
            "ORDER BY c.diskNum, c.num, ar.name, c.title")
    List<CompositionTableDTO> getCompositionTableByArtifact(Artifact artifact);

}
