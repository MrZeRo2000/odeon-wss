package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExtraDTO {
    private List<String> extra;

    public List<String> getExtra() {
        return extra;
    }

    public void setExtra(List<String> extra) {
        this.extra = extra;
    }

    public static ExtraDTO from(List<String> value) {
        ExtraDTO instance = new ExtraDTO();
        instance.setExtra(value);

        return instance;
    }

    @Override
    public String toString() {
        return "ExtraDTO{" +
                "extra='" + extra + '\'' +
                '}';
    }
}
