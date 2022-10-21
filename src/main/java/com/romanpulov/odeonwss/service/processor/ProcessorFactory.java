package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.springframework.stereotype.Component;

@Component
public class ProcessorFactory {

    private final AppConfiguration appConfiguration;

    private final ArtistsMDBImportProcessor artistsMDBImportProcessor;
    private final ClassicsMDBImportProcessor classicsMDBImportProcessor;
    private final DVMusicMDBImportProcessor dvMusicMDBImportProcessor;
    private final DVMusicLoadProcessor dvMusicLoadProcessor;
    private final ClassicsValidateProcessor classicsValidateProcessor;
    private final MP3LoadProcessor mp3LoadProcessor;
    private final MP3ValidateProcessor mp3ValidateProcessor;
    private final LALoadProcessor laLoadProcessor;
    private final LAValidateProcessor laValidateProcessor;

    public ProcessorFactory(
            AppConfiguration appConfiguration,
            ArtistsMDBImportProcessor artistsMDBImportProcessor,
            ClassicsMDBImportProcessor classicsMDBImportProcessor,
            DVMusicMDBImportProcessor dvMusicMDBImportProcessor,
            DVMusicLoadProcessor dvMusicLoadProcessor,
            ClassicsValidateProcessor classicsValidateProcessor,
            MP3LoadProcessor mp3LoadProcessor,
            MP3ValidateProcessor mp3ValidateProcessor,
            LALoadProcessor laLoadProcessor,
            LAValidateProcessor laValidateProcessor)
    {
        this.appConfiguration = appConfiguration;
        this.artistsMDBImportProcessor = artistsMDBImportProcessor;
        this.classicsMDBImportProcessor = classicsMDBImportProcessor;
        this.classicsValidateProcessor = classicsValidateProcessor;
        this.dvMusicMDBImportProcessor = dvMusicMDBImportProcessor;
        this.dvMusicLoadProcessor = dvMusicLoadProcessor;
        this.mp3LoadProcessor = mp3LoadProcessor;
        this.mp3ValidateProcessor = mp3ValidateProcessor;
        this.laLoadProcessor = laLoadProcessor;
        this.laValidateProcessor = laValidateProcessor;
    }

    public AbstractProcessor fromProcessorType(ProcessorType processorType, ProgressHandler handler) {
        AbstractProcessor processor;
        switch (processorType) {
            case ARTISTS_IMPORTER:
                processor = artistsMDBImportProcessor;
                processor.setProgressHandler(handler);
                processor.setRootFolder(appConfiguration.getMdbPath());
                return processor;
            case CLASSICS_IMPORTER:
                processor = classicsMDBImportProcessor;
                processor.setProgressHandler(handler);
                processor.setRootFolder(appConfiguration.getMdbPath());
                return processor;
            case DV_MUSIC_IMPORTER:
                processor = dvMusicMDBImportProcessor;
                processor.setProgressHandler(handler);
                processor.setRootFolder(appConfiguration.getMdbPath());
                return processor;
            case DV_MUSIC_MEDIA_LOADER:
                processor = dvMusicLoadProcessor;
                processor.setProgressHandler(handler);
                processor.setRootFolder(appConfiguration.getDvMusicPath());
                return processor;
            case CLASSICS_VALIDATOR:
                processor = classicsValidateProcessor;
                processor.setProgressHandler(handler);
                processor.setRootFolder(appConfiguration.getClassicsPath());
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
            case LA_LOADER:
                processor = laLoadProcessor;
                processor.setProgressHandler(handler);
                processor.setRootFolder(appConfiguration.getLaPath());
                return processor;
            case LA_VALIDATOR:
                processor = laValidateProcessor;
                processor.setProgressHandler(handler);
                processor.setRootFolder(appConfiguration.getLaPath());
                return processor;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
