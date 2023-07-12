package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistDetail;
import com.romanpulov.odeonwss.dto.BiographyDTO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface ArtistDetailRepository extends CrudRepository<ArtistDetail, Long> {
    Optional<ArtistDetail> findArtistDetailByArtist(Artist artist);

    Optional<BiographyDTO> findArtistDetailById(Long id);
}
