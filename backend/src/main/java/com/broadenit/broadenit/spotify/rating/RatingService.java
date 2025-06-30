package com.broadenit.broadenit.spotify.rating;

import com.broadenit.broadenit.spotify.track.Track;
import com.broadenit.broadenit.spotify.track.TrackRepository;
import com.broadenit.broadenit.user.User;
import com.broadenit.broadenit.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final TrackRepository trackRepository;


    public ResponseEntity<Void> rateSong(String SpotifyTrackId, int userRating) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return ResponseEntity.badRequest().build();
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        Track track = trackRepository.findBySpotifyTrackId(SpotifyTrackId).orElseThrow();

        Rating ratingExists = ratingRepository.findByUserIdAndTrackId(user, track).orElseGet(() -> null);

        if (ratingExists != null) {
            ratingExists.setRating(userRating);
            ratingRepository.save(ratingExists);
            return ResponseEntity.ok().build();
        }

        Rating rating = new Rating();

        rating.setRating(userRating);
        rating.setUserId(user);
        rating.setTrackId(track);
        ratingRepository.save(rating);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Integer> getRating(String SpotifyTrackId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return ResponseEntity.badRequest().build();
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        Track track = trackRepository.findBySpotifyTrackId(SpotifyTrackId).orElseThrow();

        Rating rating = ratingRepository.findByUserIdAndTrackId(user, track).orElseGet(() -> null);

        if (rating == null) {
            return ResponseEntity.ok(0);
        }

        return ResponseEntity.ok(rating.getRating());
    }
}
