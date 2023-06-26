package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.TrackFlatDTO;
import com.romanpulov.odeonwss.dto.TrackValidationDTO;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Track;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface TrackRepository extends CrudRepository<Track, Long> {

    @Query("SELECT c FROM Track AS c LEFT JOIN FETCH c.dvProducts WHERE c.id = :id")
    Optional<Track> findByIdWithProducts(Long id);

    @Query("SELECT c FROM Track AS c LEFT JOIN FETCH c.mediaFiles WHERE c.id = :id")
    Optional<Track> findByIdWithMediaFiles(Long id);

    List<Track> findAllByArtifact(Artifact artifact);

    @Query(
            "SELECT c " +
            "FROM Track c " +
            "INNER JOIN Artifact a ON c.artifact = a " +
            "WHERE a.artifactType=:artifactType"
    )
    List<Track> getTracksByArtifactType(ArtifactType artifactType);

    @Query("SELECT " +
            "new com.romanpulov.odeonwss.dto.TrackValidationDTO(" +
            "ar.name, " +
            "af.title, " +
            "af.year, " +
            "c.num, " +
            "c.title) " +
            "FROM Artist AS ar " +
            "INNER JOIN Artifact af ON af.artist = ar " +
            "LEFT OUTER JOIN Track c ON c.artifact = af " +
            "WHERE af.artifactType = ?1 " +
            "ORDER BY ar.name, af.year, af.title, c.num"
    )
    List<TrackValidationDTO> getTrackValidationMusic(ArtifactType artifactType);

    @Query("SELECT " +
            "c.id AS id, " +
            "c.diskNum AS diskNum, " +
            "c.num AS num, " +
            "ar.id AS artistId, " +
            "ar.name AS artistName, " +
            "par.id AS performerArtistId, " +
            "par.name AS performerArtistName, " +
            "dvt.id AS dvTypeId, " +
            "dvt.name AS dvTypeName, " +
            "c.title AS title, " +
            "c.duration AS duration, " +
            "m.size AS size, " +
            "m.bitrate AS bitRate, " +
            "m.name AS fileName " +
            "FROM Track c " +
            "LEFT OUTER JOIN TrackMediaFile cm ON cm.trackId = c.id " +
            "LEFT OUTER JOIN MediaFile m ON m.id = cm.mediaFileId " +
            "LEFT OUTER JOIN Artist ar ON ar = c.artist " +
            "LEFT OUTER JOIN Artist par ON par = c.performerArtist " +
            "LEFT OUTER JOIN DVType dvt ON dvt = c.dvType " +
            "WHERE c.artifact = :artifact " +
            "ORDER BY c.diskNum, c.num, ar.name, c.title, m.name")
    List<TrackFlatDTO> findAllFlatDTOByArtifact(Artifact artifact);

    @Query("SELECT " +
            "c.id AS id, " +
            "c.num AS num, " +
            "dvt.id AS dvTypeId, " +
            "dvt.name AS dvTypeName, " +
            "c.title AS title, " +
            "c.duration AS duration, " +
            "dvp.id AS dvProductId, " +
            "dvp.title AS dvProductTitle " +
            "FROM Track c " +
            "INNER JOIN Artifact a ON c.artifact = a " +
            "LEFT OUTER JOIN DVType dvt ON dvt = c.dvType " +
            "LEFT OUTER JOIN TrackDVProduct tp ON tp.trackId = c.id " +
            "LEFT OUTER JOIN DVProduct dvp ON tp.dvProductId = dvp.id " +
            "WHERE a.artifactType.id = :artifactTypeId " +
            "ORDER BY a.title, c.num, c.title")
    List<TrackFlatDTO> findAllFlatDTOByArtifactTypeId(Long artifactTypeId);

    @Query("SELECT " +
            "c.id AS id, " +
            "c.num AS num, " +
            "a.id AS artifactId, " +
            "a.title AS artifactTitle, " +
            "dvt.id AS dvTypeId, " +
            "dvt.name AS dvTypeName, " +
            "c.title AS title, " +
            "c.duration AS duration, " +
            "dvp.id AS dvProductId, " +
            "dvp.title AS dvProductTitle, " +
            "m.name AS fileName " +
            "FROM Track c " +
            "INNER JOIN Artifact a ON c.artifact = a " +
            "INNER JOIN TrackDVProduct tp ON tp.trackId = c.id " +
            "INNER JOIN DVProduct dvp ON tp.dvProductId = dvp.id " +
            "LEFT OUTER JOIN TrackMediaFile cm ON cm.trackId = c.id " +
            "LEFT OUTER JOIN MediaFile m ON m.id = cm.mediaFileId " +
            "LEFT OUTER JOIN DVType dvt ON dvt = c.dvType " +
            "WHERE dvp.id = :dvProductId " +
            "ORDER BY a.title, c.num, c.title, m.name")
    List<TrackFlatDTO> findAllFlatDTOByDvProductId(Long dvProductId);

    Optional<Track> findTrackByArtifactAndTitle(Artifact artifact, String title);
}
