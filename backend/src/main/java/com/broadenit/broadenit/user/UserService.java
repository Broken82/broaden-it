package com.broadenit.broadenit.user;

import com.broadenit.broadenit.exception.UserAlreadyExistsException;
import com.broadenit.broadenit.registration.RegistrationRequest;
import com.broadenit.broadenit.registration.token.VerificationToken;
import com.broadenit.broadenit.registration.token.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final LocalDate date = LocalDate.now();
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User registerUser(RegistrationRequest request) {
        Optional<User> user = this.findByEmail(request.email());
        if(user.isPresent()){
            throw new UserAlreadyExistsException("User with email " + request.email() + " already exists");
        }
        User newUser = new User();
        newUser.setEmail(request.email());
        newUser.setNickname(request.nickname());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setCreatedAt(date);
        return userRepository.save(newUser);

    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUserToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateToken(String token1) {
        VerificationToken token = verificationTokenRepository.findByToken(token1)
                .orElseThrow(() -> new IllegalStateException("Token not found"));
        if(token == null){
            return "Token not found";
        }
        User user = token.getUser();
        Calendar calendar = Calendar.getInstance();
        if(token.getExpiryDate().before(calendar.getTime())){
            verificationTokenRepository.delete(token);
            return "Token expired";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "User verified";
    }
}
