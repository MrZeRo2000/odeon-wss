package com.romanpulov.odeonwss.config;

import com.romanpulov.odeonwss.utils.media.FFMPEGMediaFileParser;
import com.romanpulov.odeonwss.utils.media.MediaFileParserInterface;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MediaFileParserConfiguration {

    private final AppConfiguration appConfiguration;

    public MediaFileParserConfiguration(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    @Bean
    public MediaFileParserInterface getMediaFileParser() {
        return new FFMPEGMediaFileParser(appConfiguration.getFFProbePath());
    }
}
