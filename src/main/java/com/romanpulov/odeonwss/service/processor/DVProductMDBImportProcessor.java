package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.repository.DVCategoryRepository;
import com.romanpulov.odeonwss.repository.DVOriginRepository;
import com.romanpulov.odeonwss.repository.DVProductRepository;
import org.springframework.stereotype.Component;

@Component
public class DVProductMDBImportProcessor extends AbstractMDBImportProcessor {
    private static final int[] DV_ORIGIN_CODES = {1, 2, 17, 76};

    private final DVOriginRepository dvOriginRepository;

    private final DVCategoryRepository dvCategoryRepository;

    private final DVProductRepository dvProductRepository;

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

    }
}
