package com.romanpulov.odeonwss.entity;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "dv_products")
@AttributeOverride(name = "id", column = @Column(name = "dvpd_id"))
@AttributeOverride(name = "insertDateTime", column = @Column(name = "dvpd_ins_datm"))
@AttributeOverride(name = "updateDateTime", column = @Column(name = "dvpd_upd_datm"))
@AttributeOverride(name = "migrationId", column = @Column(name = "dvpd_migration_id"))
public class DVProduct extends AbstractBaseMigratedEntity {
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
    @JoinColumn(name = "dvor_id", referencedColumnName = "dvor_id")
    @NotNull
    private DVOrigin dvOrigin;

    public DVOrigin getDvOrigin() {
        return dvOrigin;
    }

    public void setDvOrigin(DVOrigin dvOrigin) {
        this.dvOrigin = dvOrigin;
    }

    @Column(name = "dvpd_title")
    @NotNull
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "dvpd_orig_title")
    private String originalTitle;

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    @Column(name = "dvpd_year")
    private Long year;

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    @Column(name = "dvpd_front_info")
    private String frontInfo;

    public String getFrontInfo() {
        return frontInfo;
    }

    public void setFrontInfo(String frontInfo) {
        this.frontInfo = frontInfo;
    }

    @Column(name = "dvpd_description")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "dvpd_notes")
    private String notes;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "dv_products_dv_categories",
            joinColumns = @JoinColumn(name = "dvpd_id"),
            inverseJoinColumns = @JoinColumn(name = "dvct_id")
    )
    private Set<DVCategory> dvCategories = new HashSet<>();

    public Set<DVCategory> getDvCategories() {
        return dvCategories;
    }

    public void setDvCategories(Set<DVCategory> dvCategories) {
        this.dvCategories = dvCategories;
    }

    @ManyToMany(mappedBy = "dvProducts")
    private Set<Track> tracks = new HashSet<>();

    public Set<Track> getTracks() {
        return tracks;
    }

    public void setTracks(Set<Track> tracks) {
        this.tracks = tracks;
    }

    @PreRemove
    private void removeDvProduct() {
        if (tracks != null && !tracks.isEmpty()) {
            throw new HibernateException("Unable to delete " + this + " because it has tracks");
        }
    }

    public DVProduct() {
    }

    public static DVProduct fromId(Long id) {
        DVProduct dvProduct = new DVProduct();
        dvProduct.setId(id);

        return dvProduct;
    }

    public DVProduct(Long id, DVOrigin dvOrigin, String title, String originalTitle, Long year, String frontInfo, String description, String notes) {
        this.setId(id);
        this.dvOrigin = dvOrigin;
        this.title = title;
        this.originalTitle = originalTitle;
        this.year = year;
        this.frontInfo = frontInfo;
        this.description = description;
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DVProduct dvProduct = (DVProduct) o;
        return getId().equals(dvProduct.getId()) && title.equals(dvProduct.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), title);
    }

    @Override
    public String toString() {
        return "DVProduct{" +
                "id=" + getId() +
                ", dvOrigin=" + (Hibernate.isInitialized(dvOrigin) ? dvOrigin : "not initialized") +
                ", title='" + title + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", year=" + year +
                ", frontInfo='" + frontInfo + '\'' +
                ", description='" + description + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}