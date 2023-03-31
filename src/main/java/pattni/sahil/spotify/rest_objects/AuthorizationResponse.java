package pattni.sahil.spotify.rest_objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthorizationResponse(String access_token, String token_type, String scope, int expires_in, String refresh_token) { }
