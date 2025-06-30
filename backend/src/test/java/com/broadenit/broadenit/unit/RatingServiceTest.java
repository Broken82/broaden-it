package com.broadenit.broadenit.unit;

import com.broadenit.broadenit.spotify.rating.Rating;
import com.broadenit.broadenit.spotify.rating.RatingRepository;
import com.broadenit.broadenit.spotify.rating.RatingService;
import com.broadenit.broadenit.spotify.track.Track;
import com.broadenit.broadenit.spotify.track.TrackRepository;
import com.broadenit.broadenit.user.User;
import com.broadenit.broadenit.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {
    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TrackRepository trackRepository;

    @InjectMocks
    private RatingService ratingService;

    @Test
    void rateNewSong() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("123@gmail.com");
        SecurityContextHolder.setContext(securityContext);

        User user = new User();
        user.setEmail("123@gmail.com");
        when(userRepository.findByEmail("123@gmail.com")).thenReturn(Optional.of(user));

        Track track = new Track();
        when(trackRepository.findBySpotifyTrackId("trackId")).thenReturn(Optional.of(track));


        when(ratingRepository.findByUserIdAndTrackId(user, track)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = ratingService.rateSong("trackId", 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(ratingRepository, times(1)).save(any(Rating.class));
    }

    @Test
    void NoAuthentication() {

        SecurityContextHolder.clearContext();


        ResponseEntity<Void> response = ratingService.rateSong("trackId", 5);


        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(ratingRepository, never()).save(any(Rating.class));
    }

}
