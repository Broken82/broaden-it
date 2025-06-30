package com.broadenit.broadenit.spotify.recommendations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class RecommendationResponse {

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Image {
        private String url;
        private int height;
        private int width;

    }





    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Track {
        private String image;
        private String artists;
        private String id;
        private String name;
        private String preview_url;
        private int popularity;
        private double similarity;
    }


    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private List<Track> tracks;
    }

    public static Response parseSpotifyResponse(String jsonResponse) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonResponse, Response.class);
    }
}
