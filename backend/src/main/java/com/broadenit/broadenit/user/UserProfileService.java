package com.broadenit.broadenit.user;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("User not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserProfile getUserProfileForUser(User user) {
        return userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile newUserProfile = new UserProfile();
                    newUserProfile.setUser(user);
                    newUserProfile.setUrl("https://raw.githubusercontent.com/mantinedev/mantine/master/.demo/images/bg-8.png");
                    return userProfileRepository.save(newUserProfile);
                });
    }

    public Mono<UserProfile> getUserProfile() {
        User user = getCurrentUser();
        UserProfile userProfile = getUserProfileForUser(user);
        return Mono.just(userProfile);
    }

    public Mono<UserProfile> saveUserProfile(String url) {
        User user = getCurrentUser();
        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseGet(UserProfile::new);
        userProfile.setUser(user);
        userProfile.setUrl(url);
        userProfileRepository.save(userProfile);
        return Mono.just(userProfile);
    }
}
