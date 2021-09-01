package dev.rexijie.ukgovapi.service;

import dev.rexijie.ukgovapi.model.Sponsor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SponsorService {
    Flux<Sponsor> getSponsors();
    Mono<Sponsor> findSponsorByName(String name);
    Flux<Sponsor> findSponsorsMatchingName(String name);
    Flux<Sponsor> getSponsorsWithType(String type);
    Flux<Sponsor> getSponsorsWithType(String type, int start, int size);
}
