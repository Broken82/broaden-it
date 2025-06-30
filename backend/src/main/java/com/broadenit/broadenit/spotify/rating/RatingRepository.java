package com.broadenit.broadenit.spotify.rating;

import com.broadenit.broadenit.spotify.track.Track;
import com.broadenit.broadenit.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByUserIdAndTrackId(User user, Track track);

    int countByUserId(User user);

    @Query("SELECT SUM(r.rating) FROM Rating r WHERE r.userId = :user")
    int sumRatingByUserId(@Param("user") User user);

    @Query("SELECT r.trackId FROM Rating r WHERE r.userId = :user ORDER BY r.rating DESC")
    List<Track> findTop5TracksByUserId(@Param("user") User user);
}
