package com.romanpulov.odeonwss.utils.media;

import com.romanpulov.odeonwss.utils.EnumUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class FFMPEGMediaFileParser implements MediaFileParserInterface {
    private final String executableFileName;

    public String getExecutableFileName() {
        return executableFileName;
    }

    public FFMPEGMediaFileParser(String executableFileName) {
        this.executableFileName = executableFileName;
    }

    @Override
    public MediaFileInfo parseMediaFile(Path file) throws MediaFileInfoException {
        try {
            Process process = new ProcessBuilder()
                    .command(
                            this.executableFileName,
                            "-print_format",
                            "json",
                            "-show_format",
                            "-show_streams",
                            "-v",
                            "quiet",
                            file.toAbsolutePath().toString()
                    )
                    .start();

            String inputStreamText = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining());

            String errorStreamText = new BufferedReader(
                    new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining());

            if (errorStreamText.isEmpty()) {
                MediaContentInfo mediaContentInfo = parseOutput(inputStreamText);
                return new MediaFileInfo(
                        file.getFileName().toString(),
                        getPrimaryMediaTypeFromStreams(mediaContentInfo.getMediaStreams()),
                        mediaContentInfo
                );
            } else {
                throw new MediaFileInfoException(file.getFileName().toString(), "Error during file processing:" + errorStreamText);
            }
        } catch (IOException e) {
            throw new MediaFileInfoException(file.getFileName().toString(), "IO Error:" + e.getMessage());
        } catch (MediaInfoParsingException e) {
            throw new MediaFileInfoException(file.getFileName().toString(), e.getMessage());
        }
    }

    private static MediaType getPrimaryMediaTypeFromStreams(List<MediaStreamInfo> mediaStreams) throws MediaInfoParsingException {
        if (mediaStreams.size() > 0) {
            return mediaStreams.get(0).getMediaType();
        } else {
            throw new MediaInfoParsingException("Error obtaining media primary type from streams");
        }
    }

    private MediaContentInfo parseOutput(String text) throws MediaInfoParsingException {
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
                    parseMediaStreams(jsonStreamsArray),
                    parseMediaFormat(jsonFormatObject)
            );

        } catch (JSONException e) {
            throw new MediaInfoParsingException("Error parsing JSON:" + text);
        }
    }

    private List<MediaStreamInfo> parseMediaStreams(JSONArray jsonStreamsArray) throws JSONException, MediaInfoParsingException {
        List<MediaStreamInfo> result = new ArrayList<>();

        for (int i = 0; i < jsonStreamsArray.length(); i++) {
            JSONObject streamObject = jsonStreamsArray.getJSONObject(i);

            String mediaTypeString = streamObject.getString("codec_type");
            MediaType mediaType = EnumUtils.getEnumFromString(MediaType.class, mediaTypeString);
            if (mediaType == null) {
                throw new MediaInfoParsingException("Unknown media type:" + mediaTypeString);
            }

            long duration = Math.round(streamObject.getDouble("duration"));
            long bitRate = Math.round(streamObject.getInt("bit_rate") / 1000.0);

            result.add(new MediaStreamInfo(mediaType, duration, bitRate));
        }

        return result;
    }

    private MediaFormatInfo parseMediaFormat(JSONObject jsonFormatObject)  throws JSONException {
        String formatName = jsonFormatObject.getString("format_name");
        long duration = Math.round(jsonFormatObject.getDouble("duration"));
        long size = jsonFormatObject.getLong("size");
        long bitRate = Math.round(jsonFormatObject.getInt("bit_rate") / 1000.0);

        return new MediaFormatInfo(formatName, duration, size, bitRate);
    }
}
