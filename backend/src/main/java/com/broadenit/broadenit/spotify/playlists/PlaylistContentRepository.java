package com.broadenit.broadenit.spotify.playlists;

import com.broadenit.broadenit.spotify.body.TrackBody;
import com.broadenit.broadenit.spotify.track.Track;
import com.broadenit.broadenit.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaylistContentRepository extends JpaRepository<PlaylistContent, Long> {
    void deleteByPlaylistId(Long playlistId);

    List<PlaylistContent> findAllByPlaylistId(Long playlistId);

    boolean existsByPlaylistIdAndTrack_SpotifyTrackId(Long playlist_id, String track_spotifyTrackId);

    Optional<PlaylistContent> findByPlaylistIdAndTrack_SpotifyTrackId(Long playlistId, String trackId);

    PlaylistContent findByPlaylistId(Long playlistId);

    boolean existsByPlaylistId(Long playlistId);
}
