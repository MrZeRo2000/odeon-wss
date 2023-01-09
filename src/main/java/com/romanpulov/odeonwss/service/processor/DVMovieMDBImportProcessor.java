package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.DVType;
import com.romanpulov.odeonwss.repository.DVTypeRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DVMovieMDBImportProcessor extends AbstractMDBImportProcessor {
    private static final int DV_MOVIE_REC_ID = 1254;

    private final DVTypeRepository dvTypeRepository;

    private Map<Long, DVType> dvTypeMap;

    public DVMovieMDBImportProcessor(DVTypeRepository dvTypeRepository) {
        this.dvTypeRepository = dvTypeRepository;
    }

    @Override
    protected void importMDB(MDBReader mdbReader) throws ProcessorException {
        if (dvTypeMap == null) {
            dvTypeMap = dvTypeRepository.findAllMap();
        }

    }
}
