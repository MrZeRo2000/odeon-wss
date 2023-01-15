package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.config.AppConfiguration;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProcessorFactory {

    private final AppConfiguration appConfiguration;

    private final Map<ProcessorType, Pair<AbstractProcessor, AppConfiguration.PathType>> processorMap = new HashMap<>();

    private void addProcessorMap(ProcessorType processorType, AbstractProcessor processor, AppConfiguration.PathType pathType) {
        processorMap.put(processorType, Pair.of(processor, pathType));
    }

    public ProcessorFactory(
            AppConfiguration appConfiguration,
            ArtistsMDBImportProcessor artistsMDBImportProcessor,
            ClassicsMDBImportProcessor classicsMDBImportProcessor,
            DVMusicMDBImportProcessor dvMusicMDBImportProcessor,
            DVMusicMediaFilesLoadProcessor dvMusicMediaFilesLoadProcessor,
            DVMusicValidateProcessor dvMusicValidateProcessor,
            DVProductMDBImportProcessor dvProductMDBImportProcessor,
            DVMoviesMDBImportProcessor dvMoviesMDBImportProcessor,
            ClassicsValidateProcessor classicsValidateProcessor,
            MP3LoadProcessor mp3LoadProcessor,
            MP3ValidateProcessor mp3ValidateProcessor,
            LALoadProcessor laLoadProcessor,
            LAValidateProcessor laValidateProcessor)
    {
        this.appConfiguration = appConfiguration;

        addProcessorMap(ProcessorType.ARTISTS_IMPORTER, artistsMDBImportProcessor, AppConfiguration.PathType.PT_MDB);
        addProcessorMap(ProcessorType.CLASSICS_IMPORTER, classicsMDBImportProcessor, AppConfiguration.PathType.PT_MDB);
        addProcessorMap(ProcessorType.CLASSICS_VALIDATOR, classicsValidateProcessor, AppConfiguration.PathType.PT_CLASSICS);
        addProcessorMap(ProcessorType.DV_MUSIC_IMPORTER, dvMusicMDBImportProcessor, AppConfiguration.PathType.PT_MDB);
        addProcessorMap(ProcessorType.DV_MUSIC_MEDIA_LOADER, dvMusicMediaFilesLoadProcessor, AppConfiguration.PathType.PT_DV_MUSIC);
        addProcessorMap(ProcessorType.DV_MUSIC_VALIDATOR, dvMusicValidateProcessor, AppConfiguration.PathType.PT_DV_MUSIC);
        addProcessorMap(ProcessorType.DV_PRODUCT_IMPORTER, dvProductMDBImportProcessor, AppConfiguration.PathType.PT_MDB);
        addProcessorMap(ProcessorType.DV_MOVIES_IMPORTER, dvMoviesMDBImportProcessor, AppConfiguration.PathType.PT_MDB);
        addProcessorMap(ProcessorType.MP3_LOADER, mp3LoadProcessor, AppConfiguration.PathType.PT_MP3);
        addProcessorMap(ProcessorType.MP3_VALIDATOR, mp3ValidateProcessor, AppConfiguration.PathType.PT_MP3);
        addProcessorMap(ProcessorType.LA_LOADER, laLoadProcessor, AppConfiguration.PathType.PT_LA);
        addProcessorMap(ProcessorType.LA_VALIDATOR, laValidateProcessor, AppConfiguration.PathType.PT_LA);
    }

    public AbstractProcessor fromProcessorType(ProcessorType processorType, ProgressHandler handler) {
        Pair<AbstractProcessor, AppConfiguration.PathType> processorPath = this.processorMap.get(processorType);
        if (processorPath != null) {
            processorPath.getFirst().setProgressHandler(handler);
            processorPath.getFirst().setRootFolder(appConfiguration.getPathMap().get(processorPath.getSecond()));
            return processorPath.getFirst();
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
