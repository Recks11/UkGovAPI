package dev.rexijie.ukgovapi.controller;

import dev.rexijie.ukgovapi.errors.InvalidRequestException;
import dev.rexijie.ukgovapi.errors.SponsorNotFoundException;
import dev.rexijie.ukgovapi.model.Sponsor;
import dev.rexijie.ukgovapi.service.SponsorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    public Mono<SponsorFetchResponse> fetchSponsorList() {
        return sponsorService.updateSponsorList()
                .map(length -> new SponsorFetchResponse("UPDATED", (long) length));
    }

    @GetMapping
    public CorePublisher<?> getSponsor(@RequestParam Map<String, String> parameters) {
        if (parameters.containsKey("name")) return getSponsorsMatchingName(parameters.get("name"));
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
                .map(aLong -> new SponsorFetchResponse("COMPLETE", aLong));
    }

    @GetMapping("/{name}")
    public Mono<Sponsor> getSponsorByName(@PathVariable String name) {
        return sponsorService.findSponsorByName(name);
    }

    private Flux<Sponsor> getSponsorsMatchingName(String name) {
        return sponsorService.findSponsorsMatchingName(name);
    }

    private Flux<Sponsor> getSponsorFromType(String type, int size, int page) {
        if (type == null) throw new SponsorNotFoundException("what are you looking for?");
        if (type.length() > 32) throw new SponsorNotFoundException("what are you looking for?");
        if (size < 0) size = 25;
        if (page < 0) page = 1;
        int start = (page - 1) * size;
        return sponsorService.getSponsorsWithType(type, start, size);
    }
}
