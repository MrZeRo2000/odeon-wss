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
            ClassicsValidateProcessor classicsValidateProcessor,

            MP3LoadProcessor mp3LoadProcessor,
            MP3ValidateProcessor mp3ValidateProcessor,
            LALoadProcessor laLoadProcessor,
            LAValidateProcessor laValidateProcessor,

            DVMusicLoadProcessor dvMusicLoadProcessor,
            DVMusicValidateProcessor dvMusicValidateProcessor,
            DVMusicMediaFilesLoadProcessor dvMusicMediaFilesLoadProcessor,

            DVMoviesLoadProcessor dvMoviesLoadProcessor,
            DVMoviesValidateProcessor dvMoviesValidateProcessor,
            DVMoviesMediaFilesLoadProcessor dvMoviesMediaFilesLoadProcessor,

            DVAnimationLoadProcessor dvAnimationLoadProcessor,
            DVAnimationValidateProcessor dvAnimationValidateProcessor,
            DVAnimationMediaFilesLoadProcessor dvAnimationMediaFilesLoadProcessor
    )
    {
        this.appConfiguration = appConfiguration;

        addProcessorMap(ProcessorType.CLASSICS_VALIDATOR, classicsValidateProcessor, AppConfiguration.PathType.PT_CLASSICS);

        addProcessorMap(ProcessorType.MP3_LOADER, mp3LoadProcessor, AppConfiguration.PathType.PT_MP3);
        addProcessorMap(ProcessorType.MP3_VALIDATOR, mp3ValidateProcessor, AppConfiguration.PathType.PT_MP3);
        addProcessorMap(ProcessorType.LA_LOADER, laLoadProcessor, AppConfiguration.PathType.PT_LA);
        addProcessorMap(ProcessorType.LA_VALIDATOR, laValidateProcessor, AppConfiguration.PathType.PT_LA);

        addProcessorMap(ProcessorType.DV_MUSIC_LOADER, dvMusicLoadProcessor, AppConfiguration.PathType.PT_DV_MUSIC);
        addProcessorMap(ProcessorType.DV_MUSIC_VALIDATOR, dvMusicValidateProcessor, AppConfiguration.PathType.PT_DV_MUSIC);
        addProcessorMap(ProcessorType.DV_MUSIC_MEDIA_LOADER, dvMusicMediaFilesLoadProcessor, AppConfiguration.PathType.PT_DV_MUSIC);

        addProcessorMap(ProcessorType.DV_MOVIES_LOADER, dvMoviesLoadProcessor, AppConfiguration.PathType.PT_DV_MOVIES);
        addProcessorMap(ProcessorType.DV_MOVIES_VALIDATOR, dvMoviesValidateProcessor, AppConfiguration.PathType.PT_DV_MOVIES);
        addProcessorMap(ProcessorType.DV_MOVIES_MEDIA_LOADER, dvMoviesMediaFilesLoadProcessor, AppConfiguration.PathType.PT_DV_MOVIES);

        addProcessorMap(ProcessorType.DV_ANIMATION_LOADER, dvAnimationLoadProcessor, AppConfiguration.PathType.PT_DV_ANIMATION);
        addProcessorMap(ProcessorType.DV_ANIMATION_VALIDATOR, dvAnimationValidateProcessor, AppConfiguration.PathType.PT_DV_ANIMATION);
        addProcessorMap(ProcessorType.DV_ANIMATION_MEDIA_LOADER, dvAnimationMediaFilesLoadProcessor, AppConfiguration.PathType.PT_DV_ANIMATION);
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
