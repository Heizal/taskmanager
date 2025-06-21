package com.example.taskmanager.service;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Service
public class OAuthTokenService {
    @Value("${GOOGLE_CLIENT_ID}")
    private String clientId;

    @Value("${GOOGLE_CLIENT_SECRET}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;

    public OAuthTokenService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Method to exchange authorization code for tokens
    public Map<String, String> exchangeCodeForTokens(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";
        Map<String, String> body = new HashMap<>();
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("redirect_uri", redirectUri);
        body.put("grant_type", "authorization_code");
        body.put("code", code);

        // Use the injected RestTemplate here
        Map<String, String> response = restTemplate.postForObject(tokenUrl, body, Map.class);

        // Return the access token, refresh token, and expires_in
        return response;
    }

    // Method to refresh the access token using refresh token
    public Map<String, String> refreshAccessToken(String refreshToken) {
        String tokenUrl = "https://oauth2.googleapis.com/token";
        Map<String, String> body = new HashMap<>();
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("grant_type", "refresh_token");
        body.put("refresh_token", refreshToken);

        // Use the injected RestTemplate here
        Map<String, String> response = restTemplate.postForObject(tokenUrl, body, Map.class);

        // Expected response: access_token, expires_in
        return response;
    }
}