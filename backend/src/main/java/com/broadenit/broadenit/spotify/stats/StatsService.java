package com.broadenit.broadenit.spotify.stats;

import com.broadenit.broadenit.spotify.playlists.PlaylistContent;
import com.broadenit.broadenit.spotify.playlists.PlaylistContentRepository;
import com.broadenit.broadenit.spotify.playlists.UserPlaylist;
import com.broadenit.broadenit.spotify.playlists.UserPlaylistRepository;
import com.broadenit.broadenit.spotify.rating.Rating;
import com.broadenit.broadenit.spotify.rating.RatingRepository;
import com.broadenit.broadenit.spotify.track.Track;
import com.broadenit.broadenit.user.User;
import com.broadenit.broadenit.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class StatsService {

    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final PlaylistContentRepository playlistContentRepository;
    private final UserPlaylistRepository userPlaylistRepository;

    public Mono<Stats> getUserProfileStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("User not authenticated");

        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Stats stats = new Stats();

        int totalRatings = ratingRepository.countByUserId(user);
        int sumRatings = ratingRepository.sumRatingByUserId(user);
        stats.setAverageRating(totalRatings == 0 ? 0 : (double) sumRatings / totalRatings);
        System.out.println("Average rating: " + stats.getAverageRating());

        List<UserPlaylist> AllPlaylists = userPlaylistRepository.findAllByUserId(user.getId());
        List<PlaylistContent> AllPlaylistContent = AllPlaylists.stream()
                .map(playlist -> playlistContentRepository.findAllByPlaylistId(playlist.getId()))
                .flatMap(List::stream)
                .toList();
        stats.setPlaylistCount(AllPlaylistContent.size());
        System.out.println("Playlist count: " + stats.getPlaylistCount());


        List<Track> top5Tracks = ratingRepository.findTop5TracksByUserId(user).stream().limit(5).toList();

        top5Tracks.forEach(track -> System.out.println(track.getTitle()));
        stats.setTopTracks(top5Tracks);

        return Mono.just(stats);



    }
}
