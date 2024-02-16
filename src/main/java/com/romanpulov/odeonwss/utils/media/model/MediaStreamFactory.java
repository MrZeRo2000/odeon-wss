package com.romanpulov.odeonwss.utils.media.model;

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
}
