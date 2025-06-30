package com.broadenit.broadenit.event;

import com.broadenit.broadenit.user.User;
import com.broadenit.broadenit.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final UserService userService;
    private final JavaMailSender emailSender;



    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {

        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveUserToken(user, token);
        String url = event.getAppUrl() + "/register/verify?token=" + token;
        try {
            sendVerificationEmail(url, user);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Error sending email", e);
        }

    }

    public void sendVerificationEmail(String url, User user) throws MessagingException, UnsupportedEncodingException {
        String subject = "Please verify your email";
        String senderName = "Broaden It";
        String messageContent = "Click the link below to verify your email: \n" + url;

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("ngrcompany21@gmail.com",senderName);
        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        helper.setText(messageContent);
        emailSender.send(message);



    }
}
