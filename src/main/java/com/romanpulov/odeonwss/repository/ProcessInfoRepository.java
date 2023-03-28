package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DBProcessDetail;
import com.romanpulov.odeonwss.entity.DBProcessDetailAction;
import com.romanpulov.odeonwss.entity.DBProcessDetailItem;
import com.romanpulov.odeonwss.entity.DBProcessInfo;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
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

    public List<ProcessInfo> findAllByLastUpdated(LocalDateTime startDate, LocalDateTime endDate) {
        return this.dbProcessInfoRepository.findAllByUpdateDateTimeBetweenOrderByIdAsc(startDate, endDate)
                .stream()
                .map(p -> {
                    ProcessInfo processInfo = new ProcessInfo(p.getProcessorType());
                    processInfo.setProcessingStatus(p.getProcessingStatus());
                    processInfo.setLastUpdated(p.getUpdateDateTime());
                    return processInfo;
                })
                .collect(Collectors.toList());
    }

    public Optional<ProcessInfo> findById(Long id) {
        DBProcessInfo dbProcessInfo = dbProcessInfoRepository.findById(id).orElse(null);
        if (dbProcessInfo == null) {
            return Optional.empty();
        } else {
            //processInfo
            ProcessInfo processInfo = new ProcessInfo(dbProcessInfo.getProcessorType());
            processInfo.setProcessingStatus(dbProcessInfo.getProcessingStatus());
            processInfo.setLastUpdated(dbProcessInfo.getUpdateDateTime());

            //processDetails
            this.dbProcessDetailRepository
                    .findAllByDbProcessInfoOrderByIdAsc(dbProcessInfo)
                    .forEach(dbProcessDetail -> {
                        List<String> processInfoItems = this.dbProcessDetailItemRepository
                                .findAllByDbProcessDetailOrderByIdAsc(dbProcessDetail)
                                .stream()
                                .map(DBProcessDetailItem::getValue)
                                .collect(Collectors.toList());

                        Pair<ProcessingActionType, String> processingAction =
                                dbProcessDetailActionRepository
                                        .findFirstByDbProcessDetailOrderByIdAsc(dbProcessDetail)
                                        .map(pa -> Pair.of(pa.getActionType(), pa.getValue()))
                                        .orElse(null);

                        ProcessDetail processDetail = new ProcessDetail(
                                dbProcessDetail.getUpdateDateTime(),
                                ProcessDetailInfo.fromMessageItems(dbProcessDetail.getMessage(), processInfoItems),
                                dbProcessDetail.getProcessingStatus(),
                                dbProcessDetail.getRows() == null ? null : dbProcessDetail.getRows().intValue(),
                                processingAction == null ? null :
                                        new ProcessingAction(processingAction.getFirst(), processingAction.getSecond())
                        );

                        processInfo.getProcessDetails().add(processDetail);
                    });

            return Optional.of(processInfo);
        }
    }

    @Transactional
    public void save(ProcessInfo processInfo) {
        saveDbProcessInfo(processInfo);
    }

    private void saveDbProcessInfo(ProcessInfo processInfo) {
        DBProcessInfo dbProcessInfo = new DBProcessInfo();
        dbProcessInfo.setProcessorType(processInfo.getProcessorType());
        dbProcessInfo.setProcessingStatus(processInfo.getProcessingStatus());
        dbProcessInfo.setUpdateDateTime(processInfo.getLastUpdated());

        dbProcessInfoRepository.save(dbProcessInfo);
        saveDbProcessDetails(processInfo, dbProcessInfo);
    }

    @Transactional
    void saveDbProcessDetails(ProcessInfo processInfo, DBProcessInfo dbProcessInfo) {
        processInfo.getProcessDetails().forEach(
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

    @Transactional
    void saveDbProcessDetailItems(DBProcessDetail dbProcessDetail, List<String> items) {
        items.forEach(item -> {
            DBProcessDetailItem dbProcessDetailItem = new DBProcessDetailItem();
            dbProcessDetailItem.setDbProcessDetail(dbProcessDetail);
            dbProcessDetailItem.setValue(item);

            dbProcessDetailItemRepository.save(dbProcessDetailItem);
        });
    }

    @Transactional
    void saveDbProcessDetailActions(DBProcessDetail dbProcessDetail, ProcessingAction processingAction) {
        DBProcessDetailAction dbProcessDetailAction = new DBProcessDetailAction();
        dbProcessDetailAction.setDbProcessDetail(dbProcessDetail);
        dbProcessDetailAction.setActionType(processingAction.getActionType());
        dbProcessDetailAction.setValue(processingAction.getValue());

        dbProcessDetailActionRepository.save(dbProcessDetailAction);
    }

}
