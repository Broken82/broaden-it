package com.broadenit.broadenit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;


    @GetMapping("/user/profile")
    public Mono<UserProfile> getUserProfile() {
        return userProfileService.getUserProfile();
    }

    @PostMapping("/user/profile")
    public Mono<UserProfile> saveUserProfile(@RequestParam String url) {
        return userProfileService.saveUserProfile(url);
    }





}
