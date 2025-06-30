package com.broadenit.broadenit.spotify.playlists;

import com.broadenit.broadenit.security.JwtProvider;
import com.broadenit.broadenit.spotify.SpotifyAuthService;
import com.broadenit.broadenit.spotify.body.AudioFeatures;
import com.broadenit.broadenit.spotify.body.TrackBody;
import com.broadenit.broadenit.spotify.track.Track;
import com.broadenit.broadenit.spotify.track.TrackRepository;
import com.broadenit.broadenit.user.User;
import com.broadenit.broadenit.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserPlaylistService {

    private final UserRepository userRepository;
    private final UserPlaylistRepository userPlaylistRepository;
    private final PlaylistContentRepository playlistContentRepository;
    private final TrackRepository trackRepository;



    public ResponseEntity<List<UserPlaylist>> getAllPlaylists() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElseThrow();
            List<UserPlaylist> userPlaylists = userPlaylistRepository.findAllByUserId(user.getId());

            System.out.println("Zalogowany użytkownik: " + user.getNickname());
            return ResponseEntity.ok(userPlaylists);
        }

        return null;
    }


    @Transactional
    public ResponseEntity<Void> deletePlaylist(Long playlistId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            playlistContentRepository.deleteByPlaylistId(playlistId);
            userPlaylistRepository.deleteById(playlistId);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<Void> createPlaylist(String name) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElseThrow();
            UserPlaylist userPlaylist = new UserPlaylist();
            userPlaylist.setName(name);
            userPlaylist.setUser(user);
            userPlaylist.setCreatedAt(LocalDate.now());
            userPlaylistRepository.save(userPlaylist);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    //FUNKCJA USUNIĘTA PRZEZ ZMIANY SPOTIFY API
//    public ResponseEntity<Void> addTrackToPlaylistSPOTIFY(TrackBody trackBody) {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null) {
//            return ResponseEntity.badRequest().build();
//        }
//        UserPlaylist userPlaylist = userPlaylistRepository.findById(trackBody.getPlaylistId()).orElseThrow();
//
//        boolean trackAlreadyInPlaylist = playlistContentRepository.existsByPlaylistIdAndTrack_SpotifyTrackId(
//                (long) trackBody.getPlaylistId(), trackBody.getId()
//        );
//
//        if (trackAlreadyInPlaylist) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).build();
//        }
//
//        PlaylistContent playlistContent = new PlaylistContent();
//        playlistContent.setPlaylist(userPlaylist);
//
//        Optional<Track> track = trackRepository.findBySpotifyTrackId(trackBody.getId());
//
//        if(track.isPresent()){
//            playlistContent.setTrack(track.get());
//        }
//        else{
//            String token = spotifyAuthService.getSpotifyToken();
//            WebClient webClient = webClientBuilder
//                    .baseUrl("https://api.spotify.com/v1")
//                    .defaultHeader("Authorization", "Bearer " + token)
//                    .build();
//
//            AudioFeatures audioFeatures = webClient.get()
//                    .uri("/audio-features/{id}", trackBody.getId())
//                    .retrieve()
//                    .bodyToMono(AudioFeatures.class)
//                    .block();
//
//            Track newTrack = getTrack(trackBody, audioFeatures);
//
//            trackRepository.save(newTrack);
//            playlistContent.setTrack(newTrack);
//        }
//
//        playlistContentRepository.save(playlistContent);
//        userPlaylist.setNumberOfTracks(userPlaylist.getNumberOfTracks() + 1);
//        userPlaylistRepository.save(userPlaylist);
//        return ResponseEntity.ok().build();
//    }
//
//    public Track getTrack(TrackBody trackBody, AudioFeatures audioFeatures) {
//        Track newTrack = new Track();
//        newTrack.setSpotifyTrackId(trackBody.getId());
//        newTrack.setArtist(trackBody.getArtist());
//        newTrack.setTitle(trackBody.getTitle());
//        newTrack.setPreviewUrl(trackBody.getPreviewUrl());
//        newTrack.setCoverArtUrl(trackBody.getCoverArtUrl());
//        newTrack.setDanceability(audioFeatures.getDanceability());
//        newTrack.setPopularity(trackBody.getPopularity());
//        newTrack.setEnergy(audioFeatures.getEnergy());
//        newTrack.setSpeechiness(audioFeatures.getSpeechiness());
//        newTrack.setAcousticness(audioFeatures.getAcousticness());
//        newTrack.setInstrumentalness(audioFeatures.getInstrumentalness());
//        newTrack.setLiveness(audioFeatures.getLiveness());
//        newTrack.setValence(audioFeatures.getValence());
//        newTrack.setTempo(audioFeatures.getTempo());
//        newTrack.setMode(audioFeatures.getMode());
//        newTrack.setKey(audioFeatures.getKey());
//        newTrack.setTime_signature(audioFeatures.getTime_signature());
//        return newTrack;
//    }

    public ResponseEntity<Void> addTrackToPlaylist(@RequestParam String SpotifyId, @RequestParam Long playlistId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return ResponseEntity.badRequest().build();
        }
        UserPlaylist userPlaylist = userPlaylistRepository.findById(playlistId).orElseThrow();

        boolean trackAlreadyInPlaylist = playlistContentRepository.existsByPlaylistIdAndTrack_SpotifyTrackId(playlistId, SpotifyId);

        if (trackAlreadyInPlaylist) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        System.out.println("SpotifyId: " + SpotifyId);
        System.out.println("PlaylistId: " + playlistId);

        PlaylistContent playlistContent = new PlaylistContent();
        playlistContent.setPlaylist(userPlaylist);

        Optional<Track> track = trackRepository.findBySpotifyTrackId(SpotifyId);

        track.ifPresent(playlistContent::setTrack);


        playlistContentRepository.save(playlistContent);
        userPlaylist.setNumberOfTracks(userPlaylist.getNumberOfTracks() + 1);
        userPlaylistRepository.save(userPlaylist);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<List<TrackBody>> getPlaylistContent(Long playlistId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return ResponseEntity.badRequest().build();
        }


        List<PlaylistContent> playlistContent = playlistContentRepository.findAllByPlaylistId(playlistId);

        if (playlistContent.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }


        List<TrackBody> tracks = playlistContent.stream()
                .map(content -> {
                    Track track = content.getTrack();
                    return new TrackBody(track.getSpotifyTrackId(), track.getTitle(), track.getArtist(), track.getCoverArtUrl(), track.getPreviewUrl());
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(tracks);
    }

    public ResponseEntity<Void> deleteTrackFromPlaylist(Long playlistId, String trackId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return ResponseEntity.badRequest().build();
        }

        PlaylistContent playlistContent = playlistContentRepository.findByPlaylistIdAndTrack_SpotifyTrackId(playlistId, trackId).orElseThrow();
        playlistContentRepository.delete(playlistContent);

        UserPlaylist userPlaylist = userPlaylistRepository.findById(playlistId).orElseThrow();
        userPlaylist.setNumberOfTracks(userPlaylist.getNumberOfTracks() - 1);
        userPlaylistRepository.save(userPlaylist);

        return ResponseEntity.ok().build();
    }
}
