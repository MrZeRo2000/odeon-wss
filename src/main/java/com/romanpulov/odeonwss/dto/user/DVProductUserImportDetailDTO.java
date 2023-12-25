package com.romanpulov.odeonwss.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DVProductUserImportDetailDTO {
    private String title;
    private String originalTitle;
    private Long year;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "DVProductUserImportDetailDTO{" +
                "title='" + title + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", year=" + year +
                '}';
    }
}
