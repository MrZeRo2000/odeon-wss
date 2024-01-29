package com.romanpulov.odeonwss.dto.process;

import com.romanpulov.odeonwss.service.processor.model.ProcessDetail;
import com.romanpulov.odeonwss.service.processor.model.ProcessInfo;
import com.romanpulov.odeonwss.service.processor.model.ProcessingAction;
import com.romanpulov.odeonwss.service.processor.model.ProcessingEvent;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProcessInfoTransformer {
    public ProcessInfoDTO transform(ProcessInfo processInfo) {
        ProcessInfoDTOImpl result = new ProcessInfoDTOImpl();

        result.setProcessorType(processInfo.getProcessorType());
        result.setProcessingStatus(processInfo.getProcessingStatus());
        result.setUpdateDateTime(processInfo.getLastUpdated());

        ProcessingEvent processingEvent = processInfo.getProcessingEvent();
        if (processingEvent != null) {
            result.setProcessingEvent(ProcessingEventDTO.from(processingEvent.getLastUpdated(), processingEvent.getEventText()));
        }

        for (ProcessDetail processDetail: processInfo.getProcessDetails()) {
            ProcessDetailDTOImpl processDetailDTO = new ProcessDetailDTOImpl();
            processDetailDTO.setUpdateDateTime(processDetail.getTime());
            processDetailDTO.setStatus(processDetail.getStatus());
            processDetailDTO.setMessage(processDetail.getInfo().getMessage());

            if (processDetail.getRows() != null) {
                processDetailDTO.setRows(processDetail.getRows().longValue());
            }

            List<String> items = processDetail.getInfo().getItems();
            if (items != null) {
                processDetailDTO.setItems(processDetail.getInfo().getItems());
            }

            ProcessingAction processingAction = processDetail.getProcessingAction();
            if (processingAction != null) {
                processDetailDTO.setProcessingAction(ProcessingActionDTO.from(
                        processingAction.getActionType(), processingAction.getValue()));
            }

            result.getProcessDetails().add(processDetailDTO);
        }

        return result;
    }

    public ProcessInfoDTO transform(Collection<ProcessInfoFlatDTO> rs) {
        ProcessInfoDTOImpl result = new ProcessInfoDTOImpl();
        final Map<Long, ProcessDetailDTOImpl> processDetailDTOMap = new HashMap<>();

        for (ProcessInfoFlatDTO row: rs) {
            if (result.getId() == null) {
                result.setId(row.getId());
                result.setProcessorType(row.getProcessorType());
                result.setProcessingStatus(row.getProcessingStatus());
                result.setUpdateDateTime(row.getUpdateDateTime());
            }

            if (row.getDetailId() != null) {
                ProcessDetailDTOImpl processDetailDTO = processDetailDTOMap.computeIfAbsent(
                        row.getDetailId(),
                        v -> {
                            ProcessDetailDTOImpl newProcessDetailDTO = new ProcessDetailDTOImpl();
                            newProcessDetailDTO.setId(v);
                            newProcessDetailDTO.setUpdateDateTime(row.getDetailUpdateDateTime());
                            newProcessDetailDTO.setStatus(row.getDetailProcessingStatus());
                            newProcessDetailDTO.setMessage(row.getDetailMessage());

                            if (row.getDetailRows() != null) {
                                newProcessDetailDTO.setRows(row.getDetailRows().longValue());
                            }

                            // add if not added before
                            result.getProcessDetails().add(newProcessDetailDTO);

                            return newProcessDetailDTO;
                        }
                );

                if (row.getProcessingActionType() != null) {
                    processDetailDTO.setProcessingAction(ProcessingActionDTO.from(
                            row.getProcessingActionType(),
                            row.getProcessingActionValue()
                    ));
                }

                if (row.getDetailItem() != null) {
                    processDetailDTO.getItems().add(row.getDetailItem());
                }
            }
        }

        return result;
    }
}
