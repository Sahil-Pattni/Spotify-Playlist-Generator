package pattni.sahil.spotify;

import org.apache.coyote.Response;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pattni.sahil.spotify.rest_objects.SavedTrack;
import pattni.sahil.spotify.rest_objects.SavedTracks;
import pattni.sahil.spotify.rest_objects.TopTracks;
import pattni.sahil.spotify.rest_objects.Track;
import pattni.sahil.spotify.security.TokenVault;

import java.util.ArrayList;
import java.util.List;

public class SpotifyBot {
    private static final String spotifyApiUrl = "https://api.spotify.com/v1";


    static List<Track> createPlaylist(int length_ms, int tolerance_ms, int numTracksConsidered) {
        /*
         * Create a playlist of recommended tracks.
         *
         * @param length_ms: The desired length of the playlist in milliseconds.
         * @param tolerance_ms: The tolerance for the playlist length in milliseconds.
         * @param numTracksConsidered: The number of tracks to consider when creating the playlist.
         *
         * @return: A playlist of recommended tracks of length length_ms +/- tolerance_ms.
         */

        Track[] topTracks = collectTopTracks(numTracksConsidered);
        List<Track> playlist = new ArrayList<>();
        int duration = 0;

        for (Track track : topTracks) {
            if (duration + track.duration_ms() <= length_ms + tolerance_ms) {
                playlist.add(track);
                duration += track.duration_ms();
            }
        }

        return playlist;
    }

    private static Track[] collectTopTracks(int limit) {
        /*
         * Collect a user's top tracks.
         *
         * @param limit: The maximum number of tracks to return. Minimum: 1.
         *
         * @return: A list of the user's top tracks.
         */

        Track[] topTracks = new Track[limit];

        int offset = 0;

        while (limit > 0) {
            int limitToGet = Math.min(limit, 50);
            TopTracks newTopTracks = getTopTracks(limitToGet, offset);
            assert newTopTracks != null;
            // Response limit may differ from provided limit
            int responseLimit = newTopTracks.limit();
            System.arraycopy(newTopTracks.items(), 0, topTracks, offset, responseLimit);

            offset += responseLimit;
            limit -= responseLimit;
        }

        return topTracks;
    }



    private static SavedTrack[] collectSavedTracks(int limit) {
        /*
         * Collect a user's saved tracks.
         *
         * @param limit: The maximum number of tracks to return. Minimum: 1.
         */

        SavedTrack[] savedTracks = new SavedTrack[limit];
        int offset = 0;
        while (limit > 0) {
            int limitToGet = Math.min(limit, 50);
            SavedTracks newSavedTracks = getSavedTracks(limitToGet, offset);
            assert newSavedTracks != null;

            System.arraycopy(newSavedTracks.items(), 0, savedTracks, offset, limitToGet);

            offset += limitToGet;
            limit -= limitToGet;
        }

        // TODO: Implement
        return savedTracks;
    }

    // --- API Requests --- //

    private static SavedTracks getSavedTracks(int limit, int offset) {
        /*
         * Get a user's saved tracks.
         *
         * @param limit: The maximum number of tracks to return. Default: 20. Minimum: 1. Maximum: 50.
         */
        // Request parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("limit", String.valueOf(limit));
        params.add("offset", String.valueOf(offset));
        params.add("market", "CA");

        // Make request
        return makeRequest("/me/tracks", params, HttpMethod.GET, SavedTracks.class);
    }

    public static TopTracks getTopTracks(int limit, int offset) {
        /*
         * Get a user's top tracks.
         *
         * @param limit: The maximum number of tracks to return. Default: 20. Minimum: 1. Maximum: 50.
         * @param offset: The index of the first track to return. Default: 0 (the first object).
         *                Use with limit to get the next set of tracks.
         */

        // Request parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("limit", String.valueOf(limit));
        params.add("offset", String.valueOf(offset));
        params.add("time_range", "long_term");

        // Make request
        return makeRequest("/me/top/tracks", params, HttpMethod.GET, TopTracks.class);
    }

    // --- HELPER METHODS --- //
    private static HttpHeaders generateHeaders() {
        /*
         * Generate the headers for the request.
         */
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", generateAuthorizationHeader());
        return headers;
    }

    private static String generateAuthorizationHeader() {
        /*
         * Generate the authorization header for the request.
         */
        return "Bearer " + TokenVault.getAccessToken();
    }

    private static String buildRoute(String route) {
        /*
         * Build the route for the request.
         * @param route: The route to append to the base Spotify API URL.
         */
        return spotifyApiUrl + route;
    }

    // Generic request method
    private static <T> T makeRequest(String route, MultiValueMap<String, String> params,  HttpMethod method, Class<T> responseType) {
        /*
         * Make a request to the Spotify API.
         *
         * @param route: The route to append to the base Spotify API URL.
         * @param params: The parameters to send with the request.
         * @param method: The HTTP method to use.
         * @param responseType: The type of the response.
         *
         * @return: The response body.
         */
        // Set up the headers
        HttpHeaders headers = generateHeaders();
        // Request entity
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        // Make request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> responseEntity = restTemplate.exchange(
                buildRoute(route),
                method,
                requestEntity,
                responseType
        );

        // Return the response
        return responseEntity.getBody();
    }
}
