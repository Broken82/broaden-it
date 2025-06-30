package com.broadenit.broadenit.spotify;


import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SpotifyTokenScheduler {
        private final SpotifyAuthService spotifyTokenService;

        @Scheduled(fixedRate = 3600000)
        public void refreshToken() {
            spotifyTokenService.getSpotifyToken();
        }
}
