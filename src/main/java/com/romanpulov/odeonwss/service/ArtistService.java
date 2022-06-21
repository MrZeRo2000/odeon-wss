package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.ArtistCategoriesDetailDTO;
import com.romanpulov.odeonwss.dto.ArtistCategoryDetailDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistCategory;
import com.romanpulov.odeonwss.entity.ArtistDetail;
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
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ArtistService implements EditableObjectService<ArtistCategoriesDetailDTO> {
    private final Logger logger = LoggerFactory.getLogger(ArtistService.class);

    private final ArtistCategoryRepository artistCategoryRepository;

    private final ArtistDetailRepository artistDetailRepository;

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistCategoryRepository artistCategoryRepository, ArtistDetailRepository artistDetailRepository, ArtistRepository artistRepository) {
        this.artistCategoryRepository = artistCategoryRepository;
        this.artistDetailRepository = artistDetailRepository;
        this.artistRepository = artistRepository;
    }

    @Override
    public ArtistCategoriesDetailDTO getById(Long id) throws CommonEntityNotFoundException {
        List<ArtistCategoryDetailDTO> acdList = artistCategoryRepository.getArtistCategoryDetailsByArtistId(id);
        if (acdList.size() == 0) {
            throw new CommonEntityNotFoundException("Artist Category Details", id);
        } else {
            return ArtistCategoryMapper.fromArtistCategoryDetailDTO(acdList);
        }
    }

    @Override
    @Transactional
    public ArtistCategoriesDetailDTO insert(ArtistCategoriesDetailDTO acd)
            throws CommonEntityAlreadyExistsException, CommonEntityNotFoundException
    {
        // check artist
        Optional<Artist> existingArtist = artistRepository.findFirstByName(acd.getArtistName());
        if (existingArtist.isPresent()) {
            throw new CommonEntityAlreadyExistsException(existingArtist.get().getName(), existingArtist.get().getId());
        }

        // save artist
        Artist artist = artistRepository.save(ArtistMapper.createFromArtistCategoriesDetailDTO(acd));

        // save artist detail
        if ((acd.getArtistBiography() != null) && !acd.getArtistBiography().isBlank()) {
            artistDetailRepository.save(ArtistDetailMapper.createFromArtistCategoriesDetailDTO(artist, acd));
        }

        // save artist categories
        List<ArtistCategory> artistCategories = ArtistCategoryMapper.createFromArtistCategoriesDetailDTO(artist, acd);
        if (artistCategories.size() > 0) {
            artistCategoryRepository.saveAll(artistCategories);
        }

        return getById(artist.getId());
    }

    @Override
    @Transactional
    public ArtistCategoriesDetailDTO update(ArtistCategoriesDetailDTO acd)
            throws CommonEntityAlreadyExistsException, CommonEntityNotFoundException {
        // check existing artist
        Optional<Artist> existingArtist = artistRepository.findById(acd.getId());
        if (existingArtist.isPresent()) {
            Artist artist = existingArtist.get();

            // check for artist after name change
            Optional<Artist> newArtist = artistRepository.findFirstByName(acd.getArtistName());
            if (newArtist.isPresent() && !newArtist.get().getId().equals(artist.getId())) {
                throw new CommonEntityAlreadyExistsException(acd.getArtistName(), newArtist.get().getId());
            }

            // save artist
            artistRepository.save(ArtistMapper.updateFromArtistCategoriesDetailDTO(artist, acd));

            // save artist detail
            Optional<ArtistDetail> existingArtistDetail = artistDetailRepository.findArtistDetailByArtist(artist);
            if (existingArtistDetail.isPresent()) {
                if ((acd.getArtistBiography() != null) && !acd.getArtistBiography().isBlank()) {
                    artistDetailRepository.save(ArtistDetailMapper.updateFromArtistCategoriesDetailDTO(existingArtistDetail.get(), acd));
                } else {
                    artistDetailRepository.delete(existingArtistDetail.get());
                }
            } else {
                if ((acd.getArtistBiography() != null) && !acd.getArtistBiography().isBlank()) {
                    artistDetailRepository.save(ArtistDetailMapper.createFromArtistCategoriesDetailDTO(artist, acd));
                }
            }

            // get artist categories
            List<ArtistCategory> existingCategories = artistCategoryRepository.getArtistCategoriesByArtistOrderByTypeAscNameAsc(artist);
            List<ArtistCategory> categories = ArtistCategoryMapper.createFromArtistCategoriesDetailDTO(artist, acd);

            // merge new with existing
            Pair<List<ArtistCategory>, List<ArtistCategory>> mergedCategories = ArtistCategoryMapper.mergeCategories(existingCategories, categories);

            // artist categories deleted
            if (mergedCategories.getSecond().size() > 0) {
                artistCategoryRepository.deleteAll(mergedCategories.getSecond());
            }

            // artist categories created
            if (mergedCategories.getFirst().size() > 0) {
                artistCategoryRepository.saveAll(mergedCategories.getFirst());
            }

            return getById(artist.getId());
        } else {
            throw new CommonEntityNotFoundException("Artist", acd.getId());
        }
    }

    @Override
    public void deleteById(Long id) throws CommonEntityNotFoundException {
        Optional<Artist> existingArtist = artistRepository.findById(id);
        if (existingArtist.isPresent()) {
            artistRepository.delete(existingArtist.get());
        } else {
            throw new CommonEntityNotFoundException("Artist", id);
        }
    }
}
