package com.broadenit.broadenit.spotify.playlists;

import com.broadenit.broadenit.spotify.body.TrackBody;
import com.broadenit.broadenit.spotify.track.Track;
import com.broadenit.broadenit.user.User;
import com.broadenit.broadenit.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class PlaylistController {

    private final UserPlaylistService userPlaylistService;




    @GetMapping("/playlists")
    public ResponseEntity<List<UserPlaylist>> getAllPlaylists() {
        return userPlaylistService.getAllPlaylists();
    }

    @DeleteMapping("/playlists/delete/{playlistId}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long playlistId) {
        return userPlaylistService.deletePlaylist(playlistId);
    }

    @PostMapping("/playlists/create")
    public ResponseEntity<Void> createPlaylist(@RequestParam String name) {
        return userPlaylistService.createPlaylist(name);
    }

    @PostMapping("/playlists/add")
    public ResponseEntity<Void> addTrackToPlaylist(@RequestParam String SpotifyId, @RequestParam Long playlistId) {
        return userPlaylistService.addTrackToPlaylist(SpotifyId, playlistId);
    }

    @GetMapping("/playlists/{playlistId}")
    public ResponseEntity<List<TrackBody>> getPlaylistContent(@PathVariable Long playlistId) {
        return userPlaylistService.getPlaylistContent(playlistId);
    }

    @DeleteMapping("/playlists/{playlistId}/delete/{trackId}")
    public ResponseEntity<Void> deleteTrackFromPlaylist(@PathVariable Long playlistId, @PathVariable String trackId) {
        return userPlaylistService.deleteTrackFromPlaylist(playlistId, trackId);
    }
}
