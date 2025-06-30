package com.broadenit.broadenit.spotify.recommendations;


import com.broadenit.broadenit.spotify.playlists.*;
import com.broadenit.broadenit.spotify.rating.Rating;
import com.broadenit.broadenit.spotify.rating.RatingRepository;
import com.broadenit.broadenit.spotify.track.Track;

import com.broadenit.broadenit.user.User;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

import static com.broadenit.broadenit.spotify.recommendations.RecommendationService.getStringMono;

@Service
@AllArgsConstructor
public class CollabFilteringRecommendationService {
    private final UserPlaylistRepository userPlaylistRepository;
    private final PlaylistContentRepository playlistContentRepository;
    private final RatingRepository ratingRepository;
    private final RecommendationService recommendationService;

    public Mono<String> getCollabRecommendations(Long playlistId) {


        //playlista usera i user
        UserPlaylist userPlaylist = userPlaylistRepository.findById(playlistId).orElseThrow();
        User playlistOwner = userPlaylist.getUser();

        //lista piosenek z playlisty usera i lista wszystkich piosenek na wszystkich playlistach
        List<PlaylistContent> playlistContents = playlistContentRepository.findAllByPlaylistId(playlistId);
        List<PlaylistContent> allPlaylistContents = playlistContentRepository.findAll();
        List<Track> tracks = playlistContents.stream().map(PlaylistContent::getTrack).toList();

        //oceny usera dla piosenek z playlisty
        List<Rating> ratings = ratingRepository.findAll();
        List<Rating> userRatings = ratings.stream()
                .filter(rating -> rating.getUserId().equals(playlistOwner) &&
                        tracks.stream().anyMatch(track -> track.getId().equals(rating.getTrackId().getId())))
                .toList();

        System.out.println("User ratings for the given playlist:");
        userRatings.forEach(rating -> {
            System.out.printf("User ID: %d, Track ID: %d, Rating: %d%n", rating.getUserId().getId(), rating.getTrackId().getId(), rating.getRating());
        });

        // grupowanie zawartosci playlist według użytkowników i ich playlist
        Map<User, Map<UserPlaylist, List<PlaylistContent>>> userPlaylistsContents = allPlaylistContents.stream()
                .collect(Collectors.groupingBy(
                        playlistContent -> playlistContent.getPlaylist().getUser(),
                        Collectors.groupingBy(PlaylistContent::getPlaylist)
                ));

        // obliczanie podobieństwa między playlistami
        Map<User, Map<UserPlaylist, Double>> userPlaylistSimilarities = userPlaylistsContents.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(playlistOwner))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().entrySet().stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        playlistEntry -> {
                                            List<PlaylistContent> otherPlaylistContents = playlistEntry.getValue();

                                            List<Track> commonTracks = tracks.stream()
                                                    .filter(track -> otherPlaylistContents.stream()
                                                            .anyMatch(pc -> pc.getTrack().getId().equals(track.getId())))
                                                    .toList();
                                            if (commonTracks.isEmpty()) {
                                                return 0.0;
                                            }

                                            double[] ownerRatingsVector = commonTracks.stream()
                                                    .mapToDouble(track -> userRatings.stream()
                                                            .filter(r -> r.getTrackId().getId().equals(track.getId()))
                                                            .mapToDouble(Rating::getRating)
                                                            .findFirst()
                                                            .orElse(0.0))
                                                    .toArray();

                                            double[] otherUserRatingsVector = commonTracks.stream()
                                                    .mapToDouble(track -> ratings.stream()
                                                            .filter(r -> r.getUserId().equals(entry.getKey()) && r.getTrackId().getId().equals(track.getId()))
                                                            .mapToDouble(Rating::getRating)
                                                            .findFirst()
                                                            .orElse(0.0))
                                                    .toArray();

                                            return recommendationService.calculateCosineSimilarity(ownerRatingsVector, otherUserRatingsVector);
                                        }
                                ))
                ));


        userPlaylistSimilarities.forEach((user, playlistMap) -> {
            System.out.printf("User ID: %d\n", user.getId());
            playlistMap.forEach((playlist, similarity) -> {
                System.out.printf("  Playlist ID: %d, Similarity: %.4f\n", playlist.getId(), similarity);
            });
        });



        // top 3 podobne playlisty
        List<Map.Entry<UserPlaylist, Double>> top3SimilarPlaylists = userPlaylistSimilarities.values().stream()
                .flatMap(playlistMap -> playlistMap.entrySet().stream())
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(3)
                .filter(v -> v.getValue() > 0)
                .toList();


        top3SimilarPlaylists.forEach(entry -> {
            System.out.printf("Playlist ID: %d, Similarity: %.4f%n", entry.getKey().getId(), entry.getValue());
        });


        // predykcja ocen dla piosenek z top 3 podobnych playlist
        Map<Track, Double> predictedRatings = predictRatingsForTracks(top3SimilarPlaylists, ratings, tracks);



        // top 30 rekomendowanych piosenek
        List<Track> recommendedTracks = predictedRatings.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(30)
                .map(Map.Entry::getKey)
                .toList();

        // oceny dla rekomendowanych piosenek
        List<Double> recommendedTracksRatings = recommendedTracks.stream()
                .map(predictedRatings::get)
                .toList();

        // zwracanie rekomendacji w formacie JSON
        return getStringMono(recommendedTracks, recommendedTracksRatings);


    }



    // predykcja ocen dla piosenek z top 3 podobnych playlist
    public Map<Track, Double> predictRatingsForTracks(List<Map.Entry<UserPlaylist, Double>> top3SimilarPlaylists,
                                                      List<Rating> ratings, List<Track> userTracks) {
        Map<Track, double[]> trackWeights = new HashMap<>();

        System.out.println("Starting prediction process...");

        for (Map.Entry<UserPlaylist, Double> similarPlaylistEntry : top3SimilarPlaylists) {
            UserPlaylist similarPlaylist = similarPlaylistEntry.getKey();
            double similarity = similarPlaylistEntry.getValue();

            System.out.printf("Processing similar playlist with ID: %d, Similarity: %.4f%n",
                    similarPlaylist.getId(), similarity);

            List<Track> similarPlaylistTracks = playlistContentRepository
                    .findAllByPlaylistId(similarPlaylist.getId())
                    .stream()
                    .map(PlaylistContent::getTrack)
                    .filter(track -> !userTracks.contains(track))
                    .toList();

            for (Track track : similarPlaylistTracks) {
                System.out.printf("Predicting rating for track ID: %d%n", track.getId());

                Rating rating = ratings.stream()
                        .filter(r -> r.getTrackId().getId().equals(track.getId()) &&
                                r.getUserId().equals(similarPlaylist.getUser()))
                        .findFirst()
                        .orElse(null);

                if (rating != null) {
                    double userRating = rating.getRating();
                    System.out.printf("Similar user rated track ID:" +
                                    " %d with rating: %.2f, similarity: %.4f%n",
                            track.getId(), userRating, similarity);

                    double[] weights = trackWeights.getOrDefault(track, new double[]{0.0, 0.0});
                    weights[0] += userRating * similarity;
                    weights[1] += similarity;
                    trackWeights.put(track, weights);
                } else {
                    System.out.printf("No rating found for track ID:" +
                                    " %d from similar user.%n",
                            track.getId());
                }
            }
        }

        System.out.println("Calculating final predicted ratings...");
        Map<Track, Double> predictedRatings = new HashMap<>();
        for (Map.Entry<Track, double[]> entry : trackWeights.entrySet()) {
            Track track = entry.getKey();
            double[] weights = entry.getValue();
            double weightedSum = weights[0];
            double totalWeight = weights[1];

            double predictedRating = totalWeight == 0.0 ? 0.0 : (weightedSum / totalWeight);

            System.out.printf("Predicted rating for track ID: %d is: %.4f%n",
                    track.getId(), predictedRating);

            predictedRatings.put(track, predictedRating);
        }

        System.out.println("Prediction process finished.");
        return predictedRatings;
    }

}
