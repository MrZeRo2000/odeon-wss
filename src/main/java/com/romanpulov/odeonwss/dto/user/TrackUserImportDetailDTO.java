package com.romanpulov.odeonwss.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrackUserImportDetailDTO {
    private String title;
    private Long duration;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "TrackUserImportDetailDTO{" +
                "title='" + title + '\'' +
                ", duration=" + duration +
                '}';
    }
}
