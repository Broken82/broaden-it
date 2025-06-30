package com.broadenit.broadenit.spotify.recommendations;

import com.broadenit.broadenit.spotify.SpotifyAuthService;
import com.broadenit.broadenit.spotify.SpotifyController;
import com.broadenit.broadenit.spotify.playlists.PlaylistContent;
import com.broadenit.broadenit.spotify.playlists.PlaylistContentRepository;
import com.broadenit.broadenit.spotify.playlists.UserPlaylistRepository;
import com.broadenit.broadenit.spotify.playlists.UserPlaylistService;
import com.broadenit.broadenit.spotify.rating.Rating;
import com.broadenit.broadenit.spotify.rating.RatingRepository;
import com.broadenit.broadenit.spotify.track.Track;
import com.broadenit.broadenit.spotify.track.TrackRepository;
import com.broadenit.broadenit.user.User;
import com.broadenit.broadenit.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RecommendationService {

    public static final double minTempo = 40;
    public static final double maxTempo = 220;
    public static final double minKey = 0;
    public static final double maxKey = 11;
    public static final double minTimeSignature = 2;
    public static final double maxTimeSignature = 7;

    private final UserRepository userRepository;
    private final PlaylistContentRepository playlistContentRepository;
    private final TrackRepository trackRepository;
    private final RatingRepository ratingRepository;
    private final SpotifyController spotifyController;


    public Mono<String> getRecommendations(Long playlistId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return Mono.empty();
        }

        //lista wszystkich piosenek w playliście
        List<PlaylistContent> playlistContent = playlistContentRepository.findAllByPlaylistId(playlistId);
        List<Track> tracks = playlistContent.stream()
                .map(PlaylistContent::getTrack)
                .toList();

        if(tracks.isEmpty()) {
            return spotifyController.getTrendingNow();
        }

        System.out.println("Liczba piosenek w playliście: " + tracks.size());

//        FUNKCJA USUNIĘTA PRZEZ ZMIANĘ SPOTIFY API

//        if (mode.equals("Spotify")) {
//            List<CentroidCluster<DoublePoint>> clusters = clusterTracks(tracks, (int) Math.round(Math.sqrt(playlistContent.size())));
//
//            System.out.println("Liczba klastrów: " + clusters.size());
//
//
//            List<Track> representativeTracks = getRepresentativeTracksFromClusters(clusters, tracks);
//
//            System.out.println("Wybrane reprezentatywne piosenki:");
//            representativeTracks.forEach(track -> System.out.println(track.getTitle()));
//
//
//            List<Track> seedTracks = representativeTracks.stream()
//                    .limit(5)
//                    .toList();
//
//
//            System.out.println("Seed Tracks dla Spotify API:");
//            seedTracks.forEach(track -> System.out.println("Track ID: " + track.getSpotifyTrackId() + ", Name: " + track.getTitle()));
//
//            String token = spotifyAuthService.getSpotifyToken();
//            WebClient webClient = webClientBuilder
//                    .baseUrl("https://api.spotify.com/v1")
//                    .defaultHeader("Authorization", "Bearer " + token)
//                    .build();
//
//            Mono<String> mono = webClient.get()
//                    .uri(uriBuilder -> uriBuilder
//                            .path("/recommendations")
//                            .queryParam("limit", 30)
//                            .queryParam("seed_tracks", seedTracks.stream()
//                                    .map(Track::getSpotifyTrackId)
//                                    .collect(Collectors.joining(",")))
//                            .build())
//                    .retrieve()
//                    .bodyToMono(String.class);
//
//            return saveAndReturnRecommendation(webClient, mono, trackRepository);
//        }

        //oceny użytkownika dla piosenek w playliście
            User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
            List<Integer> ratings = tracks.stream()
                    .map(track -> {
                        Optional<Rating> ratingOptional = ratingRepository.findByUserIdAndTrackId(user, track);
                        return ratingOptional.map(Rating::getRating).orElse(3);
                    })
                    .toList();

            Track weightedAverageTrack = weightedAveragePlaylist(tracks, ratings);

            System.out.println("Utwór ważony:");
            System.out.println("Danceability: " + weightedAverageTrack.getDanceability());
            System.out.println("Energy: " + weightedAverageTrack.getEnergy());
            System.out.println("Speechiness: " + weightedAverageTrack.getSpeechiness());
            System.out.println("Acousticness: " + weightedAverageTrack.getAcousticness());
            System.out.println("Instrumentalness: " + weightedAverageTrack.getInstrumentalness());
            System.out.println("Liveness: " + weightedAverageTrack.getLiveness());
            System.out.println("Valence: " + weightedAverageTrack.getValence());
            System.out.println("Tempo: " + weightedAverageTrack.getTempo());
            System.out.println("Mode: " + weightedAverageTrack.getMode());
            System.out.println("Key: " + weightedAverageTrack.getKey());
            System.out.println("Time Signature: " + weightedAverageTrack.getTime_signature());

            //wektor cech utworu ważonego
            double[] weightedAverageFeatures = {
                    weightedAverageTrack.getDanceability(),
                    weightedAverageTrack.getEnergy(),
                    weightedAverageTrack.getSpeechiness(),
                    weightedAverageTrack.getAcousticness(),
                    weightedAverageTrack.getInstrumentalness(),
                    weightedAverageTrack.getLiveness(),
                    weightedAverageTrack.getValence(),
                    weightedAverageTrack.getTempo(),
                    weightedAverageTrack.getMode(),
                    weightedAverageTrack.getKey(),
                    weightedAverageTrack.getTime_signature()
            };

            List<Track> allTracks = trackRepository.findAll();




            //mapa podobieństwa utworów do utworu ważonego
            Map<Track, Double> similarityMap = allTracks.stream()
                    .filter(track -> playlistContent.stream()
                            .noneMatch(playlistTrack -> playlistTrack.getTrack().getSpotifyTrackId().equals(track.getSpotifyTrackId())))
                    .collect(Collectors.toMap(
                            track -> track,
                            track -> calculateCosineSimilarity(weightedAverageFeatures, new double[]{
                                    track.getDanceability(),
                                    track.getEnergy(),
                                    track.getSpeechiness(),
                                    track.getAcousticness(),
                                    track.getInstrumentalness(),
                                    track.getLiveness(),
                                    track.getValence(),
                                    (track.getTempo() - minTempo) / (maxTempo - minTempo),
                                    track.getMode(),
                                    (track.getKey() - minKey) / (maxKey - minKey),
                                    (track.getTime_signature() - minTimeSignature) / (maxTimeSignature - minTimeSignature)
                            })
                    ));

            List<Map.Entry<Track, Double>> sortedSimilarityMap = similarityMap.entrySet().stream()
                    .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
                    .limit(30)
                    .toList();





            sortedSimilarityMap.forEach(entry -> System.out.println("Track: " + entry.getKey().getTitle() + ", Similarity: " + entry.getValue()));


            //posortowane rekomendowane utwory
            List<Track> recommendedTracks = sortedSimilarityMap.stream()
                    .map(Map.Entry::getKey)
                    .toList();

            //podobieństwo rekomendowanych utworów
            List<Double> recommendedTracksSimilarity = sortedSimilarityMap.stream()
                    .map(Map.Entry::getValue)
                    .toList();

            return getStringMono(recommendedTracks, recommendedTracksSimilarity);



    }

    //funkcja zwracająca JSON z rekomendowanymi utworami
    public static Mono<String> getStringMono(List<Track> recommendedTracks, List<Double> recommendedTracksSimilarity) {
        List<RecommendationResponse.Track> recommendedTracksResponse = recommendedTracks.stream().map(track -> {
            RecommendationResponse.Track responseTrack = new RecommendationResponse.Track();
            responseTrack.setImage(track.getCoverArtUrl());
            responseTrack.setArtists(track.getArtist());
            responseTrack.setId(track.getSpotifyTrackId());
            responseTrack.setName(track.getTitle());
            responseTrack.setPreview_url(track.getPreviewUrl());
            responseTrack.setPopularity(track.getPopularity());
            responseTrack.setSimilarity(recommendedTracksSimilarity.get(recommendedTracks.indexOf(track)));
            return responseTrack;
        }).toList();

        RecommendationResponse.Response response = new RecommendationResponse.Response();
        response.setTracks(recommendedTracksResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse;

        try {
            jsonResponse = objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.error(new RuntimeException("Error creating JSON"));
        }

        return Mono.just(jsonResponse);
    }


    //FUNKCJA USUNIĘTA PRZEZ ZMIANĘ SPOTIFY API


//    public static Mono<String> saveAndReturnRecommendation(WebClient webClient, Mono<String> mono, TrackRepository trackRepository) throws Exception {
//        RecommendationResponse.Response response = RecommendationResponse.parseSpotifyResponse(mono.block());
//
//        List<String> trackIds = response.getTracks().stream()
//                .map(RecommendationResponse.Track::getId)
//                .collect(Collectors.toList());
//
//        Mono<AudioFeaturesResponse> audioFeaturesBatch = webClient.get()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/audio-features")
//                        .queryParam("ids", String.join(",", trackIds))
//                        .build())
//                .retrieve()
//                .bodyToMono(AudioFeaturesResponse.class);
//
//
//        AudioFeaturesResponse audioFeaturesResponse = audioFeaturesBatch.block();
//        List<AudioFeatures> audioFeaturesList = audioFeaturesResponse.getAudio_features();
//
//        for (int i = 0; i < trackIds.size(); i++) {
//            String trackId = trackIds.get(i);
//            Optional<Track> trackOptional = trackRepository.findBySpotifyTrackId(trackId);
//
//            if (trackOptional.isEmpty()) {
//                AudioFeatures audioFeatures = audioFeaturesList.get(i);
//                RecommendationResponse.Track spotifyTrack = response.getTracks().get(i);
//
//                Track newTrack = new Track();
//                newTrack.setSpotifyTrackId(trackId);
//                newTrack.setArtist(spotifyTrack.getArtists().stream().map(RecommendationResponse.Artist::getName).collect(Collectors.joining(", ")));
//                newTrack.setTitle(spotifyTrack.getName());
//                newTrack.setPreviewUrl(spotifyTrack.getPreview_url());
//                newTrack.setCoverArtUrl(spotifyTrack.getAlbum().getImages().get(0).getUrl());
//                newTrack.setDanceability(audioFeatures.getDanceability());
//                newTrack.setEnergy(audioFeatures.getEnergy());
//                newTrack.setSpeechiness(audioFeatures.getSpeechiness());
//                newTrack.setAcousticness(audioFeatures.getAcousticness());
//                newTrack.setInstrumentalness(audioFeatures.getInstrumentalness());
//                newTrack.setLiveness(audioFeatures.getLiveness());
//                newTrack.setValence(audioFeatures.getValence());
//                newTrack.setPopularity(spotifyTrack.getPopularity());
//                newTrack.setTempo(audioFeatures.getTempo());
//                newTrack.setMode(audioFeatures.getMode());
//                newTrack.setKey(audioFeatures.getKey());
//                newTrack.setTime_signature(audioFeatures.getTime_signature());
//                trackRepository.save(newTrack);
//            }
//        }
//
//
//        return mono;
//    }


    //funkcja zwracająca średnią ważoną cech piosenek w playliście
    public Track weightedAveragePlaylist(List<Track> tracks, List<Integer> ratings) {
        double totalWeight = 0.0;
        double danceability = 0.0;
        double energy = 0.0;
        double speechiness = 0.0;
        double acousticness = 0.0;
        double instrumentalness = 0.0;
        double liveness = 0.0;
        double valence = 0.0;
        double tempo = 0.0;
        double mode = 0.0;
        double key = 0.0;
        double timeSignature = 0.0;

        int defaultRating = 3;



        for (int i = 0; i < tracks.size(); i++) {
            int weight = i < ratings.size() ? ratings.get(i) : defaultRating;
            Track track = tracks.get(i);

            danceability += track.getDanceability() * weight;
            energy += track.getEnergy() * weight;
            speechiness += track.getSpeechiness() * weight;
            acousticness += track.getAcousticness() * weight;
            instrumentalness += track.getInstrumentalness() * weight;
            liveness += track.getLiveness() * weight;
            valence += track.getValence() * weight;
            //cechy są normalizowane do przedziału [0, 1]
            tempo += ((track.getTempo() - minTempo) / (maxTempo - minTempo)) * weight;
            mode += track.getMode() * weight;
            key += ((track.getKey() - minKey) / (maxKey - minKey)) * weight;
            timeSignature += ((track.getTime_signature() - minTimeSignature) / (maxTimeSignature - minTimeSignature)) * weight;
            totalWeight += weight;
        }


        danceability /= totalWeight;
        energy /= totalWeight;
        speechiness /= totalWeight;
        acousticness /= totalWeight;
        instrumentalness /= totalWeight;
        liveness /= totalWeight;
        valence /= totalWeight;
        tempo /= totalWeight;
        mode /= totalWeight;
        key /= totalWeight;
        timeSignature /= totalWeight;

        Track weightedAverageTrack = new Track();
        weightedAverageTrack.setDanceability(danceability);
        weightedAverageTrack.setEnergy(energy);
        weightedAverageTrack.setSpeechiness(speechiness);
        weightedAverageTrack.setAcousticness(acousticness);
        weightedAverageTrack.setInstrumentalness(instrumentalness);
        weightedAverageTrack.setLiveness(liveness);
        weightedAverageTrack.setValence(valence);
        weightedAverageTrack.setTempo(tempo);
        weightedAverageTrack.setMode(mode);
        weightedAverageTrack.setKey(key);
        weightedAverageTrack.setTime_signature(timeSignature);

        return weightedAverageTrack;
    }


    //funkcja obliczająca podobieństwo kosinusowe dwóch wektorów
    public double calculateCosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }

        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // FUNKCJA USUNIĘTA PRZEZ ZMIANĘ SPOTIFY API

