package com.romanpulov.odeonwss.service.processor.model;

import java.time.LocalDateTime;

public class ProcessingEvent {
    private final LocalDateTime lastUpdated;

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    private final String eventText;

    public String getEventText() {
        return eventText;
    }

    private ProcessingEvent(LocalDateTime lastUpdated, String eventText) {
        this.lastUpdated = lastUpdated;
        this.eventText = eventText;
    }

    public static ProcessingEvent fromEventText(String eventText) {
        return new ProcessingEvent(LocalDateTime.now(), eventText);
    }

    @Override
    public String toString() {
        return "ProcessingEvent{" +
                "lastUpdated=" + lastUpdated +
                ", eventText='" + eventText + '\'' +
                '}';
    }
}
