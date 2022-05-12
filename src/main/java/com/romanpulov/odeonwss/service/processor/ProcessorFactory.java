package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.config.AppConfiguration;
import org.springframework.stereotype.Component;

@Component
public class ProcessorFactory {

    private final AppConfiguration appConfiguration;

    private final MP3LoadProcessor mp3LoadProcessor;
    private final MP3ValidateProcessor mp3ValidateProcessor;

    public ProcessorFactory(AppConfiguration appConfiguration, MP3LoadProcessor mp3LoadProcessor, MP3ValidateProcessor mp3ValidateProcessor) {
        this.appConfiguration = appConfiguration;
        this.mp3LoadProcessor = mp3LoadProcessor;
        this.mp3ValidateProcessor = mp3ValidateProcessor;
    }

    public AbstractProcessor fromProcessorType(ProcessorType processorType, ProgressHandler handler) {
        AbstractProcessor processor;
        switch (processorType) {
            case MP3_LOADER:
                processor = mp3LoadProcessor;
                processor.setProgressHandler(handler);
                processor.setRootFolder(appConfiguration.getMp3Path());
                return processor;
            case MP3_VALIDATOR:
                processor = mp3ValidateProcessor;
                processor.setProgressHandler(handler);
                processor.setRootFolder(appConfiguration.getMp3Path());
                return processor;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
