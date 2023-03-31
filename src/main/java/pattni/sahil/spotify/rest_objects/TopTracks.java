package pattni.sahil.spotify.rest_objects;

public record TopTracks(
        String href,
        int limit,
        int total,
        String offset,
        Track[] items
) {
}
