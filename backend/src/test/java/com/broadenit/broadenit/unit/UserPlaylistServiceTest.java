package com.broadenit.broadenit.unit;


import com.broadenit.broadenit.spotify.playlists.UserPlaylist;
import com.broadenit.broadenit.spotify.playlists.UserPlaylistRepository;
import com.broadenit.broadenit.spotify.playlists.UserPlaylistService;
import com.broadenit.broadenit.user.User;
import com.broadenit.broadenit.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPlaylistServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPlaylistRepository userPlaylistRepository;

    @InjectMocks
    private UserPlaylistService userPlaylistService;

    @Test
    void CreatePlaylist() {

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("123@gmail.com");
        SecurityContextHolder.setContext(securityContext);

        User user = new User();
        user.setEmail("123@gmail.com");
        when(userRepository.findByEmail("123@gmail.com")).thenReturn(Optional.of(user));


        ResponseEntity<Void> response = userPlaylistService.createPlaylist("Pop");


        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userPlaylistRepository, times(1)).save(any(UserPlaylist.class));
    }
}
