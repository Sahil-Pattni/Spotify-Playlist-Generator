package pattni.sahil.spotify.rest_objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Artist(String name, String id) { }
