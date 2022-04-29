package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.config.AppConfiguration;
import org.springframework.stereotype.Component;

@Component
public class ProcessorFactory {

    private final AppConfiguration appConfiguration;

    private final LoadMP3Processor loadMP3Processor;

    public ProcessorFactory(AppConfiguration appConfiguration, LoadMP3Processor loadMP3Processor) {
        this.appConfiguration = appConfiguration;
        this.loadMP3Processor = loadMP3Processor;
    }

    public AbstractProcessor fromProcessorType(ProcessorType processorType, ProgressHandler handler) {
        switch (processorType) {
            case MP3_LOADER:
                AbstractProcessor processor = loadMP3Processor;
                processor.setProgressHandler(handler);
                processor.setRootPath(appConfiguration.getMp3Path());
                return processor;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
