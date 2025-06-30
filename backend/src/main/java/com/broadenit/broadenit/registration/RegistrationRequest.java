package com.broadenit.broadenit.registration;

public record RegistrationRequest(
        String nickname,
        String email,
        String password
) {
}
