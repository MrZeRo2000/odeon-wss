package com.romanpulov.odeonwss.entity;

import com.romanpulov.odeonwss.entity.converter.DateConverter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "artifacts")
public class Artifact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artf_id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attp_id", referencedColumnName = "attp_id")
    @NotNull
    private ArtifactType artifactType;

    public ArtifactType getArtifactType() {
        return artifactType;
    }

    public void setArtifactType(ArtifactType artifactType) {
        this.artifactType = artifactType;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arts_id", referencedColumnName = "arts_id")
    @Nullable
    private Artist artist;

    @Nullable
    public Artist getArtist() {
        return artist;
    }

    public void setArtist(@Nullable Artist artist) {
        this.artist = artist;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perf_arts_id", referencedColumnName = "arts_id")
    @Nullable
    private Artist performerArtist;

    @Nullable
    public Artist getPerformerArtist() {
        return performerArtist;
    }

    public void setPerformerArtist(@Nullable Artist performerArtist) {
        this.performerArtist = performerArtist;
    }

    @Column(name = "artf_title")
    @NotNull
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "artf_year")
    @Nullable
    private Long year;

    @Nullable
    public Long getYear() {
        return year;
    }

    public void setYear(@Nullable Long year) {
        this.year = year;
    }

    @Column(name = "artf_duration")
    @Nullable
    private Long duration;

    @Nullable
    public Long getDuration() {
        return duration;
    }

    public void setDuration(@Nullable Long duration) {
        this.duration = duration;
    }

    @Column(name = "artf_size")
    @Nullable
    private Long size;

    @Nullable
    public Long getSize() {
        return size;
    }

    public void setSize(@Nullable Long size) {
        this.size = size;
    }

    @Column(name = "artf_ins_date")
    @Convert(converter = DateConverter.class)
    @Nullable
    private LocalDate insertDate;

    @Nullable
    public LocalDate getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(@Nullable LocalDate insertDate) {
        this.insertDate = insertDate;
    }

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "artifact", fetch = FetchType.LAZY)
    private List<Composition> compositions;

    public List<Composition> getCompositions() {
        return compositions;
    }

    public void setCompositions(List<Composition> compositions) {
        this.compositions = compositions;
    }

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "artifact", fetch = FetchType.LAZY)
    private List<MediaFile> mediaFiles;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artifact artifact = (Artifact) o;
        return id.equals(artifact.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Artifact{" +
                "id=" + id +
                ", artifactType=" + artifactType +
                ", artist=" + artist +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", duration=" + duration +
                ", size=" + size +
                ", insertDate=" + insertDate +
                ", compositions=" + compositions +
                ", mediaFiles=" + mediaFiles +
                '}';
    }
}
