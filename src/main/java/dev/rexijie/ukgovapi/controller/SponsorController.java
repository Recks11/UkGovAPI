package dev.rexijie.ukgovapi.controller;

import dev.rexijie.ukgovapi.errors.InvalidRequestException;
import dev.rexijie.ukgovapi.errors.SponsorNotFoundException;
import dev.rexijie.ukgovapi.model.Sponsor;
import dev.rexijie.ukgovapi.service.SponsorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.CorePublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/sponsors")
public class SponsorController {

    private final SponsorService sponsorService;

    public SponsorController(@Autowired SponsorService sponsorService) {
        this.sponsorService = sponsorService;
    }

    @GetMapping("/fetch")
    public Mono<Map<String, Boolean>> fetchSponsorList() {
        return sponsorService.updateSponsorList()
                        .map(status -> Map.of("status", status));
    }

    @GetMapping
    public CorePublisher<?> getSponsor(@RequestParam Map<String, String> parameters) {
        if (parameters.containsKey("name")) return getSponsorByName(parameters.get("name"));
        if (parameters.containsKey("namelike")) return getSponsorsMatchingName(parameters.get("namelike"));
        try {
            if (parameters.containsKey("type")) return getSponsorFromType(
                    parameters.get("type"),
                    Integer.parseInt(parameters.getOrDefault("size", "25")),
                    Integer.parseInt(parameters.getOrDefault("page", "1"))
            );
        } catch (NumberFormatException exception) {
            throw new InvalidRequestException("error with query parameter format for size or page");
        }
        return sponsorService.getSponsors().count()
                .map(count -> Map.of("total", count));
    }

    private Mono<Sponsor> getSponsorByName(String name) {
        if (name == null) throw new SponsorNotFoundException("what are you looking for?");
        return sponsorService.findSponsorByName(name);
    }

    private Flux<Sponsor> getSponsorsMatchingName(String name) {
        if (name == null) throw new SponsorNotFoundException("what are you looking for?");
        return sponsorService.findSponsorsMatchingName(name);
    }

    private Flux<Sponsor> getSponsorFromType(String type, int size, int page) {
        if (type == null) throw new SponsorNotFoundException("what are you looking for?");
        if (size < 0) size = 25;
        if (page < 0) page = 1;
        int start = (page - 1) * size;
        return sponsorService.getSponsorsWithType(type, start, size);
    }
}
