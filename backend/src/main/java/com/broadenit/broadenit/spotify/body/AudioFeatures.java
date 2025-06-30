package com.broadenit.broadenit.spotify.body;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AudioFeatures {
    private String id;
    private double acousticness;
    private double danceability;
    private int duration_ms;
    private double energy;
    private double instrumentalness;
    private int key;
    private double liveness;
    private double loudness;
    private int mode;
    private double speechiness;
    private double tempo;
    private int time_signature;
    private double valence;
    private int popularity;


}