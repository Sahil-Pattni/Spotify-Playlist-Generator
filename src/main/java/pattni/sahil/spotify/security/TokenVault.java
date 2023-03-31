package pattni.sahil.spotify.security;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TokenVault {
    /*
     * This class is used to store the access and refresh tokens for the Spotify API.
     */
    private static String accessToken;
    private static String refreshToken;
    private static final String filepath = "src/main/resources/vault/tokens.vault";

    public static void setAccessToken(String accessToken) {
        TokenVault.accessToken = accessToken;
        saveTokens();
    }

    public static void setRefreshToken(String refreshToken) {
        TokenVault.refreshToken = refreshToken;
        saveTokens();
    }

    public static String getRefreshToken() {
        if (refreshToken == null) {
            readTokens();
        }
        return refreshToken;
    }

    public static String getAccessToken() {
        if (accessToken == null) {
            readTokens();
        }
        return accessToken;
    }

    private static void saveTokens() {
        // TODO: Encrypt tokens / implement refresh strategy
        // Write out tokens to file
        try {
            FileWriter writer = new FileWriter(filepath);
            writer.write(accessToken + ":" + refreshToken);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readTokens() {
        // Read tokens from file
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                System.out.println("No tokens file found.");
                return;
            }
            Scanner reader = new Scanner(file);
            String[] tokens = reader.nextLine().split(":");
            accessToken = tokens[0].strip();
            refreshToken = tokens[1].strip();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasTokens() {
        return accessToken != null && refreshToken != null;
    }
}