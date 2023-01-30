package com.romanpulov.odeonwss.service.processor;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.romanpulov.odeonwss.entity.DVCategory;
import com.romanpulov.odeonwss.entity.DVOrigin;
import com.romanpulov.odeonwss.entity.DVProduct;
import com.romanpulov.odeonwss.repository.DVCategoryRepository;
import com.romanpulov.odeonwss.repository.DVOriginRepository;
import com.romanpulov.odeonwss.repository.DVProductRepository;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.romanpulov.odeonwss.service.processor.MDBConst.*;

@Component
public class DVProductMDBImportProcessor extends AbstractMDBImportProcessor {
    private static final Set<Long> DV_ORIGIN_CODES = Stream.of(1L, 2L, 17L, 76L).collect(Collectors.toSet());

    private final DVOriginRepository dvOriginRepository;

    private final DVCategoryRepository dvCategoryRepository;

    private final DVProductRepository dvProductRepository;

    private Map<Long, DVOrigin> dvOrigins;
    private Map<Long, DVCategory> dvCategories;
    private Map<Long, DVProduct> dvProducts;

    private Map<Long, Long> dvOriginIds;
    private Map<Long, Collection<Long>> dvCategoryIds;

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
        dvProducts = new HashMap<>();

        readProductCatOrigin(mdbReader);

        infoHandler(ProcessorMessages.INFO_CATEGORIES_IMPORTED, importCategories(mdbReader));
        infoHandler(ProcessorMessages.INFO_PRODUCTS_IMPORTED, importProducts(mdbReader));
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

    public void readProductCatOrigin(MDBReader mdbReader) throws ProcessorException {
        dvOriginIds = new HashMap<>();
        dvCategoryIds = new HashMap<>();

        for (Row row : mdbReader.getTable(PRODUCT_CAT_TABLE_NAME)) {
            long catId = row.getInt(CAT_ID_COLUMN_NAME).longValue();
            long productId = row.getInt(VPRODUCT_ID_COLUMN_NAME).longValue();
            if (DV_ORIGIN_CODES.contains(catId)) {
                dvOriginIds.put(row.getInt(VPRODUCT_ID_COLUMN_NAME).longValue(), catId);
            } else {
                if (dvCategoryIds.containsKey(productId)) {
                    dvCategoryIds.get(productId).add(catId);
                } else {
                    dvCategoryIds.put(row.getInt(VPRODUCT_ID_COLUMN_NAME).longValue(), Stream.of(catId).collect(Collectors.toSet()));
                }
            }
        }
    }

    @Transactional
    public int importProducts(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(PRODUCT_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);

        Map<Long, DVProduct> migrationProducts = dvProductRepository.findAllMigrationIdMap();

        for (Row row: table) {
            long id = row.getInt(VPRODUCT_ID_COLUMN_NAME).longValue();
            DVProduct product = migrationProducts.get(id);
            if (product == null) {
                product = new DVProduct();
                product.setTitle(row.getString(TITLE_COLUMN_NAME));
                product.setOriginalTitle(row.getString(ORIG_TITLE_COLUMN_NAME));
                product.setDvOrigin(dvOrigins.get(dvOriginIds.get(id)));

                String year = row.getString(YEAR_COLUMN_NAME);
                if (year != null) {
                    try {
                        product.setYear(Long.parseLong(year.trim()));
                    } catch (NumberFormatException ignored) {

                    }
                }

                product.setFrontInfo(row.getString(FRONT_INFO_COLUMN_NAME));
                product.setDescription(row.getString(DESCRIPTION_COLUMN_NAME));
                product.setNotes(row.getString(NOTES_COLUMN_NAME));

                Collection<Long> productCategories = this.dvCategoryIds.get(id);
                if (productCategories != null) {
                    product.setDvCategories(
                            productCategories.stream().map(v -> dvCategories.get(v)).collect(Collectors.toSet())
                    );
                }

                product.setMigrationId(id);

                dvProductRepository.save(product);

                counter.getAndIncrement();
            }

            this.dvProducts.put(id, product);
        }

        return counter.get();
    }
}
