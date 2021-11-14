package dev.rexijie.ukgovapi.config;

import dev.rexijie.ukgovapi.batch.DocumentDownloader;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParserConfig {
    private static final Logger LOG = LoggerFactory.getLogger(ParserConfig.class);
    @Value("${jsoup.conn.user-agent}")
    private String USER_AGENT;
    private final SponsorProperties sponsorProperties;

    public ParserConfig(SponsorProperties sponsorProperties) {
        this.sponsorProperties = sponsorProperties;
    }

    @Bean
    Connection parser() {
        var url = sponsorProperties.getSponsorListPage();
        LOG.info("connecting to url: %s".formatted(url));
        return Jsoup.connect(url)
                .userAgent(USER_AGENT);
    }

    @Bean
    DocumentDownloader documentDownloader() {
        return new DocumentDownloader(parser(), sponsorProperties);
    }
}
