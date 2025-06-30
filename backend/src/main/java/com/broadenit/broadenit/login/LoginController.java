package com.broadenit.broadenit.login;


import com.broadenit.broadenit.response.AuthResponse;
import com.broadenit.broadenit.security.JwtProvider;
import com.broadenit.broadenit.security.UserRegistrationDetailsService;
import com.broadenit.broadenit.user.User;
import com.broadenit.broadenit.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final UserService userService;
    private final UserRegistrationDetailsService userRegistrationDetailsService;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody User loginRequest){
        Authentication authentication = authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = JwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setMessage("Login successful");
        authResponse.setJwt(token);
        authResponse.setStatus(true);

        return new ResponseEntity<>(authResponse, HttpStatus.OK);

    }

    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = userRegistrationDetailsService.loadUserByUsername(email);

        if(userDetails == null){
            throw new UsernameNotFoundException("User not found");
        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new BadCredentialsException("Invalid credentials");
        }

        if(!userDetails.isEnabled()){
            throw new BadCredentialsException("User not verified");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());


    }
}
