package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.mapper.MediaFileMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.service.CompositionService;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;
import com.romanpulov.odeonwss.utils.media.MediaFileInfo;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MP3LoadProcessor extends AbstractArtistProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MP3LoadProcessor.class);

    private final CompositionService compositionService;

    private final MediaParser mediaParser;

    private static class CompositionsSummary {
        private long duration;
        private long size;
    }

    public MP3LoadProcessor(
            ArtistRepository artistRepository,
            ArtifactRepository artifactRepository,
            CompositionService compositionService,
            MediaParser mediaParser )
    {
        super(artistRepository, artifactRepository);
        this.compositionService = compositionService;
        this.mediaParser = mediaParser;
    }

    @Override
    protected ArtifactType getArtifactType() {
        return ArtifactType.withMP3();
    }

    @Override
    protected int processCompositions(List<Pair<Path, Artifact>> pathArtifacts) throws ProcessorException {
        AtomicInteger counter = new AtomicInteger(0);

        // load composition files to flat list
        List<Pair<Path, Pair<Artifact, NamesParser.NumberTitle>>> flatPathArtifacts = new ArrayList<>();
        List<Path> flatPathCompositions = new ArrayList<>();
        for (Pair<Path, Artifact> pathArtifactPair: pathArtifacts) {
            List<Path> compositionFiles = new ArrayList<>();
            if (!PathReader.readPathFilesOnly(this, pathArtifactPair.getFirst(), compositionFiles)) {
                return counter.get();
            }

            for (Path p: compositionFiles) {
                String compositionFileName = p.getFileName().toString();
                if (!compositionFileName.endsWith("mp3")) {
                    errorHandler(ProcessorMessages.ERROR_WRONG_FILE_TYPE, p.toAbsolutePath());
                    return counter.get();
                }

                NamesParser.NumberTitle nt = NamesParser.parseMusicComposition(compositionFileName);
                if (nt == null) {
                    errorHandler(ProcessorMessages.ERROR_PARSING_COMPOSITION_NAME, p.toAbsolutePath().getFileName());
                    return counter.get();
                }

                flatPathArtifacts.add(Pair.of(p, Pair.of(pathArtifactPair.getSecond(), nt)));
                flatPathCompositions.add(p);
            }
        }

        Map<Path, MediaFileInfo> parsedCompositionMediaInfo = mediaParser.parseCompositions(flatPathCompositions);

        Map<Artifact, List<Pair<Path, Pair<Artifact, NamesParser.NumberTitle>>>> pathArtifactsMap =
                flatPathArtifacts
                        .stream()
                        .collect(Collectors.groupingBy(v -> v.getSecond().getFirst(), Collectors.toList()));

        //process composition files
        for (Artifact artifact: pathArtifactsMap.keySet()) {
            CompositionsSummary summary = new CompositionsSummary();

            for (Pair<Path, Pair<Artifact, NamesParser.NumberTitle>> flatPathArtifact: pathArtifactsMap.get(artifact)) {
                MediaFileInfo mediaFileInfo = parsedCompositionMediaInfo.get(flatPathArtifact.getFirst());
                if (mediaFileInfo != null) {
                    MediaFile mediaFile = MediaFileMapper.fromMediaFileInfo(mediaFileInfo);
                    mediaFile.setArtifact(artifact);
                    mediaFile.setName(flatPathArtifact.getFirst().getFileName().toString());

                    Composition composition = new Composition();
                    composition.setArtifact(artifact);
                    composition.setTitle(flatPathArtifact.getSecond().getSecond().getTitle());
                    composition.setDiskNum(1L);
                    composition.setNum(flatPathArtifact.getSecond().getSecond().getNumber());
                    composition.setDuration(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration());

                    compositionService.insertCompositionWithMedia(composition, mediaFile);
                    counter.getAndIncrement();

                    summary.duration += mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration();
                    summary.size += mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getSize();

                }

                artifact.setDuration(summary.duration);
                artifact.setSize(summary.size);
                artifact.setInsertDate(LocalDate.now());
                artifactRepository.save(artifact);
            }
        }

        return counter.get();
    }

}
