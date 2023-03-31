package pattni.sahil.spotify.rest_objects;

public record SavedTracks(
        String href,
        int limit,
        int total,
        String offset,
        SavedTrack[] items
) { }
