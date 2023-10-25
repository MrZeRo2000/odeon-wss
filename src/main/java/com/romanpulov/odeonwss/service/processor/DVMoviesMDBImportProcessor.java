package com.romanpulov.odeonwss.service.processor;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.romanpulov.jutilscore.io.FileUtils.getExtension;
import static com.romanpulov.odeonwss.service.processor.MDBConst.*;

@Component
public class DVMoviesMDBImportProcessor extends AbstractMDBImportProcessor {
    private static final Logger log = Logger.getLogger(DVMoviesMDBImportProcessor.class.getSimpleName());

    private static final int DV_MOVIE_REC_ID = 1254;

    private ArtifactType artifactType;

    private final DVTypeRepository dvTypeRepository;

    private final DVProductMDBImportProcessor dvProductMDBImportProcessor;

    private final ArtifactTypeRepository artifactTypeRepository;

    private final ArtifactRepository artifactRepository;

    private final TrackRepository trackRepository;

    private final MediaFileRepository mediaFileRepository;

    private final DVProductRepository dvProductRepository;

    private Map<Long, DVType> dvTypeMap;

    private Map<Long, Artifact> artifacts;
    private Map<String, Track> tracks;
    private Map<Long, Track> contentTracks;
    private Map<Long, MediaFile> mediaFiles;
    private Map<Long, DVProduct> dvProducts;

    public DVMoviesMDBImportProcessor(
            DVTypeRepository dvTypeRepository,
            DVProductMDBImportProcessor dvProductMDBImportProcessor,
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            TrackRepository trackRepository,
            MediaFileRepository mediaFileRepository,
            DVProductRepository dvProductRepository
    ) {
        this.dvTypeRepository = dvTypeRepository;
        this.dvProductMDBImportProcessor = dvProductMDBImportProcessor;
        this.artifactTypeRepository = artifactTypeRepository;
        this.artifactRepository = artifactRepository;
        this.trackRepository = trackRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.dvProductRepository = dvProductRepository;
    }

    @Override
    protected void importMDB(MDBReader mdbReader) throws ProcessorException {
        if (this.artifactType == null) {
            this.artifactType = artifactTypeRepository.getWithDVMovies();
        }
        if (dvTypeMap == null) {
            dvTypeMap = dvTypeRepository.findAllMap();
        }
        artifacts = artifactRepository.findAllByArtifactTypeMigrationIdMap(artifactType);
        tracks = trackRepository.getTracksByArtifactType(artifactType)
                .stream()
                .collect(Collectors.toMap(Track::getTitle, v-> v));
        contentTracks = new HashMap<>();
        mediaFiles = mediaFileRepository.getMediaFilesByArtifactType(artifactType)
                        .stream()
                        .collect(Collectors.toMap(MediaFile::getMigrationId, v ->v));

        dvProductMDBImportProcessor.setProgressHandler(getProgressHandler());
        dvProductMDBImportProcessor.setRootFolder(getRootFolder());
        dvProductMDBImportProcessor.execute();

        dvProducts = dvProductRepository.findAllMigrationIdMap();

        infoHandler(ProcessorMessages.INFO_ARTIFACTS_IMPORTED, importArtifacts(mdbReader));
        infoHandler(ProcessorMessages.INFO_TRACKS_IMPORTED, importTracks(mdbReader));
        infoHandler(ProcessorMessages.INFO_PRODUCTS_TRACKS_IMPORTED, importProductsTracks(mdbReader));
        infoHandler(ProcessorMessages.INFO_MEDIA_FILES_IMPORTED, importMediaFiles(mdbReader));
    }

    public int importArtifacts(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(DVCONT_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);

        for (Row row: table) {
            if (row.getInt(REC_ID_COLUMN_NAME) == DV_MOVIE_REC_ID) {
                long id = row.getInt(DVCONT_ID_COLUMN_NAME);
                String artifactName = row.getString(FILE_NAME_COLUMN_NAME);

                Artifact artifact = this.artifacts.get(id);
                if (artifact == null) {
                    artifact = new Artifact();
                    artifact.setArtifactType(artifactType);
                    artifact.setDuration(0L);
                    artifact.setTitle(artifactName);
                    artifact.setMigrationId(id);

                    artifactRepository.save(artifact);

                    counter.getAndIncrement();
                }

                this.artifacts.put(id, artifact);
            }
        }

        return counter.get();
    }

    public int importTracks(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(DVDET_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);

        for (Row row: table) {
            long id = row.getInt(DVDET_ID_COLUMN_NAME);
            long contId = row.getInt(DVCONT_ID_COLUMN_NAME);

            if (this.artifacts.containsKey(contId)) {
                String title = row.getString(TITLE_COLUMN_NAME);
                Track track = this.tracks.get(title);
                if (track == null) {
                    track = new Track();
                    track.setArtifact(this.artifacts.get(contId));
                    track.setTitle(row.getString(TITLE_COLUMN_NAME));
                    track.setNum(1L);
                    track.setDuration(0L);
                    track.setDvType(dvTypeMap.get(row.getInt(VIDEOTYPE_ID_COLUMN_NAME).longValue()));
                    track.setMigrationId(id);

                    trackRepository.save(track);
                    this.tracks.put(title, track);

                    counter.getAndIncrement();
                }

                this.contentTracks.put(id, track);
            }
        }

        return counter.get();
    }

    public int importMediaFiles(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(DVDET_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);

        for (Row row: table) {
            long id = row.getInt(DVDET_ID_COLUMN_NAME);
            long contId = row.getInt(DVCONT_ID_COLUMN_NAME);

            if (this.artifacts.containsKey(contId)) {
                String title = row.getString(TITLE_COLUMN_NAME);
                String fileName = row.getString(FILE_NAME_COLUMN_NAME);
                Track track = this.tracks.get(title);
                if ((track != null) && !this.mediaFiles.containsKey(id)) {
                    MediaFile mediaFile = new MediaFile();
                    mediaFile.setArtifact(artifacts.get(contId));
                    mediaFile.setName(fileName);
                    mediaFile.setFormat(getExtension(fileName.toUpperCase()));
                    mediaFile.setSize(0L);
                    mediaFile.setMigrationId(id);

                    mediaFileRepository.save(mediaFile);

                    Set<MediaFile> mediaFiles = track.getMediaFiles();
                    mediaFiles.add(mediaFile);

                    if (mediaFiles.size() > 1) {
                        track.setMediaFiles(new HashSet<>());
                        trackRepository.save(track);
                    }

                    track.setMediaFiles(mediaFiles);
                    trackRepository.save(track);

                    counter.getAndIncrement();
                }
            }
        }

        return counter.get();
    }

    public int importProductsTracks(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(PRODUCT_DVDET_TABLE_NAME);

        Set<Track> savedTracks = new HashSet<>();

        for (Row row: table) {
            long id = row.getInt(DVDET_ID_COLUMN_NAME);
            long productId = row.getInt(VPRODUCT_ID_COLUMN_NAME);

            Track track = null;
            if (this.contentTracks.containsKey(id)) {
                track = trackRepository.findByIdWithProducts(this.contentTracks.get(id).getId()).orElse(null);
            }
            DVProduct dvProduct = this.dvProducts.get(productId);

            if (
                    (track != null)
                            && (dvProduct != null)
                            && (!savedTracks.contains(track))
                            && (track.getDvProducts().isEmpty())) {
                track.getDvProducts().add(dvProduct);
                trackRepository.save(track);

                savedTracks.add(track);
            }
        }

        return savedTracks.size();
    }
}
