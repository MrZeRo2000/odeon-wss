package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.MessageDTO;
import com.romanpulov.odeonwss.dto.ProcessorRequestDTO;
import com.romanpulov.odeonwss.exception.DataNotFoundException;
import com.romanpulov.odeonwss.exception.WrongParameterValueException;
import com.romanpulov.odeonwss.service.ProcessService;
import com.romanpulov.odeonwss.service.processor.model.ProcessInfo;
import com.romanpulov.odeonwss.service.processor.model.ProcessingAction;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import com.romanpulov.odeonwss.utils.EnumUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/process", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessController {

    final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @PostMapping
    ResponseEntity<MessageDTO> execute(@RequestBody ProcessorRequestDTO processorRequest) throws WrongParameterValueException {
        ProcessorType processorType = EnumUtils.getEnumFromString(ProcessorType.class, processorRequest.getProcessorType());
        if (processorType == null) {
            throw new WrongParameterValueException("Processor Type", processorRequest.getProcessorType());
        } else {
            processService.executeProcessorAsync(processorType);
            return ResponseEntity.ok(MessageDTO.fromMessage("Started"));
        }
    }

    @GetMapping
    ResponseEntity<ProcessInfo> getProcessInfo() throws DataNotFoundException {
        if (processService.getProcessInfo() == null) {
            throw new DataNotFoundException("Progress data not available");
        } else {
            return ResponseEntity.ok(processService.getProcessInfo());
        }
    }

    @DeleteMapping
    ResponseEntity<MessageDTO> clearProcessInfo() {
        processService.clearProcessInfo();
        return ResponseEntity.ok(MessageDTO.fromMessage("Cleared"));
    }

    @PostMapping("/resolve")
    ResponseEntity<ProcessInfo> resolveAction(@RequestBody ProcessingAction processingAction) {
        processService.getProcessInfo().resolveAction(processingAction);
        return ResponseEntity.ok(processService.getProcessInfo());
    }
}
