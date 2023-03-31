package pattni.sahil.spotify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import pattni.sahil.spotify.rest_objects.AuthorizationResponse;
import pattni.sahil.spotify.security.TokenVault;


import java.util.Base64;

@Controller
public class AuthenticationController {

    @Value("${spring.security.oauth2.client.registration.spotify.client-id}")
    private String CLIENT_ID;
    @Value("${spring.security.oauth2.client.registration.spotify.client-secret}")
    private String CLIENT_SECRET;
    @Value("${spring.security.oauth2.client.provider.spotify.token-uri}")
    private String TOKEN_URI;

    @GetMapping("/test")
    public String test() {
        System.out.printf("CLIENT_ID: %s%n", CLIENT_ID);
        System.out.printf("CLIENT_SECRET: %s%n", CLIENT_SECRET);
        System.out.printf("TOKEN_URI: %s%n", TOKEN_URI);

        return "redirect:/authorized.html";
    }

    @GetMapping("/callback")
    public String exchangeCodeForToken(
            @RequestParam(name = "code") String code
    ) {
        System.out.printf("---------%nCode: %s%n-----------", code);
        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", authorizationHeader());

        // Request Parameters
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("redirect_uri", "http://localhost:8080/callback");

        // Request Entity
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        // Make request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<AuthorizationResponse> responseEntity = restTemplate.exchange(
                TOKEN_URI,
                HttpMethod.POST,
                requestEntity,
                AuthorizationResponse.class
        );

        // Set access token
        AuthorizationResponse auth = responseEntity.getBody();
        assert auth != null;
        TokenVault.setAccessToken(auth.access_token());
        TokenVault.setRefreshToken(auth.refresh_token());

        return "redirect:/authorized.html";
    }

    private String authorizationHeader() {
        Base64.Encoder encoder = Base64.getEncoder();
        String toEncode = String.format("%s:%s", CLIENT_ID, CLIENT_SECRET);
        return String.format("Basic %s", encoder.encodeToString(toEncode.getBytes()));
    }
}
