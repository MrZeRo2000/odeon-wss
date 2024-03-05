package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.IdNameDTO;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.mapper.MediaFileMapper;
import com.romanpulov.odeonwss.repository.*;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;
import com.romanpulov.odeonwss.service.processor.utils.MediaFilesProcessUtil;
import com.romanpulov.odeonwss.service.processor.utils.PathProcessUtil;
import com.romanpulov.odeonwss.service.processor.vo.SizeDuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class DVMusicLoadProcessor extends AbstractFileSystemProcessor {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(DVMusicLoadProcessor.class);

    private ArtifactType artifactType;

    private final ArtistRepository artistRepository;
    private final ArtifactTypeRepository artifactTypeRepository;
    private final ArtifactRepository artifactRepository;
    private final TrackRepository trackRepository;
    private final MediaFileRepository mediaFileRepository;

    private final MediaFileMapper mediaFileMapper;

    private final MediaParser mediaParser;

    public DVMusicLoadProcessor(
            ArtistRepository artistRepository,
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            TrackRepository trackRepository,
            MediaFileRepository mediaFileRepository,
            MediaFileMapper mediaFileMapper,
            MediaParser mediaParser) {
        this.artistRepository = artistRepository;
        this.artifactTypeRepository = artifactTypeRepository;
        this.artifactRepository = artifactRepository;
        this.trackRepository = trackRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.mediaFileMapper = mediaFileMapper;
        this.mediaParser = mediaParser;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        this.artifactType = Optional.ofNullable(this.artifactType).orElse(artifactTypeRepository.getWithDVMusic());

        Map<String, Long> artists = artistRepository
                .getByTypeOrderByName(ArtistType.ARTIST)
                .stream()
                .collect(Collectors.toMap(IdNameDTO::getName, IdNameDTO::getId));

        // artifacts
        infoHandler(ProcessorMessages.INFO_ARTIFACTS_LOADED,
                PathProcessUtil.processArtifactsPath(
                        this,
                        path,
                        artists,
                        artifactRepository,
                        artifactType,
                        s -> processingEventHandler(ProcessorMessages.PROCESSING_ARTIFACT, s),
                        s -> errorHandler(ProcessorMessages.ERROR_EXPECTED_DIRECTORY, s)));

        // media files and tracks
        Pair<Integer, Integer> mediaFilesTracksResult = processMediaFilesAndTracks(path, artists);

        // report results
        infoHandler(ProcessorMessages.INFO_MEDIA_FILES_LOADED, mediaFilesTracksResult.getFirst());
        if (mediaFilesTracksResult.getSecond() > 0) {
            infoHandler(ProcessorMessages.INFO_TRACKS_LOADED, mediaFilesTracksResult.getSecond());
        }
    }

    private Pair<Integer, Integer> processMediaFilesAndTracks(Path path, Map<String, Long> artists) throws ProcessorException {
        AtomicInteger counter = new AtomicInteger(0);
        AtomicInteger trackCounter = new AtomicInteger(0);

        for (Artifact a: artifactRepository.getAllByArtifactTypeWithTracks(artifactType)) {
            Path mediaFilesRootPath = Paths.get(path.toAbsolutePath().toString(), a.getTitle());

            List<Path> mediaFilesPaths = new ArrayList<>();
            if (!PathReader.readPathPredicateFilesOnly(
                    this,
                    mediaFilesRootPath,
                    p -> NamesParser.validateFileNameMediaFormat(
                            p.getFileName().toString(),
                            this.artifactType.getMediaFileFormats()),
                    mediaFilesPaths)) {
                return Pair.of(counter.get(), trackCounter.get());
            }

            Set<MediaFile> mediaFiles = MediaFilesProcessUtil.loadFromMediaFilesPaths(
                    mediaFilesPaths,
                    a,
                    mediaParser,
                    mediaFileRepository,
                    mediaFileMapper,
                    counter,
                    p -> processingEventHandler(ProcessorMessages.PROCESSING_PARSING_MEDIA_FILE, p),
                    p -> errorHandler(ProcessorMessages.ERROR_PARSING_FILE, p));

            trackCounter.addAndGet(processTracks(a, mediaFiles, artists));

            SizeDuration sizeDuration = MediaFilesProcessUtil.getMediaFilesSizeDuration(mediaFiles);

            if (ValueValidator.isEmpty(a.getSize()) || ValueValidator.isEmpty(a.getDuration())) {
                a.setSize(sizeDuration.getSize());
                a.setDuration(sizeDuration.getDuration());

                artifactRepository.save(a);
            }
        }

        return Pair.of(counter.get(), trackCounter.get());
    }

    private int processTracks(Artifact artifact, Collection<MediaFile> mediaFiles, Map<String, Long> artists) {
        AtomicInteger counter = new AtomicInteger(0);

        for (MediaFile mediaFile: mediaFiles) {
            NamesParser.NumberTitle nt = NamesParser.parseMusicVideoTrack(mediaFile.getName());
            if (nt != null) {
                Track track = new Track();
                track.setArtifact(artifact);

                // find artist
                Long artistId = null;
                if (nt.hasArtistName()) {
                    artistId = artists.get(nt.getArtistName());
                }
                if ((artistId == null) && artifact.getArtist() != null) {
                    artistId = artifact.getArtist().getId();
                }

                // set artist if found
                if (artistId != null) {
                    Artist artist = new Artist();
                    artist.setId(artistId);
                    track.setArtist(artist);
                }

                DVType dvType = new DVType();
                dvType.setId(8L);
                track.setDvType(dvType);

                track.setTitle(nt.getTitle());
                track.setDuration(mediaFile.getDuration());
                track.setNum(nt.getNumber());
                track.setMediaFiles(Set.of(mediaFile));

                trackRepository.save(track);
                counter.getAndIncrement();
            }
        }

        return counter.get();
    }
}
