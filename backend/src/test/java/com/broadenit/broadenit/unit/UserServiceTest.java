package com.broadenit.broadenit.unit;

import com.broadenit.broadenit.registration.RegistrationRequest;
import com.broadenit.broadenit.user.User;
import com.broadenit.broadenit.user.UserRepository;
import com.broadenit.broadenit.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void RegisterUser() {

        RegistrationRequest request = new RegistrationRequest("123@gmail.com",
                "user", "password");
        User savedUser = new User();
        savedUser.setEmail("123@gmail.com");
        savedUser.setNickname("user");
        savedUser.setPassword("encodedPassword");
        savedUser.setCreatedAt(LocalDate.now());

        when(userRepository.findByEmail(request.email())).
                thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).
                thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).
                thenReturn(savedUser);

        User result = userService.registerUser(request);

        assertEquals("123@gmail.com", result.getEmail());
        assertEquals("user", result.getNickname());
        assertEquals("encodedPassword", result.getPassword());
        verify(userRepository, times(1)).
                save(any(User.class));
    }

}
