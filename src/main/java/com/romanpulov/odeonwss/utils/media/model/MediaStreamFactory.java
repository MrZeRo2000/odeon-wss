package com.romanpulov.odeonwss.utils.media.model;

import java.util.Comparator;

public class MediaStreamFactory {
    public static AbstractMediaStreamInfo fromMediaType(
            MediaType mediaType,
            long order,
            long duration,
            long bitRate,
            long width,
            long height) {
        if (mediaType.equals(MediaType.AUDIO)) {
            return new MediaStreamAudioInfo(order, duration, bitRate);
        } else if (mediaType.equals(MediaType.VIDEO)) {
            return new MediaStreamVideoInfo(order, duration, bitRate, width, height);
        } else {
            throw new RuntimeException("Unexpected or empty media type");
        }
    }

    public static final Comparator<AbstractMediaStreamInfo> mediaStreamInfoComparator = (s1, s2) -> {
        if (s1 instanceof MediaStreamVideoInfo) {
            return -1;
        } else if (s2 instanceof MediaStreamVideoInfo) {
            return 1;
        } else {
            return 0;
        }
    };
}
