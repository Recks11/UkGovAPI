package dev.rexijie.ukgovapi;

import dev.rexijie.ukgovapi.config.ApplicationProperties;
import dev.rexijie.ukgovapi.config.SponsorProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({SponsorProperties.class, ApplicationProperties.class})
public class UkGovApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UkGovApiApplication.class, args);
    }
}
