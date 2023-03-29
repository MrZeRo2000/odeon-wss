package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.ArtistLyricsEditDTO;
import com.romanpulov.odeonwss.dto.ArtistLyricsTableDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistLyrics;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.ArtistLyricsMapper;
import com.romanpulov.odeonwss.repository.ArtistLyricsRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.dto.TextDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistLyricsService implements EditableObjectService <ArtistLyricsEditDTO> {

    private final ArtistRepository artistRepository;

    private final ArtistLyricsRepository artistLyricsRepository;

    private final ArtistLyricsMapper artistLyricsMapper;

    public ArtistLyricsService(
            ArtistRepository artistRepository,
            ArtistLyricsRepository artistLyricsRepository,
            ArtistLyricsMapper artistLyricsMapper) {
        this.artistRepository = artistRepository;
        this.artistLyricsRepository = artistLyricsRepository;
        this.artistLyricsMapper = artistLyricsMapper;
    }

    public List<ArtistLyricsTableDTO> getTable() {
        return artistLyricsRepository.getArtistLyricsTableDTO();
    }

    public List<ArtistLyricsTableDTO> getTable(Long artistId) {
        return artistLyricsRepository.getArtistLyricsTableByArtistDTO(artistId);
    }

    public TextDTO getText(Long id) throws CommonEntityNotFoundException {
        Optional<TextDTO> textView = artistLyricsRepository.findArtistLyricsById(id);
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
    public ArtistLyricsEditDTO insert(ArtistLyricsEditDTO o) throws CommonEntityNotFoundException {
        ArtistLyrics artistLyrics = artistLyricsMapper.fromDTO(o);
        artistLyricsRepository.save(artistLyrics);
        return getById(artistLyrics.getId());
    }

    @Override
    public ArtistLyricsEditDTO update(ArtistLyricsEditDTO o) throws CommonEntityNotFoundException {
        Optional<ArtistLyrics> existingEntity = artistLyricsRepository.findById(o.getId());
        if (existingEntity.isPresent()) {
            Optional<Artist> existingArtistEntity = artistRepository.findById(o.getArtistId());
            if (existingArtistEntity.isPresent()) {
                artistLyricsRepository.save(artistLyricsMapper.update(existingEntity.get(), o, existingArtistEntity.get()));
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
