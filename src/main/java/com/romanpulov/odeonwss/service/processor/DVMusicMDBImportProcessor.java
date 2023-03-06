package com.romanpulov.odeonwss.service.processor;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.*;
import com.romanpulov.odeonwss.service.TrackService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.romanpulov.jutilscore.io.FileUtils.getExtension;
import static com.romanpulov.odeonwss.service.processor.MDBConst.*;

@Component
public class DVMusicMDBImportProcessor extends AbstractMDBImportProcessor {
    private static final int DV_MUSIC_REC_ID = 1253;
    public static final ArtifactType ARTIFACT_TYPE = ArtifactType.withDVMusic();

    private final ArtistRepository artistRepository;

    private final ArtifactRepository artifactRepository;

    private final TrackRepository trackRepository;

    private final TrackService trackService;

    private final MediaFileRepository mediaFileRepository;

    private final DVTypeRepository dvTypeRepository;

    private Map<Long, Artist> migratedArtists;

    private Map<Long, DVType> dvTypeMap;

    private Map<Integer, Artifact> dvContArtifactMap;

    public DVMusicMDBImportProcessor(
            ArtistRepository artistRepository,
            ArtifactRepository artifactRepository,
            TrackRepository trackRepository,
            TrackService trackService,
            MediaFileRepository mediaFileRepository,
            DVTypeRepository dvTypeRepository
    ) {
        this.artistRepository = artistRepository;
        this.artifactRepository = artifactRepository;
        this.trackRepository = trackRepository;
        this.trackService = trackService;
        this.mediaFileRepository = mediaFileRepository;
        this.dvTypeRepository = dvTypeRepository;
    }

    @Override
    protected void importMDB(MDBReader mdbReader) throws ProcessorException {
        if (dvTypeMap == null) {
            dvTypeMap = dvTypeRepository.findAllMap();
        }

        infoHandler(ProcessorMessages.INFO_ARTIFACTS_IMPORTED, importArtifacts(mdbReader));
        infoHandler(ProcessorMessages.INFO_TRACKS_IMPORTED, importTracksAndMediaFiles(mdbReader));
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
        dvContArtifactMap = new HashMap<>();

        for (Row row: table) {
            if (row.getInt(REC_ID_COLUMN_NAME) == DV_MUSIC_REC_ID) {
                String artifactName = row.getString(FILE_NAME_COLUMN_NAME);

                Artist artist = getArtistByMigrationId(row.getInt(ARTISTLIST_ID_COLUMN_NAME).longValue());

                Long year = row.getInt(YEAR_COLUMN_NAME) == null ? null : row.getInt(YEAR_COLUMN_NAME).longValue();

                Artifact artifact = artifactRepository.findFirstByArtifactTypeAndArtistAndTitleAndYear(
                        ARTIFACT_TYPE,
                        artist,
                        artifactName,
                        year
                ).orElseGet(() -> {
                    Artifact newArtifact = new Artifact();

                    newArtifact.setArtifactType(ARTIFACT_TYPE);
                    newArtifact.setArtist(artist);
                    newArtifact.setTitle(artifactName);
                    newArtifact.setYear(year);

                    artifactRepository.save(newArtifact);
                    counter.getAndIncrement();

                    return newArtifact;
                });

                int tracksSize = trackRepository.findAllByArtifact(artifact).size();
                if (tracksSize == 0) {
                    dvContArtifactMap.put(row.getInt(DVCONT_ID_COLUMN_NAME), artifact);
                }
            }
        }

        return counter.get();
    }

    public int importTracksAndMediaFiles(MDBReader mdbReader)
            throws ProcessorException {
        AtomicInteger counter = new AtomicInteger(0);
        Map<Integer, List<DVDet>> dvDetMap = loadDVDetMap(mdbReader);

        //by artifacts
        for (int dvContId: dvContArtifactMap.keySet()) {
            Artifact artifact = dvContArtifactMap.get(dvContId);
            List<DVDet> dvDetList = dvDetMap.get(dvContId);

            dvDetList.sort(Comparator.comparingInt(o -> o.id));
            long compNum = 0;

            for (DVDet dvDet: dvDetList) {
                Track track = new Track();

                track.setArtifact(artifact);
                track.setArtist(dvDet.artist);
                track.setTitle(dvDet.title);
                track.setDvType(dvTypeMap.get((long)dvDet.videoTypeId));

                track.setNum(++compNum);

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

                trackService.insertTrackWithMedia(track, mediaFile);
                counter.getAndIncrement();
            }
        }

        return counter.get();
    }

    private Map<Integer, List<DVDet>> loadDVDetMap(MDBReader mdbReader) throws ProcessorException {
        Map<Integer, List<DVDet>> result = new HashMap<>();
        Table table = mdbReader.getTable(DVDET_TABLE_NAME);

        for (Row row: table) {
            int id = row.getInt(DVCONT_ID_COLUMN_NAME);
            if (dvContArtifactMap.containsKey(id)) {
                List<DVDet> dvDetList = result.computeIfAbsent(id, k -> new ArrayList<>());
                dvDetList.add (
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

        return result;
    }
}
