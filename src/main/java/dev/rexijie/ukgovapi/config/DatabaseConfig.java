package dev.rexijie.ukgovapi.config;

import dev.rexijie.ukgovapi.service.SponsorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class DatabaseConfig {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseConfig.class);

    final SponsorService sponsorService;

    public DatabaseConfig(SponsorService sponsorService) {
        this.sponsorService = sponsorService;
    }

    @Scheduled(fixedRateString = "PT1H", initialDelayString = "PT10S")
    void fetchSponsorList() {
        sponsorService.updateSponsorList()
                .doOnSuccess(integer -> LOG.info("fetched new sponsor list with %s items".formatted(integer)))
                .subscribe();
    }
}
