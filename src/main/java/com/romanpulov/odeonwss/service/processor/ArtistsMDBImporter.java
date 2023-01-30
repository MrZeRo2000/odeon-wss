package com.romanpulov.odeonwss.service.processor;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.ArtistCategoryRepository;
import com.romanpulov.odeonwss.repository.ArtistDetailRepository;
import com.romanpulov.odeonwss.repository.ArtistLyricsRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import static com.romanpulov.odeonwss.service.processor.MDBConst.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ArtistsMDBImporter {

    private final ArtistRepository artistRepository;

    private final ArtistDetailRepository artistDetailRepository;

    private final ArtistCategoryRepository artistCategoryRepository;

    private final ArtistLyricsRepository artistLyricsRepository;

    private final AbstractMDBImportProcessor.MDBReader mdbReader;

    private final ArtistType artistType;

    private Map<Long, Artist> migratedArtists;

    public Map<Long, Artist> getMigratedArtists() {
        return migratedArtists;
    }

    public ArtistsMDBImporter(
            ArtistRepository artistRepository,
            ArtistDetailRepository artistDetailRepository,
            ArtistCategoryRepository artistCategoryRepository,
            ArtistLyricsRepository artistLyricsRepository,
            AbstractMDBImportProcessor.MDBReader mdbReader,
            ArtistType artistType
    ) {
        this.artistRepository = artistRepository;
        this.artistDetailRepository = artistDetailRepository;
        this.artistCategoryRepository = artistCategoryRepository;
        this.artistLyricsRepository = artistLyricsRepository;
        this.mdbReader = mdbReader;
        this.artistType = artistType;
    }

    public int importArtists() throws ProcessorException {
        Table table = mdbReader.getTable(ARTISTLIST_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);
        migratedArtists = new HashMap<>();

        for (Row row: table) {
            ArtistType rowArtistType = row.getInt(SOURCE_COLUMN_NAME) == 5 ? ArtistType.CLASSICS : ArtistType.ARTIST;

            if (rowArtistType.equals(artistType)) {
                String artistName = row.getString(TITLE_COLUMN_NAME);
                long migrationId = row.getInt(ARTISTLIST_ID_COLUMN_NAME).longValue();
                artistRepository.findFirstByTypeAndName(rowArtistType, artistName).ifPresentOrElse(
                        artist -> {
                            artist.setMigrationId(migrationId);
                            migratedArtists.put(migrationId, artist);
                        },
                        () -> migratedArtists.put(migrationId,
                            artistRepository.findFirstByMigrationId(migrationId).orElseGet(
                                    () -> {
                                        Artist artist = new Artist();
                                        artist.setType(rowArtistType);
                                        artist.setName(artistName);
                                        artist.setMigrationId(migrationId);

                                        artistRepository.save(artist);
                                        counter.getAndIncrement();

                                        return artist;
                                    }
                            )
                        ));
            }
        }
        return counter.get();
    }

    public int importArtistDetails() throws ProcessorException {
        Table table = mdbReader.getTable(ARTISTLIST_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);

        for (Row row: table) {
            long artistMigrationId = row.getInt(ARTISTLIST_ID_COLUMN_NAME).longValue();
            String biography = row.getString(NOTES_COLUMN_NAME);
            Artist artist;

            if ((biography != null) && ((artist = migratedArtists.get(artistMigrationId)) != null)) {
                artistDetailRepository.findArtistDetailByArtist(artist).orElseGet(() -> {
                    ArtistDetail artistDetail = new ArtistDetail();
                    artistDetail.setArtist(artist);
                    artistDetail.setBiography(biography);

                    artistDetailRepository.save(artistDetail);
                    counter.getAndIncrement();

                    return artistDetail;
                });
            }
        }

        return counter.get();
    }

    public int importArtistCategories() throws ProcessorException {
        Table table = mdbReader.getTable(ARTISTLISTCAT_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);

        for (Row row: table) {
            long artistMigrationId = row.getInt(ARTISTLIST_ID_COLUMN_NAME).longValue();
            Artist artist = migratedArtists.get(artistMigrationId);

            if (artist != null) {
                long migrationId = row.getInt(ARTISTLISTCAT_ID_COLUMN_NAME).longValue();
                Short catId = row.getShort(CAT_ID_COLUMN_NAME);
                String title = row.getString(TITLE_COLUMN_NAME);

                if (catId != null && title != null) {
                    artistCategoryRepository.findFirstByMigrationId(migrationId).orElseGet(() -> {
                        ArtistCategory artistCategory = new ArtistCategory();
                        artistCategory.setArtist(artist);
                        artistCategory.setType(row.getShort(CAT_ID_COLUMN_NAME) == 0 ? ArtistCategoryType.GENRE : ArtistCategoryType.STYLE);
                        artistCategory.setName(cleanseArtistCategory(title));
                        artistCategory.setMigrationId(migrationId);

                        artistCategoryRepository.save(artistCategory);
                        counter.getAndIncrement();

                        return artistCategory;
                    });
                }
            }
        }

        return counter.get();
    }

    private String cleanseArtistCategory(String title) {
        return title.replace("/ ", "/");
    }

    public int importArtistLyrics() throws ProcessorException {
        Table table = mdbReader.getTable(ARTISTLYRICS_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);

        for (Row row: table) {
            long artistMigrationId = row.getInt(ARTISTLIST_ID_COLUMN_NAME).longValue();
            Artist artist = migratedArtists.get(artistMigrationId);

            if (artist != null) {
                String songName = row.getString(SONGNAME_COLUMN_NAME);
                String lyricsText = row.getString(LYRICSTEXT_COLUMN_NAME);

                if (songName != null && lyricsText != null) {
                    artistLyricsRepository.findFirstByArtistAndTitle(artist, songName).ifPresentOrElse(
                            artistLyrics -> {
                                artistLyrics.setText(lyricsText);
                                artistLyricsRepository.save(artistLyrics);
                            },
                            () -> {
                                ArtistLyrics artistLyrics = new ArtistLyrics();
                                artistLyrics.setArtist(artist);
                                artistLyrics.setTitle(songName);
                                artistLyrics.setText(lyricsText);

                                artistLyricsRepository.save(artistLyrics);
                                counter.getAndIncrement();
                            }
                    );
                }
            }
        }

        return counter.get();
    }

}
