package com.romanpulov.odeonwss.service.processor;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.*;
import com.romanpulov.odeonwss.service.CompositionService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.romanpulov.jutilscore.io.FileUtils.getExtension;
import static com.romanpulov.odeonwss.service.processor.MDBConst.*;

@Component
public class DVMusicMDBImportProcessor extends AbstractMDBImportProcessor {
    private static final int DV_MUSIC_REC_ID = 1253;

    private final ArtistRepository artistRepository;

    private final ArtistDetailRepository artistDetailRepository;

    private final ArtistCategoryRepository artistCategoryRepository;

    private final ArtistLyricsRepository artistLyricsRepository;

    private final ArtifactRepository artifactRepository;

    private final CompositionRepository compositionRepository;

    private final CompositionService compositionService;

    private final MediaFileRepository mediaFileRepository;

    private final DVTypeRepository dvTypeRepository;

    private Map<Long, Artist> migratedArtists;

    private Map<Integer, DVDet> dvDet;

    private Map<Long, DVType> dvTypeMap;

    public DVMusicMDBImportProcessor(
            ArtistRepository artistRepository,
            ArtistDetailRepository artistDetailRepository,
            ArtistCategoryRepository artistCategoryRepository,
            ArtistLyricsRepository artistLyricsRepository,
            ArtifactRepository artifactRepository,
            CompositionRepository compositionRepository,
            CompositionService compositionService,
            MediaFileRepository mediaFileRepository,
            DVTypeRepository dvTypeRepository
    ) {
        this.artistRepository = artistRepository;
        this.artistDetailRepository = artistDetailRepository;
        this.artistCategoryRepository = artistCategoryRepository;
        this.artistLyricsRepository = artistLyricsRepository;
        this.artifactRepository = artifactRepository;
        this.compositionRepository = compositionRepository;
        this.compositionService = compositionService;
        this.mediaFileRepository = mediaFileRepository;
        this.dvTypeRepository = dvTypeRepository;
    }

    @Override
    protected void importMDB(MDBReader mdbReader) throws ProcessorException {
        if (dvTypeMap == null) {
            dvTypeMap = dvTypeRepository.findAllMap();
        }

        infoHandler(ProcessorMessages.INFO_ARTIFACTS_IMPORTED, importArtifacts(mdbReader));
    }

    private static class DVCont {
        private int id;
        private String artifactName;
        private Artist artist;
        private Long year;
    }

    private static class DVDet {
        private final int id;
        private final Artist artist;
        private final String title;
        private final String fileName;
        private final int videoTypeId;

        public DVDet(int id, Artist artist, String title, String fileName, int videoTypeId) {
            this.id = id;
            this.artist = artist;
            this.title = title;
            this.fileName = fileName;
            this.videoTypeId = videoTypeId;
        }
    }

    private Artist getArtistByMigrationId(long migrationArtistId) throws ProcessorException {
        Artist artist = migratedArtists.getOrDefault(migrationArtistId, artistRepository.findFirstByMigrationId(migrationArtistId).orElseThrow(
                () -> new ProcessorException(String.format("Unable to find artist by migrationId: {%d}", migrationArtistId))
        ));
        migratedArtists.put(migrationArtistId, artist);

        return artist;
    }

    public int importArtifacts(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(DVCONT_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);
        migratedArtists = new HashMap<>();

        for (Row row: table) {
            if (row.getInt(REC_ID_COLUMN_NAME) == DV_MUSIC_REC_ID) {
                String artifactName = row.getString(FILE_NAME_COLUMN_NAME);

                Artist artist = getArtistByMigrationId(row.getInt(ARTISTLIST_ID_COLUMN_NAME).longValue());

                Long year = row.getInt(YEAR_COLUMN_NAME) == null ? null : row.getInt(YEAR_COLUMN_NAME).longValue();

                Artifact artifact = artifactRepository.findFirstByArtifactTypeAndArtistAndTitleAndYear(
                        ArtifactType.withDVMusic(),
                        artist,
                        artifactName,
                        year
                ).orElseGet(() -> {
                    Artifact newArtifact = new Artifact();

                    newArtifact.setArtifactType(ArtifactType.withDVMusic());
                    newArtifact.setArtist(artist);
                    newArtifact.setTitle(artifactName);
                    newArtifact.setYear(year);

                    return newArtifact;
                });

                artifactRepository.save(artifact);
                counter.getAndIncrement();

                int compositionsSize = compositionRepository.findAllByArtifact(artifact).size();
                if (compositionsSize == 0) {
                    importCompositionsAndMediaFiles(mdbReader, row.getInt(DVCONT_ID_COLUMN_NAME), artifact);
                }
            }
        }

        return counter.get();
    }

    private List<DVDet> loadDVDet(MDBReader mdbReader, int dvContId) throws ProcessorException {
        Table table = mdbReader.getTable(DVDET_TABLE_NAME);
        List<DVDet> dvDetTable = new ArrayList<>();

        for (Row row: table) {
            if (row.getInt(DVCONT_ID_COLUMN_NAME) == dvContId) {
                dvDetTable.add (
                        new DVDet(
                                row.getInt(DVDET_ID_COLUMN_NAME),
                                getArtistByMigrationId(row.getInt(ARTISTLIST_ID_COLUMN_NAME).longValue()),
                                row.getString(TITLE_COLUMN_NAME),
                                row.getString(FILE_NAME_COLUMN_NAME),
                                row.getInt(VIDEOTYPE_ID_COLUMN_NAME)
                        )
                );
            }
        }

        return dvDetTable;
    }

    public int importCompositionsAndMediaFiles(MDBReader mdbReader, int dvContId, Artifact artifact) throws ProcessorException {
        AtomicInteger counter = new AtomicInteger(0);

        List<DVDet> dvDetList = loadDVDet(mdbReader, dvContId);
        dvDetList.sort(Comparator.comparingInt(o -> o.id));
        long compNum = 0;

        for (DVDet dvDet: dvDetList) {
            Composition composition = new Composition();

            composition.setArtifact(artifact);
            composition.setArtist(dvDet.artist);
            composition.setTitle(dvDet.title);
            composition.setDvType(dvTypeMap.get((long)dvDet.videoTypeId));

            composition.setNum(++compNum);

            MediaFile mediaFile = mediaFileRepository.findFirstByArtifactAndName(
                    artifact,
                    dvDet.fileName
            ).orElseGet(() -> {
                MediaFile newMediaFile = new MediaFile();

                newMediaFile.setArtifact(artifact);
                newMediaFile.setName(dvDet.fileName);
                newMediaFile.setFormat(getExtension(dvDet.fileName).toUpperCase());
                newMediaFile.setSize(0L);

                return newMediaFile;
            });

            compositionService.insertCompositionWithMedia(composition, mediaFile);
            counter.getAndIncrement();
        }

        return counter.get();
    }
}
