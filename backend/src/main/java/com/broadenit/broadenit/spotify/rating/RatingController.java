package com.broadenit.broadenit.spotify.rating;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class RatingController {
    private final RatingService ratingService;


    @PostMapping("/rate/{SpotifyTrackId}")
    public ResponseEntity<Void> rateSong(@PathVariable String SpotifyTrackId, @RequestParam int rating){
        return ratingService.rateSong(SpotifyTrackId, rating);

    }

    @GetMapping("/rating/{SpotifyTrackId}")
    public ResponseEntity<Integer> getRating(@PathVariable String SpotifyTrackId){
        return ratingService.getRating(SpotifyTrackId);
    }

}
