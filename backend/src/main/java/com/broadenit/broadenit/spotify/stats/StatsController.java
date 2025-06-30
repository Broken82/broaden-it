package com.broadenit.broadenit.spotify.stats;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
public class StatsController {
    private final StatsService statsService;

    @GetMapping("user/profile/stats")
    public Mono<Stats> getUserProfileStats() {
        return statsService.getUserProfileStats();
    }
}
