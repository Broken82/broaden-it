package com.broadenit.broadenit.spotify.playlists;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserPlaylistRepository extends JpaRepository<UserPlaylist, Long> {

        List<UserPlaylist> findAllByUserId(Long userId);
        void deleteById(Long id);
        Optional<UserPlaylist> findById(int id);

}
