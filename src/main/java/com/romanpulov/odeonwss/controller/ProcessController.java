package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.MessageDTO;
import com.romanpulov.odeonwss.dto.ProcessorRequestDTO;
import com.romanpulov.odeonwss.dto.process.ProcessInfoDTO;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.exception.DataNotFoundException;
import com.romanpulov.odeonwss.exception.WrongParameterValueException;
import com.romanpulov.odeonwss.repository.DBProcessInfoRepository;
import com.romanpulov.odeonwss.service.ProcessService;
import com.romanpulov.odeonwss.service.processor.model.ProcessingAction;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import com.romanpulov.odeonwss.utils.EnumUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/process", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessController {

    private final ProcessService processService;
    private final DBProcessInfoRepository dbProcessInfoRepository;

    public ProcessController(
            ProcessService processService,
            DBProcessInfoRepository dbProcessInfoRepository) {
        this.processService = processService;
        this.dbProcessInfoRepository = dbProcessInfoRepository;
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
    ResponseEntity<ProcessInfoDTO> getProcessInfo() throws DataNotFoundException {
        if (processService.getProcessInfo() == null) {
            throw new DataNotFoundException("Progress data not available");
        } else {
            return ResponseEntity.ok(processService.getProcessInfoDTO());
        }
    }

    @DeleteMapping
    ResponseEntity<MessageDTO> clearProcessInfo() {
        processService.clearProcessInfo();
        return ResponseEntity.ok(MessageDTO.fromMessage("Cleared"));
    }

    @GetMapping("/table")
    ResponseEntity<List<ProcessInfoDTO>> getTable() {
        return ResponseEntity.ok(dbProcessInfoRepository.findAllOrderedByUpdateDateTime());
    }

    @GetMapping("/{id}")
    ResponseEntity<ProcessInfoDTO> get(@PathVariable Long id) throws CommonEntityNotFoundException {
        return ResponseEntity.ok(processService.getById(id));
    }

    @PostMapping("/resolve")
    ResponseEntity<ProcessInfoDTO> resolveAction(@RequestBody ProcessingAction processingAction) {
        processService.getProcessInfo().resolveAction(processingAction);
        return ResponseEntity.ok(processService.getProcessInfoDTO());
    }
}
