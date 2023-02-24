package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DBProcessDetail;
import com.romanpulov.odeonwss.entity.DBProcessDetailAction;
import com.romanpulov.odeonwss.entity.DBProcessDetailItem;
import com.romanpulov.odeonwss.entity.DBProcessInfo;
import com.romanpulov.odeonwss.service.processor.model.ProcessInfo;
import com.romanpulov.odeonwss.service.processor.model.ProcessingAction;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

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

    @Transactional
    public void save(ProcessInfo processInfo) {
        saveDbProcessInfo(processInfo);
    }

    private void saveDbProcessInfo(ProcessInfo processInfo) {
        DBProcessInfo dbProcessInfo = new DBProcessInfo();
        dbProcessInfo.setProcessorType(processInfo.getProcessorType());
        dbProcessInfo.setProcessingStatus(processInfo.getProcessingStatus());
        dbProcessInfo.setUpdateDateTime(processInfo.getLastUpdated());;

        dbProcessInfoRepository.save(dbProcessInfo);
        saveDbProcessDetails(processInfo, dbProcessInfo);
    }

    private void saveDbProcessDetails(ProcessInfo processInfo, DBProcessInfo dbProcessInfo) {
        processInfo.getProgressDetails().forEach(
                processDetail -> {
                    DBProcessDetail dbProcessDetail = new DBProcessDetail();
                    dbProcessDetail.setDbProcessInfo(dbProcessInfo);
                    dbProcessDetail.setMessage(processDetail.getInfo().getMessage());
                    dbProcessDetail.setProcessingStatus(processDetail.getStatus());
                    dbProcessDetail.setRows(processDetail.getRows() == null ? null : processDetail.getRows().longValue());
                    dbProcessDetail.setUpdateDateTime(processDetail.getTime());

                    dbProcessDetailRepository.save(dbProcessDetail);

                    saveDbProcessDetailItems(dbProcessDetail, processDetail.getInfo().getItems());
                    if (processDetail.getProcessingAction() != null) {
                        saveDbProcessDetailActions(dbProcessDetail, processDetail.getProcessingAction());
                    }
                }
        );
    }

    private void saveDbProcessDetailItems(DBProcessDetail dbProcessDetail, List<String> items) {
        items.forEach(item -> {
            DBProcessDetailItem dbProcessDetailItem = new DBProcessDetailItem();
            dbProcessDetailItem.setDbProcessDetail(dbProcessDetail);
            dbProcessDetailItem.setValue(item);

            dbProcessDetailItemRepository.save(dbProcessDetailItem);
        });
    }

    private void saveDbProcessDetailActions(DBProcessDetail dbProcessDetail, ProcessingAction processingAction) {
        DBProcessDetailAction dbProcessDetailAction = new DBProcessDetailAction();
        dbProcessDetailAction.setDbProcessDetail(dbProcessDetail);
        dbProcessDetailAction.setActionType(processingAction.getActionType());
        dbProcessDetailAction.setValue(processingAction.getValue());

        dbProcessDetailActionRepository.save(dbProcessDetailAction);
    }

}
