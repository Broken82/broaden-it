package com.broadenit.broadenit.spotify.playlists;

import com.broadenit.broadenit.spotify.track.Track;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PlaylistContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "playlist_id", nullable = false)
    private UserPlaylist playlist;

    @ManyToOne
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;
}