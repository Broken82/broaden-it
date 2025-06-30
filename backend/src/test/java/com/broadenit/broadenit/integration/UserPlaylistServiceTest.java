package com.broadenit.broadenit.integration;

import com.broadenit.broadenit.security.JwtProvider;
import com.broadenit.broadenit.spotify.playlists.UserPlaylist;
import com.broadenit.broadenit.spotify.playlists.UserPlaylistRepository;
import com.broadenit.broadenit.user.User;
import com.broadenit.broadenit.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class UserPlaylistServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPlaylistRepository userPlaylistRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    void IntegrationCreatePlaylist() throws Exception {

        User user = new User();
        user.setEmail("123@gmail");
        user.setPassword("$2a$10$wD3y8IUjKPfU9lWzS.rH0ezI0E3pITWrUMOBB5q.YtG4.B4g/zPJi");
        userRepository.save(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "123@gmail.com", null);
        String token = jwtProvider.generateToken(authentication);

        mockMvc.perform(post("/playlists/create")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("name", "Playlista"))
                .andExpect(status().isOk());

        List<UserPlaylist> playlists = userPlaylistRepository.findAll();
        assertEquals(16, playlists.size());
        assertTrue(playlists.stream().anyMatch(p -> p.getName().equals("Playlista")));

    }
}