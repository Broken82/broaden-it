package com.broadenit.broadenit.spotify.playlists;

import com.broadenit.broadenit.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class UserPlaylist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate createdAt;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int numberOfTracks;


}