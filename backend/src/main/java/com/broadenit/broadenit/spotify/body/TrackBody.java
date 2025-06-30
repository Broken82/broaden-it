package com.broadenit.broadenit.spotify.body;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class TrackBody {

    private String id;
    private String title;
    private String artist;
    private int playlistId;
    private String coverArtUrl;
    private String previewUrl;
    private int popularity;



    public TrackBody(String spotifyTrackId, String title, String artist, String coverArtUrl, String previewUrl) {
        this.id = spotifyTrackId;
        this.title = title;
        this.artist = artist;
        this.coverArtUrl = coverArtUrl;
        this.previewUrl = previewUrl;
    }
}
