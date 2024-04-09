package com.romanpulov.odeonwss.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanpulov.jutilscore.io.FileUtils;
import com.romanpulov.odeonwss.dto.ExtraDTO;
import com.romanpulov.odeonwss.dto.MediaFileDTO;
import com.romanpulov.odeonwss.dto.MediaFileDTOImpl;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.MediaFile;
import com.romanpulov.odeonwss.utils.media.model.MediaFileInfo;
import com.romanpulov.odeonwss.utils.media.model.MediaFormatInfo;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class MediaFileMapper implements EntityDTOMapper<MediaFile, MediaFileDTO> {
    private final static DateTimeFormatter CHAPTERS_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final ObjectMapper mapper;

    public MediaFileMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String getEntityName() {
        return "MediaFile";
    }

    @Override
    public MediaFile fromDTO(MediaFileDTO dto) {
        MediaFile entity = new MediaFile();

        // immutable fields
        entity.setId(dto.getId());
        Artifact artifact = new Artifact();
        artifact.setId(dto.getArtifactId());
        entity.setArtifact(artifact);

        // mutable fields
        this.update(entity, dto);

        return entity;
    }

    @Override
    public void update(MediaFile entity, MediaFileDTO dto) {
        entity.setName(dto.getName());
        entity.setFormat(dto.getFormat());
        entity.setSize(dto.getSize());
        entity.setBitrate(dto.getBitrate());
        entity.setDuration(dto.getDuration());
        entity.setWidth(dto.getWidth());
        entity.setHeight(dto.getHeight());
        entity.setExtra(dto.getExtra());
    }

    public MediaFile fromMediaFileInfo(MediaFileInfo mediaFileInfo) {
        MediaFile mediaFile = new MediaFile();

        mediaFile.setName(mediaFileInfo.getFileName());
        mediaFile.setFormat(FileUtils.getExtension(mediaFileInfo.getFileName()).toUpperCase());

        updateFromMediaFileInfo(mediaFile, mediaFileInfo);

        return mediaFile;
    }

    public void updateFromMediaFileInfo(MediaFile mediaFile, MediaFileInfo mediaFileInfo) {
        MediaFormatInfo mediaFormatInfo = mediaFileInfo.getMediaContentInfo().getMediaFormatInfo();
        List<LocalTime> chapters = mediaFileInfo.getMediaContentInfo().getChapters();

        mediaFile.setSize(mediaFormatInfo.getSize());
        mediaFile.setBitrate(mediaFormatInfo.getBitRate());
        mediaFile.setDuration(mediaFormatInfo.getDuration());
        mediaFile.setWidth(mediaFormatInfo.getWidth());
        mediaFile.setHeight(mediaFormatInfo.getHeight());
        mediaFile.setExtra(chaptersToExtra(chapters));
    }

    public String chaptersToExtra(List<LocalTime> chapters) {
        if ((chapters != null) && !chapters.isEmpty()) {
            try {
                ExtraDTO extraDTO = ExtraDTO.from(chapters.stream().map(v -> v.format(CHAPTERS_FORMATTER)).toList());
                return mapper.writeValueAsString(extraDTO);
            } catch (IOException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public List<LocalTime> extraToChapters(String extra) {
        if ((extra != null) && !extra.isEmpty()) {
            try {
                ExtraDTO extraDTO = mapper.readValue(extra, ExtraDTO.class);
                return extraDTO.getExtra()
                        .stream()
                        .map(s -> LocalTime.parse(s, CHAPTERS_FORMATTER))
                        .toList();
            } catch (Exception e) {
                return List.of();
            }
        } else {
            return List.of();
        }
    }

    public MediaFileDTO toDTO(MediaFile entity) {
        MediaFileDTOImpl dto = new MediaFileDTOImpl();

        // attributes only
        dto.setName(entity.getName());
        dto.setFormat(entity.getFormat());
        dto.setSize(entity.getSize());
        dto.setBitrate(entity.getBitrate());
        dto.setDuration(entity.getDuration());
        dto.setWidth(entity.getWidth());
        dto.setHeight(entity.getHeight());
        dto.setExtra(entity.getExtra());

        return dto;
    }
}
