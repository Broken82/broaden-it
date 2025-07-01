package com.broadenit.broadenit.spotify;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class SpotifyAuthService {

    private final WebClient webClient;
    private String token;
    private long tokenExpirationTime;


    private static final String CLIENT_ID = "";
    private static final String CLIENT_SECRET = "";

    public SpotifyAuthService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://accounts.spotify.com/api").build();
    }


    public String getSpotifyToken() {
        if(token == null || System.currentTimeMillis() > tokenExpirationTime){
            fetchSpotifyToken();
        }
        System.out.println("TokenGET2: " + token);
        return token;

    }

    private void fetchSpotifyToken(){
        String authResponse = webClient.post()
                .uri("/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("grant_type=client_credentials&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        this.token = extractTokenFromResponse(authResponse);
        System.out.println("TokenFetch: " + token);
        this.tokenExpirationTime = System.currentTimeMillis() + 3600000;
    }

    private String extractTokenFromResponse(String response) {

        return response.split("\"access_token\":\"")[1].split("\"")[0];
    }
}
