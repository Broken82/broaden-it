package com.broadenit.broadenit.spotify;


import com.broadenit.broadenit.spotify.body.SpotifyPlaylistBody;
import com.broadenit.broadenit.spotify.playlists.PlaylistContent;
import com.broadenit.broadenit.spotify.playlists.PlaylistContentRepository;
import com.broadenit.broadenit.spotify.recommendations.RecommendationResponse;
import com.broadenit.broadenit.spotify.track.Track;
import com.broadenit.broadenit.spotify.track.TrackRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@RestController
@AllArgsConstructor
public class SpotifyController {

    private final SpotifyAuthService spotifyAuthService;
    private final WebClient.Builder webClientBuilder;
    private final TrackRepository trackRepository;




    @GetMapping("/trending")
    public Mono<String> getTrendingNow() {
        List<Track> popularTracks = trackRepository.findAllByOrderByPopularityDesc();

        List<RecommendationResponse.Track> popularTracksResponse = popularTracks.stream().limit(30)
                .map(track -> {
                    RecommendationResponse.Track popularTrack = new RecommendationResponse.Track();
                    popularTrack.setId(track.getSpotifyTrackId());
                    popularTrack.setName(track.getTitle());
                    popularTrack.setArtists(track.getArtist());
                    popularTrack.setPreview_url(track.getPreviewUrl());
                    popularTrack.setPopularity(track.getPopularity());
                    popularTrack.setPreview_url(track.getPreviewUrl());
                    popularTrack.setImage(track.getCoverArtUrl());

                    return popularTrack;
                })
                .collect(Collectors.toList());

        RecommendationResponse.Response response = new RecommendationResponse.Response();
        response.setTracks(popularTracksResponse);

        for(RecommendationResponse.Track track : response.getTracks()){
            System.out.println(track.getName());
        }


        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(response);
            return Mono.just(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.error(new RuntimeException(e));
        }




    }


    @GetMapping("/new")
    public Mono<RecommendationResponse.Response> getNewRelease() {

        List<Track> newTracks = trackRepository.findAllByOrderByReleaseDateDesc();

        List<RecommendationResponse.Track> newTracksResponse = newTracks.stream().limit(30)
                .map(track -> {
                    RecommendationResponse.Track newTrack = new RecommendationResponse.Track();
                    newTrack.setId(track.getSpotifyTrackId());
                    newTrack.setName(track.getTitle());
                    newTrack.setArtists(track.getArtist());
                    newTrack.setPreview_url(track.getPreviewUrl());
                    newTrack.setPopularity(track.getPopularity());
                    newTrack.setPreview_url(track.getPreviewUrl());
                    newTrack.setImage(track.getCoverArtUrl());

                    return newTrack;
                })
                .collect(Collectors.toList());


        RecommendationResponse.Response response = new RecommendationResponse.Response();
        response.setTracks(newTracksResponse);



        return Mono.just(response);



    }

    @PostMapping("/genres")
    public Mono<RecommendationResponse.Response> getGenrePlaylist(@RequestBody SpotifyPlaylistBody spotifyPlaylistBody) {

        List<Track> tracks = trackRepository.findAllByGenre(spotifyPlaylistBody.getGenreId());

        List<RecommendationResponse.Track> tracksResponse = tracks.stream()
                .map(track -> {
                    RecommendationResponse.Track newTrack = new RecommendationResponse.Track();
                    newTrack.setId(track.getSpotifyTrackId());
                    newTrack.setName(track.getTitle());
                    newTrack.setArtists(track.getArtist());
                    newTrack.setPreview_url(track.getPreviewUrl());
                    newTrack.setPopularity(track.getPopularity());
                    newTrack.setPreview_url(track.getPreviewUrl());
                    newTrack.setImage(track.getCoverArtUrl());

                    return newTrack;
                })
                .collect(Collectors.toList());

        RecommendationResponse.Response response = new RecommendationResponse.Response();
        response.setTracks(tracksResponse);

        return Mono.just(response);
    }

    @GetMapping("/search")
    public Mono<RecommendationResponse.Response> searchTrack(@RequestParam String searchQuery) {

        if(searchQuery.length() == 0){
            return null;
        }

        List<Track> queryTracks = trackRepository.findAllByArtistContainingIgnoreCaseOrTitleContainingIgnoreCase(searchQuery, searchQuery);

        for(Track track : queryTracks){
            System.out.println(track.getTitle());
        }

        List<RecommendationResponse.Track> queryTracksResponse = queryTracks.stream()
                .map(track -> {
                    RecommendationResponse.Track newTrack = new RecommendationResponse.Track();
                    newTrack.setId(track.getSpotifyTrackId());
                    newTrack.setName(track.getTitle());
                    newTrack.setArtists(track.getArtist());
                    newTrack.setPreview_url(track.getPreviewUrl());
                    newTrack.setPopularity(track.getPopularity());
                    newTrack.setPreview_url(track.getPreviewUrl());
                    newTrack.setImage(track.getCoverArtUrl());

                    return newTrack;
                })
                .collect(Collectors.toList());

        RecommendationResponse.Response response = new RecommendationResponse.Response();
        response.setTracks(queryTracksResponse);

        return Mono.just(response);

    }

    @GetMapping("/spotifySearch")
    public Mono<String> searchSpotifyTrack(@RequestParam String searchQuery) {
        String token = spotifyAuthService.getSpotifyToken();

        WebClient webClient = webClientBuilder
                .baseUrl("https://api.spotify.com/v1")
                .defaultHeader("Authorization", "Bearer " + token)
                .build();

        String searchResults = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", searchQuery)
                        .queryParam("type", "track")
                        .queryParam("limit", 30)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return Mono.just(searchResults);
    }

//    @GetMapping("/updateDate")
//    public Mono<String> updateDate() {
//        String token = spotifyAuthService.getSpotifyToken();
//
//        List<Track> tracks = trackRepository.findAll();
//
//        WebClient webClient = webClientBuilder
//                .baseUrl("https://api.spotify.com/v1")
//                .defaultHeader("Authorization", "Bearer " + token)
//                .build();
//
//        for (Track track : tracks) {
//            if (Objects.isNull(track.getReleaseDate())) {
//                String trackId = track.getSpotifyTrackId();
//                String releaseDate = webClient.get()
//                        .uri(uriBuilder -> uriBuilder
//                                .path("/tracks/{id}")
//                                .build(trackId))
//                        .retrieve()
//                        .bodyToMono(String.class)
//                        .block();
//
//                String date = releaseDate.split("\"release_date\":\"")[1].split("\"")[0];
//                if(Objects.equals(date, date.split("-")[0])){
//                    date = date + "-01-01";
//                }
//                if(Objects.equals(date, date.split("-")[0] + "-" + date.split("-")[1])){
//                    date = date + "-01";
//                }
//                track.setReleaseDate(LocalDate.parse(date));
//                trackRepository.save(track);
//
//
//            }
//        }
//
//
//        return Mono.just("Date updated");
//    }




}
