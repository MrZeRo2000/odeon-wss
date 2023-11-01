package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.TrackDTO;
import com.romanpulov.odeonwss.dto.TrackFlatDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Track;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface TrackRepository extends EntityDTORepository<Track, TrackDTO> {

    @Query("SELECT c FROM Track AS c LEFT JOIN FETCH c.dvProducts WHERE c.id = :id")
    Optional<Track> findByIdWithProducts(Long id);

    @Query("SELECT c FROM Track AS c LEFT JOIN FETCH c.mediaFiles WHERE c.id = :id")
    Optional<Track> findByIdWithMediaFiles(Long id);

    List<Track> findAllByArtifact(Artifact artifact);

    @Query("""
        SELECT
            c
        FROM Track c
        INNER JOIN Artifact a ON c.artifact = a
        WHERE a.artifactType=:artifactType
    """
    )
    List<Track> getTracksByArtifactType(ArtifactType artifactType);

    @Query("""
        SELECT
            c.id AS id,
            c.diskNum AS diskNum,
            c.num AS num,
            ar.id AS artistId,
            ar.name AS artistName,
            par.id AS performerArtistId,
            par.name AS performerArtistName,
            dvt.id AS dvTypeId,
            dvt.name AS dvTypeName,
            c.title AS title,
            c.duration AS duration,
            m.size AS size,
            m.bitrate AS bitRate,
            m.name AS mediaFileName,
            tp.dvProductId AS dvProductId
        FROM Track c
        LEFT OUTER JOIN TrackMediaFile cm ON cm.trackId = c.id
        LEFT OUTER JOIN MediaFile m ON m.id = cm.mediaFileId
        LEFT OUTER JOIN Artist ar ON ar = c.artist
        LEFT OUTER JOIN Artist par ON par = c.performerArtist
        LEFT OUTER JOIN DVType dvt ON dvt = c.dvType
        LEFT OUTER JOIN TrackDVProduct tp ON tp.trackId = c.id
        WHERE c.artifact.id = :artifactId
        ORDER BY c.diskNum, c.num, ar.name, c.title, m.name
    """            
    )
    List<TrackFlatDTO> findAllFlatDTOByArtifactId (long artifactId);

    @Query("""
        SELECT
            c.id AS id,
            a.id AS artifactId,
            a.title AS artifactTitle,
            a.year AS artifactYear,
            a.duration AS artifactDuration,
            ar.id AS artifactArtistId,
            ar.name AS artifactArtistName,
            c.num AS num,
            dvt.id AS dvTypeId,
            dvt.name AS dvTypeName,
            c.title AS title,
            c.duration AS duration,
            dvp.id AS dvProductId,
            dvp.title AS dvProductTitle
        FROM Track c
        INNER JOIN Artifact a ON c.artifact = a
        LEFT OUTER JOIN Artist ar ON ar = a.artist
        LEFT OUTER JOIN DVType dvt ON dvt = c.dvType
        LEFT OUTER JOIN TrackDVProduct tp ON tp.trackId = c.id
        LEFT OUTER JOIN DVProduct dvp ON tp.dvProductId = dvp.id
        WHERE a.artifactType.id = :artifactTypeId
        ORDER BY a.title, c.num, c.title
    """
    )
    List<TrackFlatDTO> findAllFlatDTOByArtifactTypeId(Long artifactTypeId);
    
    @Query("""
        SELECT
            c.id AS id,
            c.num AS num,
            a.id AS artifactId,
            a.title AS artifactTitle,
            dvt.id AS dvTypeId,
            dvt.name AS dvTypeName,
            c.title AS title,
            c.duration AS duration,
            dvp.id AS dvProductId,
            dvp.title AS dvProductTitle,
            m.name AS mediaFileName
        FROM Track c
        INNER JOIN Artifact a ON c.artifact = a
        INNER JOIN TrackDVProduct tp ON tp.trackId = c.id
        INNER JOIN DVProduct dvp ON tp.dvProductId = dvp.id
        LEFT OUTER JOIN TrackMediaFile cm ON cm.trackId = c.id
        LEFT OUTER JOIN MediaFile m ON m.id = cm.mediaFileId
        LEFT OUTER JOIN DVType dvt ON dvt = c.dvType
        WHERE dvp.id = :dvProductId
        ORDER BY a.title, c.num, c.title, m.name
    """
    )
    List<TrackFlatDTO> findAllFlatDTOByDvProductId(Long dvProductId);

    @Query("""
        SELECT
            c.id AS id,
            c.num AS num,
            c.diskNum AS diskNum,
            c.artifact.id AS artifactId,
            ar.id AS artistId,
            ar.name AS artistName,
            par.id AS performerArtistId,
            par.name AS performerArtistName,
            dvt.id AS dvTypeId,
            dvt.name AS dvTypeName,
            c.title AS title,
            c.duration AS duration,
            dvp.id AS dvProductId,
            dvp.title AS dvProductTitle,
            m.id AS mediaFileId,
            m.name AS mediaFileName
        FROM Track c
        LEFT OUTER JOIN Artist ar ON ar = c.artist
        LEFT OUTER JOIN Artist par ON par = c.performerArtist
        LEFT OUTER JOIN TrackDVProduct tp ON tp.trackId = c.id
        LEFT OUTER JOIN DVProduct dvp ON tp.dvProductId = dvp.id
        LEFT OUTER JOIN TrackMediaFile cm ON cm.trackId = c.id
        LEFT OUTER JOIN MediaFile m ON m.id = cm.mediaFileId
        LEFT OUTER JOIN DVType dvt ON dvt = c.dvType
        WHERE c.id = :id
        ORDER BY m.name
   """
    )    
    List<TrackFlatDTO> findFlatDTOById(long id);

    Optional<Track> findTrackByArtifactAndTitle(Artifact artifact, String title);
}
