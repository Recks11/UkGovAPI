package dev.rexijie.ukgovapi;

import dev.rexijie.ukgovapi.batch.DocumentDownloader;
import dev.rexijie.ukgovapi.config.SponsorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import reactor.util.Logger;
import reactor.util.Loggers;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackageClasses = SponsorProperties.class)
public class UkGovApiApplication implements CommandLineRunner {
    private static final Logger LOG = Loggers.getLogger(UkGovApiApplication.class);
    @Autowired
    DocumentDownloader documentDownloader;

    public static void main(String[] args) {
        SpringApplication.run(UkGovApiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        documentDownloader.downloadSponsorList();
    }
}
