package com.broadenit.broadenit.spotify.recommendations;

import com.broadenit.broadenit.spotify.body.AudioFeatures;
import com.broadenit.broadenit.spotify.body.TrackBody;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@AllArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final ManualRecommendationService manualRecommendationService;
    private final CollabFilteringRecommendationService collabFilteringRecommendationService;

    @GetMapping("/recommendations/content")
    public Mono<String> getRecommendations(@RequestParam Long playlistId) throws Exception {
        return recommendationService.getRecommendations(playlistId);
    }

    @GetMapping("/recommendations/collab")
    public Mono<String> getCollabRecommendations(@RequestParam Long playlistId) throws Exception {
        return collabFilteringRecommendationService.getCollabRecommendations(playlistId);
    }

    @PostMapping("/recommendations/manual")
    public Mono<RecommendationResponse.Response> getManualRecommendations(@RequestBody AudioFeatures audioFeatures, @RequestParam String genres) throws Exception {
        return manualRecommendationService.getManualRecommendations(audioFeatures, genres);
    }

    @GetMapping("/recommendations/getGenres")
    public Mono<String> getGenres() {
        return recommendationService.getGenres();
    }

}
