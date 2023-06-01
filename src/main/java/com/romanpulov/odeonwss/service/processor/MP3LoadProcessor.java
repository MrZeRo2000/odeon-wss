package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.mapper.MediaFileMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.TrackService;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;
import com.romanpulov.odeonwss.utils.media.MediaFileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class MP3LoadProcessor extends AbstractArtistProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MP3LoadProcessor.class);

    private final ArtifactTypeRepository artifactTypeRepository;

    private final TrackService trackService;

    private final MediaFileMapper mediaFileMapper;

    private final MediaParser mediaParser;

    private static class TracksSummary {
        private long duration;
        private long size;
    }

    public MP3LoadProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtistRepository artistRepository,
            ArtifactRepository artifactRepository,
            MediaFileRepository mediaFileRepository,
            TrackService trackService,
            MediaFileMapper mediaFileMapper,
            MediaParser mediaParser )
    {
        super(artistRepository, artifactRepository, mediaFileRepository);
        this.artifactTypeRepository = artifactTypeRepository;
        this.trackService = trackService;
        this.mediaFileMapper = mediaFileMapper;
        this.mediaParser = mediaParser;
    }

    @Override
    protected ArtifactType getArtifactType() {
        return artifactTypeRepository.getWithMP3();
    }

    @Override
    protected int processTracks(List<Pair<Path, Artifact>> pathArtifacts) throws ProcessorException {
        AtomicInteger counter = new AtomicInteger(0);

        // load track files to flat list
        List<Pair<Path, Pair<Artifact, NamesParser.NumberTitle>>> flatPathArtifacts = new ArrayList<>();
        List<Path> flatPathTracks = new ArrayList<>();
        for (Pair<Path, Artifact> pathArtifactPair: pathArtifacts) {
            logger.debug(String.format("processTracks path=%s", pathArtifactPair.getFirst()));
            processingEventHandler(ProcessorMessages.PROCESSING_TRACKS, pathArtifactPair.getFirst());

            List<Path> trackFiles = new ArrayList<>();
            if (!PathReader.readPathFilesOnly(this, pathArtifactPair.getFirst(), trackFiles)) {
                return counter.get();
            }

            Set<Long> trackNumberSet = new HashSet<>(trackFiles.size());
            for (Path p: trackFiles) {
                String trackFileName = p.getFileName().toString();
                processingEventHandler(ProcessorMessages.PROCESSING_TRACK, p.toAbsolutePath());
                logger.debug(String.format("processTracks trackFileName=%s", trackFileName));

                if (!NamesParser.validateFileNameMediaFormat(trackFileName, getArtifactType().getMediaFileFormats())) {
                    errorHandler(ProcessorMessages.ERROR_WRONG_FILE_TYPE, p.toAbsolutePath());
                    return counter.get();
                }

                NamesParser.NumberTitle nt = NamesParser.parseMusicTrack(trackFileName);
                if (nt == null) {
                    errorHandler(
                            ProcessorMessages.ERROR_PARSING_MUSIC_TRACK_NAME,
                            p.toAbsolutePath().toString()
                    );
                    return counter.get();
                }

                if (!trackNumberSet.add(nt.getNumber())) {
                    errorHandler(
                            ProcessorMessages.ERROR_DUPLICATE_MUSIC_TRACK_NUMBER,
                            p.toAbsolutePath().toString()
                    );
                    return counter.get();
                }

                flatPathArtifacts.add(Pair.of(p, Pair.of(pathArtifactPair.getSecond(), nt)));
                flatPathTracks.add(p);
            }
        }

        Map<Path, MediaFileInfo> parsedTrackMediaInfo = mediaParser.parseTracks(
                flatPathTracks,
                (parsing -> synchronizedProcessingEventHandler(ProcessorMessages.PROCESSING_PARSING_MEDIA_FILE, parsing)),
                (parsed -> synchronizedProcessingEventHandler(ProcessorMessages.PROCESSING_PARSED_MEDIA_FILE, parsed))
        );

        Map<Artifact, List<Pair<Path, Pair<Artifact, NamesParser.NumberTitle>>>> pathArtifactsMap =
                flatPathArtifacts
                        .stream()
                        .collect(Collectors.groupingBy(v -> v.getSecond().getFirst(), Collectors.toList()));

        //process track files
        for (Artifact artifact: pathArtifactsMap.keySet()) {
            TracksSummary summary = new TracksSummary();

            for (Pair<Path, Pair<Artifact, NamesParser.NumberTitle>> flatPathArtifact: pathArtifactsMap.get(artifact)) {
                MediaFileInfo mediaFileInfo = parsedTrackMediaInfo.get(flatPathArtifact.getFirst());
                if (mediaFileInfo != null) {
                    MediaFile mediaFile = mediaFileMapper.fromMediaFileInfo(mediaFileInfo);
                    mediaFile.setArtifact(artifact);
                    mediaFile.setName(flatPathArtifact.getFirst().getFileName().toString());

                    Track track = new Track();
                    track.setArtifact(artifact);
                    track.setTitle(flatPathArtifact.getSecond().getSecond().getTitle());
                    track.setDiskNum(1L);
                    track.setNum(flatPathArtifact.getSecond().getSecond().getNumber());
                    track.setDuration(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration());

                    processingEventHandler(ProcessorMessages.PROCESSING_SAVING_TRACK_WITH_MEDIA, flatPathArtifact.getFirst().toAbsolutePath());
                    trackService.insertTrackWithMedia(track, mediaFile);
                    counter.getAndIncrement();

                    summary.duration += mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration();
                    summary.size += mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getSize();

                }

                artifact.setDuration(summary.duration);
                artifact.setSize(summary.size);
                artifactRepository.save(artifact);
            }
        }

        return counter.get();
    }

}
