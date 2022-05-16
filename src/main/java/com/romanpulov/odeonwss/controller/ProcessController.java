package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.MessageDTO;
import com.romanpulov.odeonwss.dto.ProcessorRequestDTO;
import com.romanpulov.odeonwss.exception.WrongParameterValueException;
import com.romanpulov.odeonwss.service.ProcessService;
import com.romanpulov.odeonwss.service.processor.ProcessorType;
import com.romanpulov.odeonwss.utils.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/process", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessController {

    ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @PostMapping
    ResponseEntity<MessageDTO> start(@RequestParam ProcessorRequestDTO processorRequest) throws WrongParameterValueException {
        ProcessorType processorType = EnumUtils.getEnumFromString(ProcessorType.class, processorRequest.getProcessorType());
        if (processorType == null) {
            throw new WrongParameterValueException("Processor Type", processorRequest.getProcessorType());
        } else {
            processService.executeProcessor(processorType);
            return ResponseEntity.ok(new MessageDTO("Started"));
        }
    }
}
