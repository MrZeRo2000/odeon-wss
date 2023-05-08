package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.entity.Track;
import com.romanpulov.odeonwss.mapper.MediaFileMapper;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.service.TrackService;
import com.romanpulov.odeonwss.service.processor.parser.CueParser;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;
import com.romanpulov.odeonwss.utils.media.MediaFileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class LALoadProcessor extends AbstractArtistProcessor {

    private static final Logger logger = LoggerFactory.getLogger(LALoadProcessor.class);

    private final ArtifactTypeRepository artifactTypeRepository;

    private final TrackService trackService;

    private final MediaFileMapper mediaFileMapper;

    private final MediaParser mediaParser;

    private static class TracksSummary {
        private int count;
        private long duration;
        private long size;

        private void add(TracksSummary v) {
            this.count += v.count;
            this.duration += v.duration;
            this.size += v.size;
        }

        private boolean isEmpty() {
            return count + duration + size == 0;
        }

        @Override
        public String toString() {
            return "TracksSummary{" +
                    "count=" + count +
                    ", duration=" + duration +
                    ", size=" + size +
                    '}';
        }
    }

    public LALoadProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtistRepository artistRepository,
            ArtifactRepository artifactRepository,
            TrackService trackService,
            MediaFileMapper mediaFileMapper,
            MediaParser mediaParser
    ) {
        super(artistRepository, artifactRepository);
        this.artifactTypeRepository = artifactTypeRepository;
        this.trackService = trackService;
        this.mediaFileMapper = mediaFileMapper;
        this.mediaParser = mediaParser;
    }

    @Override
    protected ArtifactType getArtifactType() {
        return artifactTypeRepository.getWithLA();
    }

    @Override
    protected int processTracks(List<Pair<Path, Artifact>> pathArtifacts) throws ProcessorException {
        int counter = 0;
        for (Pair<Path, Artifact> pathArtifactPair: pathArtifacts) {
            Path path = pathArtifactPair.getFirst();
            Artifact artifact = pathArtifactPair.getSecond();

            TracksSummary summary = processTracksPathWithDiskNum(path, artifact,0);
            logger.info("Artifact:" + pathArtifactPair.getSecond().getTitle() + ", track summary: " + summary);

            artifact.setDuration(summary.duration);
            artifact.setSize(summary.size);
            artifactRepository.save(artifact);

            counter += summary.count;
        }
        return counter;
    }

    private TracksSummary processTracksPathWithDiskNum(Path path, Artifact artifact, int diskNum) throws ProcessorException {
        logger.debug("Processing track path:" + path + " with artifact:" + artifact);

        TracksSummary summary = new TracksSummary();

        List<Path> directoryPaths = new ArrayList<>();
        if (!PathReader.readPathAll(path, directoryPaths)) {
            return summary;
        }

        List<Path> directoryFolderPaths = directoryPaths
                .stream()
                .filter(Files::isDirectory)
                .collect(Collectors.toList());

        Map<Path, Integer> directoryFolderDisksPaths = directoryFolderPaths
                .stream()
                .collect(Collectors.toMap(p -> p, p -> NamesParser.getDiskNumFromFolderName(p.getFileName().toString())))
                .entrySet()
                .stream()
                .filter(v -> v.getValue() > 0)
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        if (directoryFolderDisksPaths.size() > 0) {
            if (directoryFolderPaths.size() > directoryFolderDisksPaths.size()) {
                errorHandler(ProcessorMessages.ERROR_FOLDER_WITH_DISK_NUMBERS_CONTAINS_OTHER, path.toString());
            } else {
                for (Map.Entry<Path, Integer> entry: directoryFolderDisksPaths.entrySet()) {
                    summary.add(processTracksPathWithDiskNum(entry.getKey(), artifact, entry.getValue()));
                }
            }
        } else {
            List<String> directoryFileNames = directoryPaths
                    .stream()
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList());

            //validation for Cue
            List<Path> cuePaths = directoryPaths.stream().filter(p -> p.toString().endsWith("cue")).collect(Collectors.toList());
            if (cuePaths.size() > 0) {
                logger.debug("Cue files found, processing");
                boolean cueProcessed = false;
                for (Path cuePath : cuePaths) {
                    List<CueParser.CueTrack> cueTracks = CueParser.parseFile(cuePath);
                    List<String> cueFiles = cueTracks
                            .stream()
                            .map(CueParser.CueTrack::getFileName)
                            .distinct().collect(Collectors.toList());

                    if (new HashSet<>(directoryFileNames).containsAll(cueFiles)) {
                        logger.debug("Contains all for " + cuePath.toString());
                        summary.add(processCueFile(path, artifact, directoryPaths, cuePath, cueTracks, cueFiles, diskNum));
                        cueProcessed = true;
                    }
                }
                if (!cueProcessed) {
                    errorHandler(ProcessorMessages.ERROR_FILES_IN_CUE_NOT_FOUND, path.toString());
                }
            } else {
                logger.debug("Cue files not found, processing files");
                //no cue files,
                summary.add(processTracks(path, artifact, directoryPaths, diskNum));
            }

            if (summary.isEmpty()) {
                errorHandler(ProcessorMessages.ERROR_NO_DATA_FOR_FOLDER, path.toString());
            }
        }

        return summary;
    }

    private TracksSummary processCueFile(
            Path path,
            Artifact artifact,
            List<Path> directoryPaths,
            Path cuePath,
            List<CueParser.CueTrack> cueTracks,
            List<String> cueFiles,
            int diskNum
    ) throws ProcessorException {
        TracksSummary summary = new TracksSummary();

        logger.debug("Processing Cue");

        Set<String> cueFileNames = cueTracks.stream().map(CueParser.CueTrack::getFileName).collect(Collectors.toSet());

        // get only files which are tracks
        List<Path> trackPaths = directoryPaths
                .stream()
                .filter(p -> cueFileNames.contains(p.getFileName().toString()))
                .collect(Collectors.toList());

        Map<String, MediaFileInfo> parsedTrackMediaInfo = mediaParser.parseTracks(trackPaths)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey().getFileName().toString(), Map.Entry::getValue));

        Map<String, MediaFile> mediaFiles = new HashMap<>();
        List<Track> tracks = new ArrayList<>();

        logger.debug("Track paths: " + trackPaths);

        int firstSection = 0;
        for (int i = 0; i < cueTracks.size(); i++) {
            CueParser.CueTrack cueTrack = cueTracks.get(i);
            boolean lastTrack = i == cueTracks.size() - 1;
            CueParser.CueTrack nextCueTrack = lastTrack ? null : cueTracks.get(i + 1);

            if (i == 0) {
                firstSection = cueTrack.getSection();
            }

            String fileName = cueTrack.getFileName();

            MediaFileInfo mediaFileInfo = parsedTrackMediaInfo.get(fileName);
            if (mediaFileInfo == null) {
                throw new ProcessorException(ProcessorMessages.ERROR_NO_DATA_FOR_FILE, cueTrack.getFileName());
            } else {
                MediaFile mediaFile;
                if (mediaFiles.containsKey(fileName)) {
                    mediaFile = mediaFiles.get(fileName);
                } else {
                    mediaFile = mediaFileMapper.fromMediaFileInfo(mediaFileInfo);
                    mediaFile.setArtifact(artifact);
                    mediaFiles.put(fileName, mediaFile);
                    summary.size += mediaFile.getSize();
                }

                logger.debug("MediaFile saved: " + mediaFile);
                Track track = new Track();
                track.setArtifact(artifact);
                track.setTitle(cueTrack.getTitle());

                // disk num
                long trackDiskNum = diskNum > 0 ?
                        diskNum :
                        Integer.valueOf(NamesParser.getDiskNumFromFileName(cuePath.getFileName().toString())).longValue();
                track.setDiskNum(trackDiskNum);

                track.setNum(Integer.valueOf(cueTrack.getNum()).longValue());

                // duration
                long mediaDuration = mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration();
                long cueDuration;
                if (nextCueTrack != null) {
                    cueDuration = nextCueTrack.getSection() - cueTrack.getSection();
                } else {
                    cueDuration = mediaDuration - cueTrack.getSection() + firstSection;
                }
                long duration = cueDuration > 0 ? cueDuration : mediaDuration;
                track.setDuration(duration);

                summary.count ++;
                summary.duration += duration;

                track.getMediaFiles().add(mediaFile);

                tracks.add(track);
            }
        }

        trackService.insertTracksWithMedia(tracks, mediaFiles.values());
        return summary;
    }

    TracksSummary processTracks(
            Path path,
            Artifact artifact,
            List<Path> directoryPaths,
            int diskNum
    ) throws ProcessorException {
        TracksSummary summary = new TracksSummary();

        Map<String, NamesParser.NumberTitle> parsedTrackFileNames = parseTrackFileNames(directoryPaths);
        if (parsedTrackFileNames.size() > 0) {
            List<MediaFile> mediaFiles = new ArrayList<>();
            List<Track> tracks = new ArrayList<>();

            List<Path> directoryTracksPaths = directoryPaths
                    .stream()
                    .filter(p -> parsedTrackFileNames.containsKey(p.getFileName().toString()))
                    .collect(Collectors.toList());
            Map<String, MediaFileInfo> parsedTrackMediaInfo = mediaParser.parseTracks(directoryTracksPaths)
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(e -> e.getKey().getFileName().toString(), Map.Entry::getValue));

            for (Map.Entry<String, NamesParser.NumberTitle> trackFile: parsedTrackFileNames.entrySet()) {
                MediaFileInfo mediaFileInfo = parsedTrackMediaInfo.get(trackFile.getKey());
                MediaFile mediaFile = mediaFileMapper.fromMediaFileInfo(mediaFileInfo);
                mediaFile.setArtifact(artifact);

                mediaFiles.add(mediaFile);

                Track track = new Track();
                track.setArtifact(artifact);
                track.setTitle(trackFile.getValue().getTitle());

                int trackDiskNum = diskNum == 0 ? 1 : diskNum;
                track.setDiskNum(Integer.valueOf(trackDiskNum).longValue());

                track.setNum(trackFile.getValue().getNumber());
                track.setDuration(mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration());

                track.getMediaFiles().add(mediaFile);

                tracks.add(track);

                summary.count ++;
                summary.duration += mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getDuration();
                summary.size += mediaFileInfo.getMediaContentInfo().getMediaFormatInfo().getSize();
            }

            trackService.insertTracksWithMedia(tracks, mediaFiles);
        } else {
            errorHandler(ProcessorMessages.ERROR_NO_DATA_FOR_FOLDER, path.toString());
        }

        return summary;
    }

    private Map<String, NamesParser.NumberTitle> parseTrackFileNames(List<Path> trackPaths) {
        Map<String, NamesParser.NumberTitle> result = new HashMap<>();

        for (Path path: trackPaths) {
            String trackFileName = path.getFileName().toString();
            NamesParser.NumberTitle nt = NamesParser.parseMusicTrack(trackFileName);
            if (nt != null) {
                result.put(trackFileName, nt);
            }
        }

        return result;
    }
}
