package com.broadenit.broadenit.user;

import com.broadenit.broadenit.registration.RegistrationRequest;
import com.broadenit.broadenit.registration.token.VerificationToken;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    List<User> getUsers();
    User registerUser(RegistrationRequest request);
    Optional<User> findByEmail(String email);

    void saveUserToken(User user, String token);

    String validateToken(String token1);
}
