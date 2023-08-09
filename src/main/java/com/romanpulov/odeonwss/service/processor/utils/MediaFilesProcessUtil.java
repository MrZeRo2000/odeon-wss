package com.romanpulov.odeonwss.service.processor.utils;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.mapper.MediaFileMapper;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.parser.MediaParser;
import com.romanpulov.odeonwss.service.processor.vo.SizeDuration;
import com.romanpulov.odeonwss.utils.media.MediaFileInfo;
import com.romanpulov.odeonwss.utils.media.MediaFileInfoException;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MediaFilesProcessUtil {
    public static Set<MediaFile> loadFromMediaFilesPaths(
            Collection<Path> mediaFilesPaths,
            Artifact artifact,
            MediaParser mediaParser,
            MediaFileRepository mediaFileRepository,
            MediaFileMapper mediaFileMapper,
            AtomicInteger counter,
            Consumer<String> parsingFileCallback,
            Consumer<String> parsingErrorCallback
    ) {
        Set<MediaFile> mediaFiles = new HashSet<>();
        for (Path mediaFilePath: mediaFilesPaths) {
            String fileName = mediaFilePath.getFileName().toString();
            MediaFile mediaFile = null;

            if (mediaFileRepository.findFirstByArtifactAndName(artifact, fileName).isEmpty()) {
                try {
                    parsingFileCallback.accept(mediaFilePath.toString());
                    MediaFileInfo mediaFileInfo = mediaParser.parseTrack(mediaFilePath);

                    mediaFile = mediaFileMapper.fromMediaFileInfo(mediaFileInfo);
                    mediaFile.setArtifact(artifact);
                    mediaFile.setName(fileName);

                    mediaFileRepository.save(mediaFile);
                    if (counter != null) {
                        counter.getAndIncrement();
                    }

                } catch (MediaFileInfoException e) {
                    parsingErrorCallback.accept(mediaFilePath.toAbsolutePath().toString());
                }
            } else {
                mediaFile = mediaFileRepository.findFirstByArtifactAndName(artifact, fileName).get();
            }
            if (mediaFile != null) {
                mediaFiles.add(mediaFile);
            }
        }

        return mediaFiles;
    }

    public static SizeDuration getMediaFilesSizeDuration(Collection<MediaFile> mediaFiles) {
        long totalSize = mediaFiles.stream().collect(Collectors.summarizingLong(MediaFile::getSize)).getSum();
        long totalDuration = mediaFiles.stream().collect(Collectors.summarizingLong(MediaFile::getDuration)).getSum();

        return SizeDuration.of(totalSize, totalDuration);
    }
}
