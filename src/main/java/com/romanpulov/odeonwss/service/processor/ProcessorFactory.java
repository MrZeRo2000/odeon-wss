package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.springframework.stereotype.Component;

@Component
public class ProcessorFactory {

    private final AppConfiguration appConfiguration;

    private final ArtistsMDBImportProcessor artistsMDBImportProcessor;
    private final MP3LoadProcessor mp3LoadProcessor;
    private final MP3ValidateProcessor mp3ValidateProcessor;

    public ProcessorFactory(
            AppConfiguration appConfiguration,
            ArtistsMDBImportProcessor artistsMDBImportProcessor,
            MP3LoadProcessor mp3LoadProcessor,
            MP3ValidateProcessor mp3ValidateProcessor)
    {
        this.appConfiguration = appConfiguration;
        this.artistsMDBImportProcessor = artistsMDBImportProcessor;
        this.mp3LoadProcessor = mp3LoadProcessor;
        this.mp3ValidateProcessor = mp3ValidateProcessor;
    }

    public AbstractProcessor fromProcessorType(ProcessorType processorType, ProgressHandler handler) {
        AbstractProcessor processor;
        switch (processorType) {
            case ARTISTS_IMPORTER:
                processor = artistsMDBImportProcessor;
                processor.setProgressHandler(handler);
                processor.setRootFolder(appConfiguration.getMdbPath());
                return processor;
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
