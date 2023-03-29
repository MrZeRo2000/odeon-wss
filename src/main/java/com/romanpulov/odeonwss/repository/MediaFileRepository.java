package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.MediaFileEditDTO;
import com.romanpulov.odeonwss.dto.MediaFileTableDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.dto.IdNameDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface MediaFileRepository extends CrudRepository<MediaFile, Long> {
    Optional<MediaFile> findFirstByArtifact(Artifact artifact);

    Optional<MediaFile> findFirstByArtifactAndName(Artifact artifact, String name);

    List<MediaFile> findAllByArtifact(Artifact artifact);

    List<IdNameDTO> findByArtifactOrderByName(Artifact artifact);

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
            "LEFT OUTER JOIN Track c ON c.artifact = af " +
            "LEFT OUTER JOIN TrackMediaFile cm ON cm.trackId = c.id " +
            "LEFT OUTER JOIN MediaFile m ON m.id = cm.mediaFileId " +
            "WHERE ar.type = :artistType AND af.artifactType = :artifactType " +
            "ORDER BY ar.name, af.year, af.title, c.num"
    )
    List<MediaFileValidationDTO> getTrackMediaFileValidationMusic(ArtistType artistType, ArtifactType artifactType);

    @Query("SELECT " +
            "new com.romanpulov.odeonwss.dto.MediaFileValidationDTO(" +
            "af.title, " +
            "af.year, " +
            "m.name, " +
            "m.format) " +
            "FROM Artifact af " +
            "LEFT OUTER JOIN Track c ON c.artifact = af " +
            "LEFT OUTER JOIN TrackMediaFile cm ON cm.trackId = c.id " +
            "LEFT OUTER JOIN MediaFile m ON m.id = cm.mediaFileId " +
            "WHERE af.artifactType = :artifactType " +
            "ORDER BY af.title"
    )
    List<MediaFileValidationDTO> getTrackMediaFileValidationDV(ArtifactType artifactType);

    @Query("SELECT " +
            "new com.romanpulov.odeonwss.dto.MediaFileValidationDTO(" +
            "af.title, " +
            "af.year, " +
            "m.name, " +
            "m.format) " +
            "FROM Artifact af " +
            "LEFT OUTER JOIN MediaFile m ON m.artifact = af " +
            "WHERE af.artifactType = :artifactType " +
            "ORDER BY af.title"
    )
    List<MediaFileValidationDTO> getArtifactMediaFileValidationDV(ArtifactType artifactType);

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
    List<MediaFileValidationDTO> getArtifactMediaFileValidationMusic(ArtifactType artifactType);

    @Query(
            "SELECT new com.romanpulov.odeonwss.dto.MediaFileEditDTO(" +
                    "mf.id, " +
                    "mf.artifact.id, " +
                    "mf.name, " +
                    "mf.format, " +
                    "mf.size, " +
                    "mf.bitrate, " +
                    "mf.duration" +
                    ") " +
                    "FROM MediaFile as mf " +
                    "WHERE mf.id = :id"
    )
    Optional<MediaFileEditDTO> getMediaFileEditById(Long id);

    @Query(
            "SELECT new com.romanpulov.odeonwss.dto.MediaFileTableDTO(" +
                    "mf.id, " +
                    "mf.name, " +
                    "mf.format, " +
                    "mf.size, " +
                    "mf.bitrate, " +
                    "mf.duration" +
                    ") " +
                    "FROM MediaFile as mf " +
                    "WHERE mf.artifact = :artifact " +
                    "ORDER BY mf.name"
    )
    List<MediaFileTableDTO> getMediaFileTableByArtifact(Artifact artifact);

    @Query(
            "SELECT mf " +
            "FROM MediaFile as mf " +
            "INNER JOIN FETCH mf.artifact as a " +
            "WHERE a.artifactType = :artifactType " +
            "AND mf.size = 0"
    )
    List<MediaFile> getMediaFilesWithEmptySizeByArtifactType(ArtifactType artifactType);

    @Query(
            "SELECT mf " +
            "FROM MediaFile mf " +
            "INNER JOIN FETCH mf.artifact AS a " +
            "WHERE a.artifactType = :artifactType"
    )
    List<MediaFile> getMediaFilesByArtifactType(ArtifactType artifactType);
}
