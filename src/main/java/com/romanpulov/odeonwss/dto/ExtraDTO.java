package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExtraDTO {
    private String extra;

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "ExtraDTO{" +
                "extra='" + extra + '\'' +
                '}';
    }
}
