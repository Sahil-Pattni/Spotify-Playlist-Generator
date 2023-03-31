package pattni.sahil.spotify;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpotifyApplication {
    @Bean
    public CommandLineRunner run() {
        return null;
    }
    public static void main(String[] args) {
        SpringApplication.run(SpotifyApplication.class, args);
    }

}
