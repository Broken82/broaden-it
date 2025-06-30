package com.broadenit.broadenit.registration.token;


import com.broadenit.broadenit.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private Date expiryDate;
    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;


    public VerificationToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expiryDate = getTokenExpiryDate();
    }

    public Date getTokenExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, 15);
        return new Date(calendar.getTime().getTime());
    }
}
