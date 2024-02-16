package com.romanpulov.odeonwss.utils.media;

import com.romanpulov.odeonwss.utils.EnumUtils;
import com.romanpulov.odeonwss.utils.media.model.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class FFMPEGMediaFileParser extends AbstractCLIMediaFileParser {
    private final static String FFPROBE_FILE_NAME = "ffprobe.exe";

    public FFMPEGMediaFileParser(String ffprobePath) {
        super(ffprobePath, FFPROBE_FILE_NAME);
    }

    @Override
    protected List<String> getProcessCommands(Path file) {
        return List.of(
                this.executableFileName,
                "-print_format",
                "json",
                "-show_format",
                "-show_streams",
                "-v",
                "quiet",
                file.toAbsolutePath().toString()
        );
    }

    @Override
    protected MediaContentInfo parseOutput(String text) throws MediaInfoParsingException {
        try {
            JSONObject jsonObject = new JSONObject(text);

            JSONArray jsonStreamsArray = jsonObject.optJSONArray("streams");
            if (jsonStreamsArray == null) {
                throw new MediaInfoParsingException("Streams information not found:" + text);
            }

            JSONObject jsonFormatObject = jsonObject.optJSONObject("format");
            if (jsonFormatObject == null) {
                throw new MediaInfoParsingException("Format information not found:" + text);
            }

            return new MediaContentInfo(
                    parseMediaFormat(jsonFormatObject),
                    parseMediaStreams(jsonStreamsArray),
                    List.of()
            );

        } catch (JSONException e) {
            throw new MediaInfoParsingException("Error parsing JSON:" + text);
        }
    }

    private List<AbstractMediaStreamInfo> parseMediaStreams(JSONArray jsonStreamsArray) throws JSONException {
        List<AbstractMediaStreamInfo> result = new ArrayList<>();

        for (int i = 0; i < jsonStreamsArray.length(); i++) {
            JSONObject streamObject = jsonStreamsArray.getJSONObject(i);

            String mediaTypeString = streamObject.getString("codec_type");
            MediaType mediaType = EnumUtils.getEnumFromString(MediaType.class, mediaTypeString);
            if (mediaType != null) {
                // for flac there may be no bitrate
                // https://trac.ffmpeg.org/ticket/4195
                long duration = Math.round(streamObject.optDouble("duration", 0));
                long bitRate = Math.round(streamObject.optInt("bit_rate", 0) / 1000.0);

                result.add(MediaStreamFactory.fromMediaType(mediaType, 0, duration, bitRate, 0, 0));
            }
        }

        return result;
    }

    private MediaFormatInfo parseMediaFormat(JSONObject jsonFormatObject) throws JSONException {
        String formatName = jsonFormatObject.getString("format_name");
        long duration = Math.round(jsonFormatObject.getDouble("duration"));
        long size = jsonFormatObject.getLong("size");
        long bitRate = Math.round(jsonFormatObject.getInt("bit_rate") / 1000.0);

        return new MediaFormatInfo(formatName, duration, size, bitRate);
    }
}
