package com.romanpulov.odeonwss.generator;

import com.romanpulov.odeonwss.dto.ArtifactDTO;
import com.romanpulov.odeonwss.dto.ArtistDTO;
import com.romanpulov.odeonwss.dto.MediaFileDTO;
import com.romanpulov.odeonwss.dto.TrackDTO;

import java.util.List;

public class DataGeneratorDTO {
    List<ArtistDTO> artists;
    List<ArtifactDTO> artifacts;
    List<MediaFileDTO> mediaFiles;
    List<TrackDTO> tracks;

    public List<ArtistDTO> getArtists() {
        return artists;
    }

    public void setArtists(List<ArtistDTO> artists) {
        this.artists = artists;
    }

    public List<ArtifactDTO> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<ArtifactDTO> artifacts) {
        this.artifacts = artifacts;
    }

    public List<MediaFileDTO> getMediaFiles() {
        return mediaFiles;
    }

    public void setMediaFiles(List<MediaFileDTO> mediaFiles) {
        this.mediaFiles = mediaFiles;
    }

    public List<TrackDTO> getTracks() {
        return tracks;
    }

    public void setTracks(List<TrackDTO> tracks) {
        this.tracks = tracks;
    }

    @Override
    public String toString() {
        return "DataGeneratorDTO{" +
                "artists=" + artists +
                ", artifacts=" + artifacts +
                ", mediaFiles=" + mediaFiles +
                ", tracks=" + tracks +
                '}';
    }
}
