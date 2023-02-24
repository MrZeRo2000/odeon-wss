package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DBProcessDetail;
import com.romanpulov.odeonwss.entity.DBProcessDetailAction;
import com.romanpulov.odeonwss.entity.DBProcessDetailItem;
import com.romanpulov.odeonwss.entity.DBProcessInfo;
import com.romanpulov.odeonwss.service.processor.model.ProcessDetail;
import com.romanpulov.odeonwss.service.processor.model.ProcessInfo;
import com.romanpulov.odeonwss.service.processor.model.ProcessingAction;
import com.romanpulov.odeonwss.service.processor.model.ProcessingActionType;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public ProcessInfo findById(Long id) {
        DBProcessInfo dbProcessInfo = dbProcessInfoRepository.findById(id).orElse(null);
        if (dbProcessInfo == null) {
            return null;
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
                                new ProcessDetail.ProcessInfo(dbProcessDetail.getMessage(), processInfoItems),
                                dbProcessDetail.getProcessingStatus(),
                                dbProcessDetail.getRows() == null ? null : dbProcessDetail.getRows().intValue(),
                                processingAction == null ? null :
                                        new ProcessingAction(processingAction.getFirst(), processingAction.getSecond())
                        );

                        processInfo.getProgressDetails().add(processDetail);
                    });


            //processDetails
            /*
            List<ProcessDetail> processDetails = this.dbProcessDetailRepository
                    .findAllByDbProcessInfoOrderByIdAsc(dbProcessInfo)
                    .stream()
                    .map(p ->
                            new ProcessDetail(
                                    p.getUpdateDateTime(),
                                    new ProcessDetail.ProcessInfo(p.getMessage(), new ArrayList<>()),
                                    p.getProcessingStatus(),
                                    p.getRows() == null ? null : p.getRows().intValue(),
                                    null
                            )
                    ).collect(Collectors.toList());


             */
            return processInfo;
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
