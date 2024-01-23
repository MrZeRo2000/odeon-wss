package com.romanpulov.odeonwss.dto.process;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessingEventDTO {
    private LocalDateTime updateDateTime;
    private String message;

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }
    public String getMessage() {
        return message;
    }

    private ProcessingEventDTO() { }

    public static ProcessingEventDTO from(LocalDateTime updateDateTime, String message) {
        ProcessingEventDTO instance = new ProcessingEventDTO();
        instance.updateDateTime = updateDateTime;
        instance.message = message;

        return instance;
    }
}
