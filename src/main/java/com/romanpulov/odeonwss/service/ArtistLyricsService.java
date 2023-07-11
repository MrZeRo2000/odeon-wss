package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.ArtistLyricsDTO;
import com.romanpulov.odeonwss.dto.TextDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistLyrics;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.mapper.ArtistLyricsMapper;
import com.romanpulov.odeonwss.repository.ArtistLyricsRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistLyricsService
        extends AbstractEntityService<ArtistLyrics, ArtistLyricsDTO, ArtistLyricsRepository>
        implements EditableObjectService <ArtistLyricsDTO> {

    public ArtistLyricsService(
            ArtistRepository artistRepository,
            ArtistLyricsRepository artistLyricsRepository,
            ArtistLyricsMapper artistLyricsMapper) {
        super(artistLyricsRepository, artistLyricsMapper);

        this.setOnBeforeSaveEntityHandler(entity -> {
            Artist artist = entity.getArtist();
            if (artist != null && artist.getId() != null && !artistRepository.existsById(artist.getId())) {
                throw new CommonEntityNotFoundException("Artist", artist.getId());
            }
        });
    }

    public List<ArtistLyricsDTO> getTable() {
        return repository.findAllDTO();
    }

    public List<ArtistLyricsDTO> getTable(Long artistId) {
        return repository.findAllDTOByArtistId(artistId);
    }

    public TextDTO getText(Long id) throws CommonEntityNotFoundException {
        Optional<TextDTO> textView = repository.findArtistLyricsById(id);
        if (textView.isPresent()) {
            return textView.get();
        } else {
            throw new CommonEntityNotFoundException("ArtistLyrics", id);
        }
    }
}
