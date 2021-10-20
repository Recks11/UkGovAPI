package dev.rexijie.ukgovapi.config;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParserConfig {
    @Value("${jsoup.conn.user-agent}")
    private String USER_AGENT;
    @Autowired
    private SponsorProperties sponsorProperties;

    @Bean
    Connection parser() {
        return Jsoup.connect(sponsorProperties.getSponsorListPage())
                .userAgent(USER_AGENT);
    }
}
