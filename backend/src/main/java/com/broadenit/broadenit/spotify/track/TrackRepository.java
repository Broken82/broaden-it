package com.broadenit.broadenit.spotify.track;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, Long> {
    Optional<Track> findBySpotifyTrackId(String spotifyId);
    List<Track> findAllBySpotifyTrackId(String spotifyId);

    List<Track> findAllByOrderByReleaseDateDesc();

    List<Track> findAllByOrderByPopularityDesc();

    List<Track> findAllByGenre(String genreId);


    List<Track> findAllByArtistContainingIgnoreCaseOrTitleContainingIgnoreCase(String searchQuery, String searchQuery1);

    @Query("SELECT DISTINCT t.genre FROM Track t")
    List<String> findAllUniqueGenres();



}
