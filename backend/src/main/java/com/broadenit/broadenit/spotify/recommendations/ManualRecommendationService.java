package com.broadenit.broadenit.spotify.recommendations;
import com.broadenit.broadenit.spotify.body.AudioFeatures;
import com.broadenit.broadenit.spotify.track.Track;
import com.broadenit.broadenit.spotify.track.TrackRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@Service
@AllArgsConstructor
public class ManualRecommendationService {
    private final TrackRepository trackRepository;
    private final RecommendationService recommendationService;


    public Mono<RecommendationResponse.Response> getManualRecommendations(AudioFeatures audioFeatures, String genres) throws Exception {


        // wektor cech
        double[] features = {
                audioFeatures.getDanceability(),
                audioFeatures.getEnergy(),
                audioFeatures.getSpeechiness(),
                audioFeatures.getAcousticness(),
                audioFeatures.getInstrumentalness(),
                audioFeatures.getLiveness(),
                audioFeatures.getValence(),
                (audioFeatures.getTempo() - RecommendationService.minTempo) /
                        (RecommendationService.maxTempo - RecommendationService.minTempo),
                audioFeatures.getMode(),
                (audioFeatures.getTime_signature() - RecommendationService.minTimeSignature) /
                        (RecommendationService.maxTimeSignature - RecommendationService.minTimeSignature),
                (audioFeatures.getKey() - RecommendationService.minKey) /
                        (RecommendationService.maxKey - RecommendationService.minKey),
                (audioFeatures.getPopularity() / 100.0)
        };

        // pobranie utworów z bazy danych
        List<Track> tracksByGenre = trackRepository.findAllByGenre(genres);


        // obliczenie podobieństwa
        Map<Track, Double> similiarityMap = tracksByGenre.stream()
                .collect(Collectors.toMap(
                        track -> track,
                        track -> recommendationService.calculateCosineSimilarity(features, new double[]{
                                track.getDanceability(),
                                track.getEnergy(),
                                track.getSpeechiness(),
                                track.getAcousticness(),
                                track.getInstrumentalness(),
                                track.getLiveness(),
                                track.getValence(),
                                (track.getTempo() - RecommendationService.minTempo) /
                                        (RecommendationService.maxTempo - RecommendationService.minTempo),
                                track.getMode(),
                                (track.getTime_signature() - RecommendationService.minTimeSignature) /
                                        (RecommendationService.maxTimeSignature - RecommendationService.minTimeSignature),
                                (track.getKey() - RecommendationService.minKey) /
                                        (RecommendationService.maxKey - RecommendationService.minKey),
                                (track.getPopularity() / 100.0)
                        })
                ));
        // sortowanie
        List<Track> recommendedTracks = similiarityMap.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        // mapowanie na odpowiedź JSON
        List<RecommendationResponse.Track> recommendedTracksResponse = recommendedTracks.stream().limit(30)
                .map(track -> {
                    RecommendationResponse.Track recommendedTrack = new RecommendationResponse.Track();
                    recommendedTrack.setId(track.getSpotifyTrackId());
                    recommendedTrack.setName(track.getTitle());
                    recommendedTrack.setArtists(track.getArtist());
                    recommendedTrack.setPreview_url(track.getPreviewUrl());
                    recommendedTrack.setPopularity(track.getPopularity());
                    recommendedTrack.setPreview_url(track.getPreviewUrl());
                    recommendedTrack.setImage(track.getCoverArtUrl());
                    recommendedTrack.setSimilarity(similiarityMap.get(track));

                    return recommendedTrack;
                })
                .toList();

        for(RecommendationResponse.Track track : recommendedTracksResponse) {
            System.out.println("Manual recommendations similarity: " + track.getSimilarity());
        }

        // odpowiedź
        RecommendationResponse.Response response = new RecommendationResponse.Response();
        response.setTracks(recommendedTracksResponse);
        // zwrócenie odpowiedzi
        return Mono.just(response);







    }



}



