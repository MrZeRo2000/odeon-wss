package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DBProcessInfo;
import com.romanpulov.odeonwss.service.processor.model.ProcessInfo;
import org.springframework.stereotype.Repository;

@Repository
public class ProcessInfoRepository {

    private final DBProcessInfoRepository dbProcessInfoRepository;

    private final DBProcessDetailRepository dbProcessDetailRepository;

    private final DBProcessDetailItemRepository dbProcessDetailItemRepository;

    private final DBProcessDetailActionRepository dbProcessDetailActionRepository;

    public ProcessInfoRepository(
            DBProcessInfoRepository dbProcessInfoRepository,
            DBProcessDetailRepository dbProcessDetailRepository,
            DBProcessDetailItemRepository dbProcessDetailItemRepository,
            DBProcessDetailActionRepository dbProcessDetailActionRepository) {
        this.dbProcessInfoRepository = dbProcessInfoRepository;
        this.dbProcessDetailRepository = dbProcessDetailRepository;
        this.dbProcessDetailItemRepository = dbProcessDetailItemRepository;
        this.dbProcessDetailActionRepository = dbProcessDetailActionRepository;
    }

    public void save(ProcessInfo processInfo) {
    }

    private DBProcessInfo fillDbProcessInfo(ProcessInfo processInfo) {
        DBProcessInfo dbProcessInfo = new DBProcessInfo();
        dbProcessInfo.setProcessorType(processInfo.getProcessorType());
        dbProcessInfo.setProcessingStatus(processInfo.getProcessingStatus());
        dbProcessInfo.setUpdateDateTime(processInfo.getLastUpdated());;

        return dbProcessInfo;
    }

}
