package com.romanpulov.odeonwss.service.processor;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static com.romanpulov.odeonwss.service.processor.MDBConst.*;

public abstract class AbstractMusicAttributesMDBImportProcessor extends AbstractMDBImportProcessor {

    protected final String mdbTableName;

    private final ArtifactRepository artifactRepository;

    private final ArtifactTypeRepository artifactTypeRepository;

    private final Function<ArtifactTypeRepository, ArtifactType> artifactTypeSupplier;

    public AbstractMusicAttributesMDBImportProcessor(
            String mdbTableName,
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            Function<ArtifactTypeRepository, ArtifactType> artifactTypeSupplier
            ) {
        this.mdbTableName = mdbTableName;
        this.artifactTypeRepository = artifactTypeRepository;
        this.artifactRepository = artifactRepository;
        this.artifactTypeSupplier = artifactTypeSupplier;
    }

    public static class ArtistTitleInsertDate {
        final String artistName;
        final String title;
        final int year;
        final LocalDateTime insertDate;

        private ArtistTitleInsertDate(String artistName, String title, int year, LocalDateTime insertDate) {
            this.artistName = artistName;
            this.title = title;
            this.year = year;
            this.insertDate = insertDate;
        }

        public static ArtistTitleInsertDate of(String artistName, String title, int year, LocalDateTime insertDate) {
            return new ArtistTitleInsertDate(artistName, title, year, insertDate);
        }
    }

    private Map<Integer, String> loadArtists(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(ARTISTLIST_TABLE_NAME);
        Map<Integer, String> result = new HashMap<>();

        for (Row row: table) {
            result.put(
                    row.getInt(ARTISTLIST_ID_COLUMN_NAME),
                    row.getString(TITLE_COLUMN_NAME)
                    );
        }

        return result;
    }

    private Collection<ArtistTitleInsertDate> loadArtistTitleInsertDates(
            MDBReader mdbReader,
            Map<Integer, String> artists) throws ProcessorException {
        Collection<ArtistTitleInsertDate> result = new ArrayList<>();
        Table table = mdbReader.getTable(this.mdbTableName);

        for (Row row: table) {
            String artistName = artists.get(row.getInt(ARTISTLIST_ID_COLUMN_NAME));
            if (artistName != null) {
                String title = row.getString(TITLE_COLUMN_NAME);
                Integer year = row.getInt(YEAR_COLUMN_NAME);
                LocalDateTime insertDate = row.getLocalDateTime(INS_DATE_COLUMN_NAME);

                if ((title != null) && (insertDate != null) && (year != null)) {
                    result.add(ArtistTitleInsertDate.of(artistName, title, year, insertDate));
                }
            }
        }

        return result;
    }

    protected int updateArtifacts(Collection<ArtistTitleInsertDate> artistTitleInsertDates) {
        AtomicInteger counter = new AtomicInteger(0);

        final ArtifactType artifactType = this.artifactTypeSupplier.apply(this.artifactTypeRepository);
        for (ArtistTitleInsertDate artistTitleInsertDate: artistTitleInsertDates) {
            artifactRepository
                    .findFirstByArtifactTypeAndArtistNameAndTitleAndYear(
                            artifactType,
                            artistTitleInsertDate.artistName,
                            artistTitleInsertDate.title,
                            Integer.valueOf(artistTitleInsertDate.year).longValue()
                    ).ifPresent(artifact -> {
                        artifact.setInsertDateTime(artistTitleInsertDate.insertDate);
                        artifactRepository.save(artifact);
                        counter.incrementAndGet();
                    });
        }

        return counter.get();
    }

    @Override
    protected void importMDB(MDBReader mdbReader) throws ProcessorException {
        Map<Integer, String> artists = loadArtists(mdbReader);
        Collection<ArtistTitleInsertDate> artistTitleInsertDates = loadArtistTitleInsertDates(mdbReader, artists);
        infoHandler(ProcessorMessages.INFO_ARTIFACTS_UPDATED, updateArtifacts(artistTitleInsertDates));
    }
}
