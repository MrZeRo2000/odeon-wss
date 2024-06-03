package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.MediaFileDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.entity.MediaFile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface MediaFileRepository extends EntityDTORepository<MediaFile, MediaFileDTO> {
    Optional<MediaFile> findFirstByArtifact(Artifact artifact);

    Optional<MediaFile> findFirstByArtifactAndName(Artifact artifact, String name);

    List<MediaFile> findAllByArtifactId(Long artifactId);

    @Transactional
    void deleteAllByArtifact(Artifact artifact);

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

    @Query("""
      SELECT
        mf.id AS id,
        mf.artifact.id AS artifactId,
        mf.artifact.title AS artifactTitle,
        mf.name AS name,
        mf.size AS size,
        mf.bitrate AS bitrate,
        mf.duration AS duration,
        mf.width AS width,
        mf.height AS height
      FROM MediaFile as mf
      INNER JOIN Artifact as a ON mf.artifact = a
      WHERE a.artifactType = :artifactType
    """
    )
    List<MediaFileDTO> findAllDTOByArtifactType(ArtifactType artifactType);

    @Query("""
        SELECT
          m.id AS id,
          m.artifact.id AS artifactId,
          m.name AS name,
          m.format AS format,
          m.size AS size,
          m.bitrate AS bitrate,
          m.duration AS duration,
          m.width AS width,
          m.height AS height,
          m.extra AS extra
        FROM MediaFile m
        WHERE m.id = :id
    """)
    Optional<MediaFileDTO> findDTOById(Long id);

    @Query("""
        SELECT
          m.id AS id,
          m.name AS name,
          m.format AS format,
          m.size AS size,
          m.bitrate AS bitrate,
          m.duration AS duration,
          m.width AS width,
          m.height AS height,
          CASE WHEN m.extra IS NOT NULL THEN 1 END AS hasExtra
        FROM MediaFile m
        WHERE m.artifact.id = :artifactId
        ORDER BY m.name
    """)
    List<MediaFileDTO> findAllDTOByArtifactId(long artifactId);

    @Query("""
        SELECT
          m.id AS id,
          m.name AS name,
          m.duration AS duration
        FROM MediaFile m
        WHERE m.artifact.id = :artifactId
        ORDER BY m.name
    """)
    List<MediaFileDTO> findAllDTOIdNameDurationByArtifactId(long artifactId);

    @Query("""
        SELECT
            ar.name AS artistName,
            af.title AS artifactTitle,
            af.id AS artifactId,
            af.year AS artifactYear,
            c.num AS trackNum,
            c.title AS trackTitle,
            m.name AS mediaFileName,
            m.format AS mediaFileFormat,
            m.size AS mediaFileSize
        FROM Artist AS ar
        INNER JOIN Artifact af ON af.artist = ar
        LEFT OUTER JOIN Track c ON c.artifact = af
        LEFT OUTER JOIN TrackMediaFile cm ON cm.trackId = c.id
        LEFT OUTER JOIN MediaFile m ON m.id = cm.mediaFileId
        WHERE ar.type = :artistType AND af.artifactType = :artifactType
        ORDER BY ar.name, af.year, af.title, c.num
    """
    )
    List<MediaFileValidationDTO> getTrackMediaFileValidationMusic(ArtistType artistType, ArtifactType artifactType);

    @Query(""" 
        SELECT
            af.id AS artifactId,
            af.title AS artifactTitle,
            af.year AS artifactYear,
            m.name AS mediaFileName,
            m.format AS mediaFileFormat,
            m.size AS mediaFileSize
        FROM Artifact af
        LEFT OUTER JOIN Track c ON c.artifact = af
        LEFT OUTER JOIN TrackMediaFile cm ON cm.trackId = c.id
        LEFT OUTER JOIN MediaFile m ON m.id = cm.mediaFileId
        WHERE af.artifactType = :artifactType
        ORDER BY af.title
    """
    )
    List<MediaFileValidationDTO> getTrackMediaFileValidationDV(ArtifactType artifactType);

    @Query("""
        SELECT
            af.id AS artifactId,
            af.title AS artifactTitle,
            af.year AS artifactYear,
            af.size AS artifactSize,
            af.duration AS artifactDuration,
            m.name AS mediaFileName,
            m.format AS mediaFileFormat,
            m.bitrate AS mediaFileBitrate,
            m.size AS mediaFileSize,
            m.duration AS mediaFileDuration,
            m.width AS mediaFileWidth,
            m.height AS mediaFileHeight
        FROM Artifact af
        LEFT OUTER JOIN MediaFile m ON m.artifact = af
        WHERE af.artifactType = :artifactType
        ORDER BY af.title
    """
    )
    List<MediaFileValidationDTO> getArtifactMediaFileValidationDV(ArtifactType artifactType);

    @Query("""
        SELECT
            ar.name AS artistName,
            af.id AS artifactId,
            af.title AS artifactTitle,
            af.year AS artifactYear,
            af.size AS artifactSize,
            af.duration AS artifactDuration,
            m.name AS mediaFileName,
            m.format AS mediaFileFormat,
            m.bitrate AS mediaFileBitrate,
            m.size AS mediaFileSize,
            m.duration AS mediaFileDuration,
            m.width AS mediaFileWidth,
            m.height AS mediaFileHeight
        FROM Artist AS ar
        INNER JOIN Artifact af ON af.artist = ar
        LEFT OUTER JOIN MediaFile m ON m.artifact = af
        WHERE ar.type = :artistType AND af.artifactType = :artifactType
        ORDER BY ar.name, af.year, af.title, m.name
    """
    )
    List<MediaFileValidationDTO> getArtifactMediaFileValidationMusic(ArtistType artistType, ArtifactType artifactType);

}
