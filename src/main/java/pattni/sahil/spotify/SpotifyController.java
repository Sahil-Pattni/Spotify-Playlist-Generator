package pattni.sahil.spotify;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pattni.sahil.spotify.rest_objects.Artist;
import pattni.sahil.spotify.rest_objects.Track;

import java.util.List;

@Controller
@RequestMapping("/spotify")
public class SpotifyController {
    /*
     * Controller to interact with the Spotify API.
     */

    @GetMapping("/playlistRecommendations")
    public String generatePlaylistRecommendations(
            @RequestParam(name = "length_ms", defaultValue = "900000") int length_ms,
            @RequestParam(name = "tolerance_ms", defaultValue = "1000") int tolerance_ms,
            @RequestParam(name = "num_tracks", defaultValue = "100") int numTracks
    ) {
        /*
         * Generate a playlist of recommended tracks.
         *
         * @param length_ms: The desired length of the playlist in milliseconds. Default is 15 minutes.
         * @param tolerance_ms: The tolerance for the playlist length in milliseconds. Default is 1 second.
         * @param num_tracks: The number of tracks to recommend. Default is 100.
         *
         * @return: A playlist of recommended tracks of length length_ms +/- tolerance_ms.
         */
        List<Track> playlist = SpotifyBot.createPlaylist(length_ms, tolerance_ms, numTracks);
        double playlistLength = 0;
        for (Track track : playlist) {
            Artist artist = track.artists()[0];
            double duration = track.duration_ms() / 60000.0;
            playlistLength += track.duration_ms();
            System.out.printf("%s - %s [%.2f] m.%n", track.name(), artist.name(), duration);
        }
        System.out.printf("Playlist length: %.2f minute(s).%n", playlistLength / 60000.0);

        return "redirect:/authorized.html";
    }
}
