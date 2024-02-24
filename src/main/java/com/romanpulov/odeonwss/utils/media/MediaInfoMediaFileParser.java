package com.romanpulov.odeonwss.utils.media;

import com.romanpulov.odeonwss.utils.EnumUtils;
import com.romanpulov.odeonwss.utils.media.model.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MediaInfoMediaFileParser extends AbstractCLIMediaFileParser {
    private final Logger logger = LoggerFactory.getLogger(MediaInfoMediaFileParser.class);

    private final static String MEDIA_INFO_FILE_NAME = "MediaInfo.exe";

    private static final Pattern REGEXP_PATTERN_CHAPTER_DURATION =
            Pattern.compile("_?(\\d{2})_(\\d{2})_(\\d{2})_?(\\d{3})?");

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
                logger.debug(ExceptionUtils.getStackTrace(e));
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

    private MediaFormatInfo parseGeneralSection(Map<String, Object> section) {
        return MediaFormatInfo.fromGeneralAttributes(
                (String) section.getOrDefault("Format", ""),
                Math.round(Double.parseDouble((String) section.getOrDefault("Duration", "0"))),
                Long.parseLong((String) section.getOrDefault("FileSize", "0")),
                Math.round(Long.parseLong((String) section.getOrDefault("OverallBitRate","0")) / 1000.)
        );
    }

    private AbstractMediaStreamInfo parseMediaStreamSection(MediaType mediaType, Map<String, Object> section) {
        // to cover different kinds of bitrate
        Object bitRateObject =
                section.getOrDefault("BitRate",
                section.getOrDefault("BitRate_Maximum",
                section.getOrDefault("BitRate_Nominal", "0")));
        AtomicLong bitRate = new AtomicLong(Long.parseLong((String) bitRateObject));
        if (bitRate.get() == 0) {
            section
                    .keySet()
                    .stream()
                    .filter(s -> s.startsWith("BitRate"))
                    .forEach(bitRateKey -> {
                        try {
                            long newBitRate = Long.parseLong(bitRateKey);
                            if (newBitRate > 0) {
                                bitRate.set(newBitRate);
                            }
                        } catch (NumberFormatException e) {
                            logger.debug(ExceptionUtils.getStackTrace(e));
                        }
                    });
        }

        //width and height
        Object widthObject =
                section.getOrDefault("Width",
                section.getOrDefault("Sampled_Width",
                section.getOrDefault("Stored_Width", "0")));

        Object heightObject =
                section.getOrDefault("Height",
                section.getOrDefault("Sampled_Height",
                section.getOrDefault("Stored_Height", "0")));

        return MediaStreamFactory.fromMediaType(
                mediaType,
                Long.parseLong((String) section.getOrDefault("StreamOrder", "0")),
                Math.round(
                        Double.parseDouble((String) section.getOrDefault("Duration", "0"))),
                Math.round(bitRate.get() / 1000.),
                Long.parseLong((String) widthObject),
                Long.parseLong((String) heightObject)
        );
    }

    List<LocalTime> parseChaptersSection(Map<String, Object> section) {
        Object extraObject = section.getOrDefault("extra", null);
        if (extraObject instanceof Map) {
            return  ((Map<?, ?>) extraObject)
                    .keySet()
                    .stream()
                    .map(v -> {
                        Matcher matcher = REGEXP_PATTERN_CHAPTER_DURATION.matcher((String) v);
                        if (matcher.find() && (matcher.groupCount() >= 3)) {
                            int correction =
                                    ((matcher.groupCount() == 4) && (Integer.parseInt(matcher.group(4)) >= 500)) ?
                                            1:
                                            0;
                            return LocalTime.of(
                                    Integer.parseInt(matcher.group(1)),
                                    Integer.parseInt(matcher.group(2)),
                                    Integer.parseInt(matcher.group(3))
                            ).plusSeconds(correction);
                        } else {
                            return LocalTime.MIN;
                        }
                    })
                    .filter(t -> t.isAfter(LocalTime.MIN))
                    .sorted()
                    .toList();
        } else {
            return List.of();
        }
    }

    private MediaContentInfo parseMedia(JSONArray tracks) throws JSONException, MediaInfoParsingException {
        Map<MediaType, AbstractMediaStreamInfo> mediaStreamMap = new HashMap<>();
        MediaFormatInfo generalMediaFormat = null;
        List<LocalTime> chapters = List.of();

        for (int i = 0; i < tracks.length(); i++) {
            Map<String, Object> tracksMap = tracks.getJSONObject(i).toMap();

            String trackType = (String) tracksMap.getOrDefault("@type", "");

            if (trackType.equals("General")) {
                generalMediaFormat = this.parseGeneralSection(tracksMap);
            } else if (trackType.equals("Menu")) {
                chapters = this.parseChaptersSection(tracksMap);
            } else {
                MediaType mediaType = EnumUtils.getEnumFromString(MediaType.class, trackType.toUpperCase());
                if (mediaType != null) {
                    mediaStreamMap.putIfAbsent(mediaType, this.parseMediaStreamSection(mediaType, tracksMap));
                }
            }
        }

        AbstractMediaStreamInfo primaryMediaStreamInfo =
                mediaStreamMap.getOrDefault(MediaType.VIDEO,
                mediaStreamMap.getOrDefault(MediaType.AUDIO, null));

        if (primaryMediaStreamInfo == null) {
            throw new MediaInfoParsingException("Media stream information not found: " + tracks);
        }

        if (generalMediaFormat == null) {
            throw new MediaInfoParsingException("General media info not found");
        }

        MediaStreamVideoInfo mediaStreamVideoInfo =
                primaryMediaStreamInfo instanceof MediaStreamVideoInfo ?
                        (MediaStreamVideoInfo)primaryMediaStreamInfo :
                        null;

        MediaFormatInfo resultMediaFormat = MediaFormatInfo.fromAllAttributes(
                generalMediaFormat.getFormatName(),
                Math.max(generalMediaFormat.getDuration(), primaryMediaStreamInfo.getDuration()),
                generalMediaFormat.getSize(),
                primaryMediaStreamInfo.getBitRate() == 0 ? generalMediaFormat.getBitRate() : primaryMediaStreamInfo.getBitRate(),
                mediaStreamVideoInfo == null ? null : mediaStreamVideoInfo.getWidth(),
                mediaStreamVideoInfo == null ? null : mediaStreamVideoInfo.getHeight()
        );

        List<AbstractMediaStreamInfo> mediaStreams = mediaStreamMap
                .values()
                .stream()
                .sorted(MediaStreamFactory.mediaStreamInfoComparator)
                .collect(Collectors.toList());

        return new MediaContentInfo(resultMediaFormat, mediaStreams, chapters);
    }
}