//    public List<CentroidCluster<DoublePoint>> clusterTracks(List<Track> tracks, int numClusters) {
//        List<DoublePoint> points = new ArrayList<>();
//
//        numClusters = Math.max(numClusters, 1);
//
//
//
//        for (Track track : tracks) {
//            double[] features = getFeatures(minTempo, maxTempo, minKey, maxKey, minTimeSignature, maxTimeSignature, track);
//            points.add(new DoublePoint(features));
//        }
//
//        KMeansPlusPlusClusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<>(numClusters);
//        return clusterer.cluster(points);
//    }

    // FUNKCJA USUNIĘTA PRZEZ ZMIANĘ SPOTIFY API
//    public List<Track> getRepresentativeTracksFromClusters(List<CentroidCluster<DoublePoint>> clusters, List<Track> tracks) {
//        List<Track> representativeTracks = new ArrayList<>();
//
//
//
//        for (CentroidCluster<DoublePoint> cluster : clusters) {
//            DoublePoint center = (DoublePoint) cluster.getCenter();
//
//            Track closestTrack = null;
//            double maxSimilarity = -1;
//
//
//            for (Track track : tracks) {
//                double[] features = getFeatures(minTempo, maxTempo, minKey, maxKey, minTimeSignature, maxTimeSignature, track);
//                double similarity = calculateCosineSimilarity(center.getPoint(), features);
//
//                if (similarity > maxSimilarity) {
//                    maxSimilarity = similarity;
//                    closestTrack = track;
//                }
//            }
//
//
//            System.out.println("Klaster: Najbardziej podobna piosenka to " + closestTrack.getTitle() + " z podobieństwem kosinusowym " + maxSimilarity);
//            representativeTracks.add(closestTrack);
//        }
//
//        return representativeTracks;
//    }



    //FUNKCJA USUNIĘTA PRZEZ ZMIANĘ SPOTIFY API

//    private double[] getFeatures(double minTempo, double maxTempo, double minKey, double maxKey, double minTimeSignature, double maxTimeSignature, Track track) {
//
//
//        double[] features = {
//                track.getDanceability(),
//                track.getEnergy(),
//                track.getSpeechiness(),
//                track.getAcousticness(),
//                track.getInstrumentalness(),
//                track.getLiveness(),
//                track.getValence(),
//                track.getMode(),
//                (track.getTempo() - minTempo) / (maxTempo - minTempo),
//                (track.getKey() - minKey) / (maxKey - minKey),
//                (track.getTime_signature() - minTimeSignature) / (maxTimeSignature - minTimeSignature)
//        };
//        return features;
//    }


    //funkcja zwracająca unikalne gatunki muzyczne
    public Mono<String> getGenres() {


        List<String> genres = trackRepository.findAllUniqueGenres();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse;

        try {
            jsonResponse = objectMapper.writeValueAsString(genres);
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.error(new RuntimeException("Error creating JSON"));
        }

        return Mono.just(jsonResponse);
    }

}