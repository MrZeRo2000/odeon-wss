package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.IdTitleOriginalTitleDTO;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.mapper.MediaFileMapper;
import com.romanpulov.odeonwss.repository.*;
import com.romanpulov.odeonwss.service.DVProductService;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;
import com.romanpulov.odeonwss.service.processor.utils.MediaFilesProcessUtil;
import com.romanpulov.odeonwss.service.processor.utils.PathProcessUtil;
import com.romanpulov.odeonwss.service.processor.vo.SizeDuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Abstract super class for Movies, Animation
 */
public class AbstractDVNonMusicLoadProcessor extends AbstractFileSystemProcessor {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(AbstractDVNonMusicLoadProcessor.class);

    private ArtifactType artifactType;

    private DVType dvType;

    private final ArtifactTypeRepository artifactTypeRepository;

    private final ArtifactRepository artifactRepository;

    private final TrackRepository trackRepository;

    private final MediaFileRepository mediaFileRepository;

    private final MediaFileMapper mediaFileMapper;

    private final DVTypeRepository dvTypeRepository;

    private final DVProductRepository dvProductRepository;

    private final DVProductService dVProductService;

    private final MediaParser mediaParser;

    private final Function<ArtifactTypeRepository, ArtifactType> artifactTypeSupplier;

    public AbstractDVNonMusicLoadProcessor(
            ArtifactTypeRepository artifactTypeRepository,
            ArtifactRepository artifactRepository,
            TrackRepository trackRepository,
            MediaFileRepository mediaFileRepository,
            MediaFileMapper mediaFileMapper,
            DVTypeRepository dvTypeRepository,
            DVProductRepository dvProductRepository,
            DVProductService dVProductService,
            MediaParser mediaParser,
            Function<ArtifactTypeRepository, ArtifactType> artifactTypeSupplier) {
        this.artifactTypeRepository = artifactTypeRepository;
        this.artifactRepository = artifactRepository;
        this.trackRepository = trackRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.mediaFileMapper = mediaFileMapper;
        this.dvTypeRepository = dvTypeRepository;
        this.dvProductRepository = dvProductRepository;
        this.dVProductService = dVProductService;
        this.mediaParser = mediaParser;
        this.artifactTypeSupplier = artifactTypeSupplier;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        if (this.artifactType == null) {
            this.artifactType = artifactTypeSupplier.apply(artifactTypeRepository);
        }

        if (this.dvType == null) {
            this.dvType = dvTypeRepository.getReferenceById(7L);
        }

        infoHandler(ProcessorMessages.INFO_ARTIFACTS_LOADED,
                PathProcessUtil.processArtifactsPath(
                        this,
                        path,
                        null,
                        artifactRepository,
                        artifactType,
                        null,
                        s -> processingEventHandler(ProcessorMessages.PROCESSING_ARTIFACT, s),
                        s -> errorHandler(ProcessorMessages.ERROR_EXPECTED_DIRECTORY, s)));

        List<Artifact> artifacts = this.artifactRepository.getAllByArtifactTypeWithTracks(artifactType)
                .stream()
                .filter(a -> a.getTracks().isEmpty())
                .collect(Collectors.toList());
        Map<Artifact, Collection<Path>> artifactPaths = loadArtifactPaths(path, artifactType, artifacts);

        Set<Artifact> multipleTrackArtifacts = getMultipleTrackArtifacts(artifactPaths);

        Map<Artifact, Collection<Path>> singleTracksArtifactPaths = artifactPaths.entrySet()
                .stream()
                .filter(a -> !multipleTrackArtifacts.contains(a.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<Artifact, Collection<Path>> multipleTracksArtifactPaths = artifactPaths.entrySet()
                .stream()
                .filter(a -> multipleTrackArtifacts.contains(a.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        int trackRows = processTracks(singleTracksArtifactPaths.keySet());
        int mediaFileRows = processMediaFiles(singleTracksArtifactPaths);
        Pair<Integer, Integer> tracksMediaFilesRows = processTracksAndMediaFiles(multipleTracksArtifactPaths);

        infoHandler(ProcessorMessages.INFO_TRACKS_LOADED, trackRows + tracksMediaFilesRows.getFirst());
        infoHandler(ProcessorMessages.INFO_MEDIA_FILES_LOADED, mediaFileRows + tracksMediaFilesRows.getSecond());
    }

    private Map<Artifact, Collection<Path>> loadArtifactPaths(
            Path path,
            ArtifactType artifactType,
            Collection<Artifact> artifacts) throws ProcessorException {
        Map<Artifact, Collection<Path>> result = new HashMap<>();

        for (Artifact artifact : artifacts) {
            List<Path> paths = new ArrayList<>();
            if (!PathReader.readPathPredicateFilesOnly(
                    this,
                    Path.of(path.toAbsolutePath().toString(), artifact.getTitle()),
                    p -> NamesParser.validateFileNameMediaFormat(
                            p.getFileName().toString(),
                            artifactType.getMediaFileFormats()),
                    paths)) {
                return result;
            }

            if (!paths.isEmpty()) {
                result.put(artifact, paths);
            }
        }

        return result;
    }

    private Set<Artifact> getMultipleTrackArtifacts(Map<Artifact, Collection<Path>> artifactPaths) {
        return artifactPaths
                .entrySet()
                .stream()
                .filter(e -> e.getValue()
                        .stream()
                        .allMatch(p -> NamesParser.validateVideoTrack(p.getFileName().toString())))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private int processTracks(Collection<Artifact> artifacts) {
        AtomicInteger counter = new AtomicInteger(0);

        for (Artifact artifact : artifacts) {
            Track track = new Track();
            track.setArtifact(artifact);
            track.setDvType(dvType);
            track.setTitle(artifact.getTitle());
            track.setNum(1L);
            track.setDuration(artifact.getDuration());
            dVProductService.findProductByArtifactTypeAndTitle(artifactType, track.getTitle())
                    .ifPresent(p -> track.setDvProducts(Set.of(p)));

            trackRepository.save(track);

            artifact.getTracks().add(track);
            artifactRepository.save(artifact);

            counter.getAndIncrement();
        }

        return counter.get();
    }

    private int processMediaFiles(Map<Artifact, Collection<Path>> artifactPaths) {
        AtomicInteger counter = new AtomicInteger(0);

        for (Artifact a : artifactPaths.keySet()) {
            Collection<Path> mediaFilesPaths = artifactPaths.get(a);

            Set<MediaFile> mediaFiles = MediaFilesProcessUtil.loadFromMediaFilesPaths(
                    mediaFilesPaths,
                    a,
                    mediaParser,
                    mediaFileRepository,
                    mediaFileMapper,
                    counter,
                    p -> processingEventHandler(ProcessorMessages.PROCESSING_PARSING_MEDIA_FILE, p),
                    p -> errorHandler(ProcessorMessages.ERROR_PARSING_FILE, p));

            if (a.getTracks().size() == 1) {
                Track track = trackRepository
                        .findByIdWithMediaFiles(a.getTracks().get(0).getId()).orElseThrow();
                if (!track.getMediaFiles().equals(mediaFiles)) {
                    track.setMediaFiles(mediaFiles);

                    trackRepository.save(track);
                }
            }

            SizeDuration sizeDuration = MediaFilesProcessUtil.getMediaFilesSizeDuration(mediaFiles);

            if (
                    ValueValidator.isEmpty(a.getSize()) ||
                            ValueValidator.isEmpty(a.getDuration())) {
                a.setSize(sizeDuration.getSize());
                a.setDuration(sizeDuration.getDuration());

                artifactRepository.save(a);
            }

            if (
                    (a.getTracks().size() == 1) &&
                            ValueValidator.isEmpty(a.getTracks().get(0).getDuration())) {
                a.getTracks().get(0).setDuration(sizeDuration.getDuration());

                trackRepository.save(a.getTracks().get(0));
            }
        }

        return counter.get();
    }

    private Pair<Integer, Integer> processTracksAndMediaFiles(Map<Artifact, Collection<Path>> artifactPaths)
            throws ProcessorException {
        Map<String, Track> trackMap = new HashMap<>();
        AtomicInteger mediaFilesCounter = new AtomicInteger();

        List<IdTitleOriginalTitleDTO> productIdTitlesOriginalTitles =
                dvProductRepository.findAllIdTitleOriginalTitle(artifactType);
        Map<String, Long> productTitles = productIdTitlesOriginalTitles
                .stream()
                .filter(v -> v.getTitle() != null)
                .collect(Collectors.toMap(v -> NamesParser.cleanupFileName(v.getTitle()), IdTitleOriginalTitleDTO::getId));
        Map<String, Long> productOriginalTitles = productIdTitlesOriginalTitles
                .stream()
                .filter(v -> v.getOriginalTitle() != null)
                .collect(Collectors.toMap(v -> NamesParser.cleanupFileName(v.getOriginalTitle()), IdTitleOriginalTitleDTO::getId));

        for (Artifact a : artifactPaths.keySet()) {
            Set<MediaFile> mediaFiles = MediaFilesProcessUtil.loadFromMediaFilesPaths(
                    artifactPaths.get(a),
                    a,
                    mediaParser,
                    mediaFileRepository,
                    mediaFileMapper,
                    mediaFilesCounter,
                    p -> processingEventHandler(ProcessorMessages.PROCESSING_PARSING_MEDIA_FILE, p),
                    p -> errorHandler(ProcessorMessages.ERROR_PARSING_FILE, p));

            SizeDuration sizeDuration = SizeDuration.empty();
            for (MediaFile mediaFile: mediaFiles) {
                NamesParser.NumberTitle nt = NamesParser.parseVideoTrack(mediaFile.getName());
                if (nt == null) {
                    throw new ProcessorException("Error parsing video track name: " + mediaFile.getName());
                }

                // calc size duration
                sizeDuration.addSize(mediaFile.getSize());
                sizeDuration.addDuration(mediaFile.getDuration());

                // create track if not present yet, expect track title to be unique within artifact
                Track track = trackMap.computeIfAbsent(nt.getTitle(), v -> {
                    Track newTrack = new Track();
                    newTrack.setArtifact(a);
                    newTrack.setDvType(dvType);
                    newTrack.setTitle(nt.getTitle());
                    newTrack.setNum(nt.getNumber());

                    Long productId = productTitles.getOrDefault(newTrack.getTitle(),
                            productOriginalTitles.getOrDefault(newTrack.getTitle(), null));
                    if (productId != null) {
                        dvProductRepository
                                .findById(productId)
                                .ifPresent(dvProduct -> newTrack.setDvProducts(Set.of(dvProduct)));
                    }

                    return newTrack;
                });

                track.setDuration(Optional.ofNullable(track.getDuration()).orElse(0L) + mediaFile.getDuration());
                track.getMediaFiles().add(mediaFile);

                trackRepository.save(track);

                a.getTracks().add(track);
            }

            a.setSize(sizeDuration.getSize());
            a.setDuration(sizeDuration.getDuration());
            artifactRepository.save(a);
        }

        return Pair.of(trackMap.size(), mediaFilesCounter.get());
    }
}
