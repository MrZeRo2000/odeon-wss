package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.ArtistLyricsEditDTO;
import com.romanpulov.odeonwss.dto.ArtistLyricsTableDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistLyrics;
import com.romanpulov.odeonwss.exception.CommonEntityAlreadyExistsException;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.ArtistLyricsMapper;
import com.romanpulov.odeonwss.repository.ArtistLyricsRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.view.TextView;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistLyricsService implements EditableObjectService <ArtistLyricsEditDTO> {

    private final ArtistRepository artistRepository;

    private final ArtistLyricsRepository artistLyricsRepository;

    public ArtistLyricsService(ArtistRepository artistRepository, ArtistLyricsRepository artistLyricsRepository) {
        this.artistRepository = artistRepository;
        this.artistLyricsRepository = artistLyricsRepository;
    }

    public List<ArtistLyricsTableDTO> getTable() {
        return artistLyricsRepository.getArtistLyricsTableDTO();
    }

    public TextView getText(Long id) throws CommonEntityNotFoundException {
        Optional<TextView> textView = artistLyricsRepository.findArtistLyricsById(id);
        if (textView.isPresent()) {
            return textView.get();
        } else {
            throw new CommonEntityNotFoundException("ArtistLyrics", id);
        }
    }

    @Override
    public ArtistLyricsEditDTO getById(Long id) throws CommonEntityNotFoundException {
        Optional<ArtistLyricsEditDTO> existingDTO = artistLyricsRepository.getArtistListEditById(id);
        if (existingDTO.isPresent()) {
            return existingDTO.get();
        } else {
            throw new CommonEntityNotFoundException("ArtistLyrics", id);
        }
    }

    @Override
    public ArtistLyricsEditDTO insert(ArtistLyricsEditDTO o) throws CommonEntityAlreadyExistsException, CommonEntityNotFoundException {
        ArtistLyrics artistLyrics = ArtistLyricsMapper.fromEditDTO(o);
        artistLyricsRepository.save(artistLyrics);
        return getById(artistLyrics.getId());
    }

    @Override
    public ArtistLyricsEditDTO update(ArtistLyricsEditDTO o) throws CommonEntityAlreadyExistsException, CommonEntityNotFoundException {
        Optional<ArtistLyrics> existingEntity = artistLyricsRepository.findById(o.getId());
        if (existingEntity.isPresent()) {
            Optional<Artist> existingArtistEntity = artistRepository.findById(o.getArtistId());
            if (existingArtistEntity.isPresent()) {
                artistLyricsRepository.save(ArtistLyricsMapper.updateFromEditDTO(existingEntity.get(), o, existingArtistEntity.get()));
                return getById(o.getId());
            } else {
                throw new CommonEntityNotFoundException("Artist", o.getArtistId());
            }
        } else {
            throw new CommonEntityNotFoundException("ArtistLyrics", o.getId());
        }
    }

    @Override
    public void deleteById(Long id) throws CommonEntityNotFoundException {
        Optional<ArtistLyrics> existingEntity = artistLyricsRepository.findById(id);
        if (existingEntity.isPresent()) {
            artistLyricsRepository.delete(existingEntity.get());
        } else {
            throw new CommonEntityNotFoundException("ArtistLyrics", id);
        }
    }
}
