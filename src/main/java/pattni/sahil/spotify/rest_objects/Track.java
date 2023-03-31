package pattni.sahil.spotify.rest_objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Track(String id, String name, String uri, int duration_ms, Artist[] artists) { }
