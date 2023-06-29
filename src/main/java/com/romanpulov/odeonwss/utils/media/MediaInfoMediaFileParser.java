package com.romanpulov.odeonwss.utils.media;

import com.romanpulov.odeonwss.utils.EnumUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class MediaInfoMediaFileParser extends AbstractCLIMediaFileParser {
    private final static String MEDIA_INFO_FILE_NAME = "MediaInfo.exe";

    public MediaInfoMediaFileParser(String mediaInfoPath) {
        super(mediaInfoPath, MEDIA_INFO_FILE_NAME);
    }

    @Override
    protected List<String> getProcessCommands(Path file) {
        return List.of(
                this.executableFileName,
                "--Output=JSON",
                file.toAbsolutePath().toString()
        );
    }

    private String cleanup(String text) {
        return text.replaceAll(",\\n?\"extra\":\\s*\\{[^\\}]+\\}", "");
    }

    @Override
    protected MediaContentInfo parseOutput(String text) throws MediaInfoParsingException {
        try {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(text);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonObject == null) {
                jsonObject = new JSONObject(cleanup(text));
            }

            JSONObject mediaObject = jsonObject.optJSONObject("media");
            if (mediaObject == null) {
                jsonObject = new JSONObject(cleanup(text));
                throw new MediaInfoParsingException("Media information not found:" + jsonObject);
            }

            JSONArray tracksArray = mediaObject.optJSONArray("track");
            if (tracksArray == null) {
                throw new MediaInfoParsingException("Tracks information not found in media:" + mediaObject);
            }

            return parseMedia(tracksArray);
        } catch (JSONException e) {
            throw new MediaInfoParsingException("Error parsing JSON:" + text);
        }
    }

    private MediaContentInfo parseMedia(JSONArray tracks) throws JSONException, MediaInfoParsingException {
        Map<MediaType, MediaStreamInfo> mediaStreamMap = new HashMap<>();
        MediaFormatInfo generalMediaFormat = new MediaFormatInfo();

        for (int i = 0; i < tracks.length(); i++) {
            Map<String, Object> tracksMap = tracks.getJSONObject(i).toMap();

            String trackType = (String) tracksMap.getOrDefault("@type", "");

            if (trackType.equals("General")) {
                generalMediaFormat.setSize(
                        Long.parseLong((String) tracksMap.getOrDefault("FileSize", "0")));
                generalMediaFormat.setDuration(
                        Math.round(Double.parseDouble((String) tracksMap.getOrDefault("Duration", "0"))));
                generalMediaFormat.setFormatName((String) tracksMap.getOrDefault("Format", ""));
            } else {
                MediaType mediaType = EnumUtils.getEnumFromString(MediaType.class, trackType.toUpperCase());
                if (mediaType != null) {
                    MediaStreamInfo mediaStream = MediaStreamInfo.createOrdered(
                            mediaType,
                            Long.parseLong((String) tracksMap.getOrDefault("StreamOrder", "0")),
                            Math.round(
                                    Double.parseDouble((String) tracksMap.getOrDefault("Duration", "0"))),
                            Math.round(
                                    Long.parseLong((String) tracksMap.getOrDefault("BitRate", "0")) / 1000.0)
                            );
                    mediaStreamMap.put(mediaType, mediaStream);
                }
            }
        }
        MediaStreamInfo mediaStreamInfo = mediaStreamMap.getOrDefault(MediaType.VIDEO,
                mediaStreamMap.getOrDefault(MediaType.AUDIO, null));

        if (mediaStreamInfo == null) {
            throw new MediaInfoParsingException("Media stream information not found: " + tracks);
        }

        MediaFormatInfo resultMediaFormat = new MediaFormatInfo(
                generalMediaFormat.getFormatName(),
                Math.max(generalMediaFormat.getDuration(), mediaStreamInfo.getDuration()),
                generalMediaFormat.getSize(),
                mediaStreamInfo.getBitRate()
        );

        List<MediaStreamInfo> mediaStreams = mediaStreamMap
                .values()
                .stream()
                .sorted(Comparator.comparing(MediaStreamInfo::getOrder))
                .collect(Collectors.toList());

        return new MediaContentInfo(mediaStreams, resultMediaFormat);
    }
}
