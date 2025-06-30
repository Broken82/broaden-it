package com.broadenit.broadenit.registration;


import com.broadenit.broadenit.event.RegistrationCompleteEvent;
import com.broadenit.broadenit.registration.token.VerificationToken;
import com.broadenit.broadenit.registration.token.VerificationTokenRepository;
import com.broadenit.broadenit.user.User;
import com.broadenit.broadenit.user.UserService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenRepository verificationTokenRepository;

    @PostMapping
    public String register(@RequestBody RegistrationRequest request, final HttpServletRequest httpServlet) {
        User user = userService.registerUser(request);
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(httpServlet)));
        return "User with email " + user.getEmail() + " has been registered";

    }

    @GetMapping("/verify")
    public String verify(@RequestParam("token") String token) {
        VerificationToken token1 = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found"));

        if (token1.getUser().isEnabled()) {
            return "User already verified";
        }
        String verificationResult = userService.validateToken(token);

        if (verificationResult.equals("User verified")) {
            return "User verified";
        } else {
            return "Token not valid";
        }

    }

    private String applicationUrl(HttpServletRequest httpServlet) {
        return "http://" + httpServlet.getServerName() + ":" + httpServlet.getServerPort() + httpServlet.getContextPath();
    }
}
