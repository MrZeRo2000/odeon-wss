package com.romanpulov.odeonwss.generator;

import com.romanpulov.odeonwss.dto.ArtifactDTO;
import com.romanpulov.odeonwss.dto.ArtistDTO;
import com.romanpulov.odeonwss.dto.MediaFileDTO;
import com.romanpulov.odeonwss.dto.TrackDTO;

import java.util.List;

public class DataGeneratorDTO {
    List<ArtistDTO> artists;
    List<ArtifactDTO> artifacts;
    List<TrackDTO> tracks;
    List<MediaFileDTO> mediaFiles;

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

    public List<TrackDTO> getTracks() {
        return tracks;
    }

    public void setTracks(List<TrackDTO> tracks) {
        this.tracks = tracks;
    }

    public List<MediaFileDTO> getMediaFiles() {
        return mediaFiles;
    }

    public void setMediaFiles(List<MediaFileDTO> mediaFiles) {
        this.mediaFiles = mediaFiles;
    }

    @Override
    public String toString() {
        return "DataGeneratorDTO{" +
                "artists=" + artists +
                ", artifacts=" + artifacts +
                ", tracks=" + tracks +
                ", mediaFiles=" + mediaFiles +
                '}';
    }
}
