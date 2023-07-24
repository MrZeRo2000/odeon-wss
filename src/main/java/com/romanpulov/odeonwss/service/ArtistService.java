package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.*;
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

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ArtistService
        extends AbstractEntityService<Artist, ArtistDTO, ArtistRepository>
        implements EditableObjectService<ArtistDTO> {
    private final Logger logger = LoggerFactory.getLogger(ArtistService.class);

    private final ArtistCategoryRepository artistCategoryRepository;

    private final ArtistCategoryMapper artistCategoryMapper;

    private final ArtistDetailRepository artistDetailRepository;

    private final ArtistDetailMapper artistDetailMapper;

    private final ArtistTransformer artistTransformer;

    public ArtistService(
            ArtistCategoryRepository artistCategoryRepository,
            ArtistCategoryMapper artistCategoryMapper,
            ArtistDetailRepository artistDetailRepository,
            ArtistDetailMapper artistDetailMapper,
            ArtistRepository artistRepository,
            ArtistMapper artistMapper,
            ArtistTransformer artistTransformer) {
        super(artistRepository, artistMapper);
        this.artistCategoryRepository = artistCategoryRepository;
        this.artistCategoryMapper = artistCategoryMapper;
        this.artistDetailRepository = artistDetailRepository;
        this.artistDetailMapper = artistDetailMapper;
        this.artistTransformer = artistTransformer;
    }

    @Override
    @Transactional
    public ArtistDTO getById(Long id) throws CommonEntityNotFoundException {
        List<ArtistFlatDTO> flatDTOS = repository.findFlatDTOById(id);
        if (flatDTOS.size() == 0) {
            throw new CommonEntityNotFoundException(this.entityName, id);
        } else {
            return artistTransformer.transform(flatDTOS).get(0);
        }
    }

    @Override
    @Transactional
    public ArtistDTO insert(ArtistDTO acd)
            throws CommonEntityAlreadyExistsException, CommonEntityNotFoundException
    {
        // check artist
        Optional<Artist> existingArtist = repository.findFirstByName(acd.getArtistName());
        if (existingArtist.isPresent()) {
            throw new CommonEntityAlreadyExistsException(existingArtist.get().getName(), existingArtist.get().getId());
        }

        // save artist
        Artist artist = repository.save(mapper.fromDTO(acd));

        // save artist detail
        if ((acd.getArtistBiography() != null) && !acd.getArtistBiography().isBlank()) {
            artistDetailRepository.save(artistDetailMapper.fromArtistDTO(artist, acd));
        }

        // save artist categories
        List<ArtistCategory> artistCategories = artistCategoryMapper.createFromArtistDTO(artist, acd);
        if (artistCategories.size() > 0) {
            artistCategoryRepository.saveAll(artistCategories);
        }

        return this.getById(artist.getId());
    }

    @Override
    @Transactional
    public ArtistDTO update(ArtistDTO acd)
            throws CommonEntityAlreadyExistsException, CommonEntityNotFoundException {
        // check existing artist
        Optional<Artist> existingArtist = repository.findById(acd.getId());
        if (existingArtist.isPresent()) {
            Artist artist = existingArtist.get();

            // check for artist after name change
            Optional<Artist> newArtist = repository.findFirstByName(acd.getArtistName());
            if (newArtist.isPresent() && !newArtist.get().getId().equals(artist.getId())) {
                throw new CommonEntityAlreadyExistsException(acd.getArtistName(), newArtist.get().getId());
            }

            // save artist
            mapper.update(artist, acd);
            repository.save(artist);

            // save artist detail
            Optional<ArtistDetail> existingArtistDetail = artistDetailRepository.findArtistDetailByArtist(artist);
            if (existingArtistDetail.isPresent()) {
                if ((acd.getArtistBiography() != null) && !acd.getArtistBiography().isBlank()) {
                    artistDetailRepository.save(artistDetailMapper.update(existingArtistDetail.get(), acd));
                } else {
                    artistDetailRepository.delete(existingArtistDetail.get());
                }
            } else {
                if ((acd.getArtistBiography() != null) && !acd.getArtistBiography().isBlank()) {
                    artistDetailRepository.save(artistDetailMapper.fromArtistDTO(artist, acd));
                }
            }

            // get artist categories
            List<ArtistCategory> existingCategories = artistCategoryRepository.getArtistCategoriesByArtistOrderByTypeAscNameAsc(artist);
            List<ArtistCategory> categories = artistCategoryMapper.createFromArtistDTO(artist, acd);

            // merge new with existing
            Pair<List<ArtistCategory>, List<ArtistCategory>> mergedCategories = artistCategoryMapper.mergeCategories(existingCategories, categories);

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

    public List<ArtistDTO> getTable() {
        return artistTransformer.transform(repository.findAllFlatDTO());
    }
}
