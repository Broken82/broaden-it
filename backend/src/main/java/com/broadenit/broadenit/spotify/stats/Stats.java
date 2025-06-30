package com.broadenit.broadenit.spotify.stats;

import com.broadenit.broadenit.spotify.track.Track;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Stats {
    private double averageRating;
    private int playlistCount;
    private List<Track> topTracks;
}
