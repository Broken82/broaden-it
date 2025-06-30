package com.broadenit.broadenit.spotify.track;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String coverArtUrl;

    private String title;

    private String spotifyTrackId;

    private String artist;

    private String genre;

    private LocalDate releaseDate;

    private double acousticness;

    private double danceability;

    private double energy;


    private double instrumentalness;

    private double liveness;

    private int popularity;

    private double speechiness;

    private double valence;

    private double tempo;

    private double mode;

    private double key;

    private double time_signature;

    private String previewUrl;

}