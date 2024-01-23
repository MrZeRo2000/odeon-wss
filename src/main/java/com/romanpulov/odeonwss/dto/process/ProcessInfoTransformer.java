package com.romanpulov.odeonwss.dto.process;

import com.romanpulov.odeonwss.service.processor.model.ProcessDetail;
import com.romanpulov.odeonwss.service.processor.model.ProcessInfo;
import com.romanpulov.odeonwss.service.processor.model.ProcessingAction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProcessInfoTransformer {
    public ProcessInfoDTO transform(ProcessInfo processInfo) {
        ProcessInfoDTOImpl result = new ProcessInfoDTOImpl();

        result.setProcessorType(processInfo.getProcessorType());
        result.setProcessingStatus(processInfo.getProcessingStatus());
        result.setUpdateDateTime(processInfo.getLastUpdated());

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
                processDetailDTO.setProcessingAction(ProcessingActionDTOImpl.from(
                        processingAction.getActionType(), processingAction.getValue()));
            }

            result.getProcessDetails().add(processDetailDTO);
        }

        return result;
    }
}
