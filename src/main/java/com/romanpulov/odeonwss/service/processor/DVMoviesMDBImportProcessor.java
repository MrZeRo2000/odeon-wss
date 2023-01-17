package com.romanpulov.odeonwss.service.processor;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.*;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
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
    private static final ArtifactType ARTIFACT_TYPE = ArtifactType.withDVMovies();

    private final DVTypeRepository dvTypeRepository;

    private final DVProductMDBImportProcessor dvProductMDBImportProcessor;

    private final ArtifactRepository artifactRepository;

    private final CompositionRepository compositionRepository;

    private final MediaFileRepository mediaFileRepository;

    private final DVProductRepository dvProductRepository;

    private Map<Long, DVType> dvTypeMap;

    private Map<Long, Artifact> artifacts;
    private Map<String, Composition> compositions;
    private Map<Long, Composition> contentCompositions;
    private Map<Long, MediaFile> mediaFiles;
    private Map<Long, DVProduct> dvProducts;

    public DVMoviesMDBImportProcessor(
            DVTypeRepository dvTypeRepository,
            DVProductMDBImportProcessor dvProductMDBImportProcessor,
            ArtifactRepository artifactRepository,
            CompositionRepository compositionRepository,
            MediaFileRepository mediaFileRepository,
            DVProductRepository dvProductRepository
    ) {
        this.dvTypeRepository = dvTypeRepository;
        this.dvProductMDBImportProcessor = dvProductMDBImportProcessor;
        this.artifactRepository = artifactRepository;
        this.compositionRepository = compositionRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.dvProductRepository = dvProductRepository;
    }

    @Override
    protected void importMDB(MDBReader mdbReader) throws ProcessorException {
        if (dvTypeMap == null) {
            dvTypeMap = dvTypeRepository.findAllMap();
        }
        artifacts = artifactRepository.findAllByArtifactTypeMigrationIdMap(ARTIFACT_TYPE);
        compositions = compositionRepository.getCompositionsByArtifactType(ARTIFACT_TYPE)
                .stream()
                .collect(Collectors.toMap(Composition::getTitle, v-> v));
        contentCompositions = new HashMap<>();
        mediaFiles = mediaFileRepository.getMediaFilesByArtifactType(ARTIFACT_TYPE)
                        .stream()
                        .collect(Collectors.toMap(MediaFile::getMigrationId, v ->v));

        dvProductMDBImportProcessor.setProgressHandler(getProgressHandler());
        dvProductMDBImportProcessor.setRootFolder(getRootFolder());
        dvProductMDBImportProcessor.execute();

        dvProducts = dvProductRepository.findAllMigrationIdMap();

        infoHandler(ProcessorMessages.INFO_ARTIFACTS_IMPORTED, importArtifacts(mdbReader));
        infoHandler(ProcessorMessages.INFO_COMPOSITIONS_IMPORTED, importCompositions(mdbReader));
        infoHandler(ProcessorMessages.INFO_PRODUCTS_COMPOSITIONS_IMPORTED, importProductsCompositions(mdbReader));
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
                    artifact.setArtifactType(ARTIFACT_TYPE);
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

    public int importCompositions(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(DVDET_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);

        for (Row row: table) {
            long id = row.getInt(DVDET_ID_COLUMN_NAME);
            long contId = row.getInt(DVCONT_ID_COLUMN_NAME);

            if (this.artifacts.containsKey(contId)) {
                String title = row.getString(TITLE_COLUMN_NAME);
                Composition composition = this.compositions.get(title);
                if (composition == null) {
                    composition = new Composition();
                    composition.setArtifact(this.artifacts.get(contId));
                    composition.setTitle(row.getString(TITLE_COLUMN_NAME));
                    composition.setDuration(0L);
                    composition.setDvType(dvTypeMap.get(row.getInt(VIDEOTYPE_ID_COLUMN_NAME).longValue()));
                    composition.setMigrationId(id);

                    compositionRepository.save(composition);
                    this.compositions.put(title, composition);

                    counter.getAndIncrement();
                }

                this.contentCompositions.put(id, composition);
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
                Composition composition = this.compositions.get(title);
                if ((composition != null) && !this.mediaFiles.containsKey(id)) {
                    MediaFile mediaFile = new MediaFile();
                    mediaFile.setArtifact(artifacts.get(contId));
                    mediaFile.setName(fileName);
                    mediaFile.setFormat(getExtension(fileName.toUpperCase()));
                    mediaFile.setSize(0L);
                    mediaFile.setMigrationId(id);

                    mediaFileRepository.save(mediaFile);

                    Set<MediaFile> mediaFiles = composition.getMediaFiles();
                    mediaFiles.add(mediaFile);

                    if (mediaFiles.size() > 1) {
                        composition.setMediaFiles(new HashSet<>());
                        compositionRepository.save(composition);
                    }

                    composition.setMediaFiles(mediaFiles);
                    compositionRepository.save(composition);

                    counter.getAndIncrement();
                }
            }
        }

        return counter.get();
    }

    public int importProductsCompositions(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(PRODUCT_DVDET_TABLE_NAME);

        Set<Composition> savedCompositions = new HashSet<>();

        for (Row row: table) {
            long id = row.getInt(DVDET_ID_COLUMN_NAME);
            long productId = row.getInt(VPRODUCT_ID_COLUMN_NAME);
            log.info(String.format("Loading %s DVDetId=%d, VProductId=%d", PRODUCT_DVDET_TABLE_NAME, id, productId));

            Composition composition = null;
            if (this.contentCompositions.containsKey(id)) {
                composition = compositionRepository.findByIdFetchProducts(this.contentCompositions.get(id).getId()).orElse(null);
            }
            DVProduct dvProduct = this.dvProducts.get(productId);

            if (
                    (composition != null)
                            && (dvProduct != null)
                            && (!savedCompositions.contains(composition))
                            && (composition.getDvProducts().size() == 0)) {
                composition.getDvProducts().add(dvProduct);
                compositionRepository.save(composition);

                savedCompositions.add(composition);
            }
        }

        return savedCompositions.size();
    }
}
