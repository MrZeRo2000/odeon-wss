package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.ArtistCategoriesDetailDTO;
import com.romanpulov.odeonwss.dto.ArtistCategoryDetailDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistCategory;
import com.romanpulov.odeonwss.entity.ArtistCategoryType;
import com.romanpulov.odeonwss.exception.CommonEntityAlreadyExistsException;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.ArtistCategoryMapper;
import com.romanpulov.odeonwss.mapper.ArtistDetailMapper;
import com.romanpulov.odeonwss.mapper.ArtistMapper;
import com.romanpulov.odeonwss.repository.ArtistCategoryRepository;
import com.romanpulov.odeonwss.repository.ArtistDetailRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ArtistService {
    private final Logger logger = LoggerFactory.getLogger(ArtistService.class);

    private final ArtistCategoryRepository artistCategoryRepository;

    private final ArtistDetailRepository artistDetailRepository;

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistCategoryRepository artistCategoryRepository, ArtistDetailRepository artistDetailRepository, ArtistRepository artistRepository) {
        this.artistCategoryRepository = artistCategoryRepository;
        this.artistDetailRepository = artistDetailRepository;
        this.artistRepository = artistRepository;
    }

    public ArtistCategoriesDetailDTO getACDById(Long id) throws CommonEntityNotFoundException {
        List<ArtistCategoryDetailDTO> acdList = artistCategoryRepository.getArtistCategoryDetailsByArtistId(id);
        if (acdList.size() == 0) {
            throw new CommonEntityNotFoundException("Artist Category Details", id);
        } else {
            return ArtistCategoryMapper.fromArtistCategoryDetailDTO(acdList);
        }
    }

    @Transactional
    public ArtistCategoriesDetailDTO insertACD(ArtistCategoriesDetailDTO acd)
            throws CommonEntityAlreadyExistsException, CommonEntityNotFoundException
    {
        // check artist
        Optional<Artist> existingArtist = artistRepository.findFirstByName(acd.getArtistName());
        if (existingArtist.isPresent()) {
            throw new CommonEntityAlreadyExistsException(existingArtist.get().getName(), existingArtist.get().getId());
        }

        // save artist
        Artist artist = artistRepository.save(ArtistMapper.fromArtistCategoriesDetailDTO(acd));

        // save artist detail
        artistDetailRepository.save(ArtistDetailMapper.fromArtistCategoriesDetailDTO(artist, acd));

        // save artist categories
        List<ArtistCategory> artistCategories = ArtistCategoryMapper.fromArtistCategoriesDetailDTO(artist, acd);
        if (artistCategories.size() > 0) {
            artistCategoryRepository.saveAll(artistCategories);
        }

        return getACDById(artist.getId());
    }
}
