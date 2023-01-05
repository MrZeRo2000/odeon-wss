package com.romanpulov.odeonwss.service.processor;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.romanpulov.odeonwss.entity.DVCategory;
import com.romanpulov.odeonwss.entity.DVOrigin;
import com.romanpulov.odeonwss.repository.DVCategoryRepository;
import com.romanpulov.odeonwss.repository.DVOriginRepository;
import com.romanpulov.odeonwss.repository.DVProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.romanpulov.odeonwss.service.processor.MDBConst.*;

@Component
public class DVProductMDBImportProcessor extends AbstractMDBImportProcessor {
    private static final Set<Long> DV_ORIGIN_CODES = Stream.of(1L, 2L, 17L, 76L).collect(Collectors.toSet());;

    private final DVOriginRepository dvOriginRepository;

    private final DVCategoryRepository dvCategoryRepository;

    private final DVProductRepository dvProductRepository;

    private Map<Long, DVOrigin> dvOrigins;
    private Map<Long, DVCategory> dvCategories;

    public DVProductMDBImportProcessor(
            DVOriginRepository dvOriginRepository,
            DVCategoryRepository dvCategoryRepository,
            DVProductRepository dvProductRepository
    ) {
        this.dvOriginRepository = dvOriginRepository;
        this.dvCategoryRepository = dvCategoryRepository;
        this.dvProductRepository = dvProductRepository;
    }

    @Override
    protected void importMDB(MDBReader mdbReader) throws ProcessorException {
        dvOrigins = new HashMap<>();
        dvCategories = new HashMap<>();

        infoHandler(ProcessorMessages.INFO_CATEGORIES_IMPORTED, importCategories(mdbReader));
    }

    public int importCategories(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(CAT_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);

        Map<Long, DVOrigin> migrationOrigins = dvOriginRepository.findAllMigrationIdMap();
        Map<Long, DVCategory> migrationCategories = dvCategoryRepository.findAllMigrationIdMap();

        for (Row row: table) {
            long id = row.getInt(CAT_ID_COLUMN_NAME).longValue();
            String name = row.getString(TITLE_COLUMN_NAME);

            if (DV_ORIGIN_CODES.contains(id)) {
                DVOrigin origin = migrationOrigins.get(id);
                if (origin == null) {
                    origin = new DVOrigin();
                    origin.setName(name);
                    origin.setMigrationId(id);

                    dvOriginRepository.save(origin);

                    counter.getAndIncrement();
                }
                this.dvOrigins.put(id, origin);
            } else {
                DVCategory category = migrationCategories.get(id);
                if (category == null) {
                    category = new DVCategory();
                    category.setName(name);
                    category.setMigrationId(id);

                    dvCategoryRepository.save(category);

                    counter.getAndIncrement();
                }
                this.dvCategories.put(id, category);
            }
        }

        return counter.get();
    }
}
