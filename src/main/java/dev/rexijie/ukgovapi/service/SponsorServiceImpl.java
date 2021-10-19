package dev.rexijie.ukgovapi.service;

import dev.rexijie.ukgovapi.batch.DocumentDownloader;
import dev.rexijie.ukgovapi.converter.SponsorMapper;
import dev.rexijie.ukgovapi.errors.SponsorNotFoundException;
import dev.rexijie.ukgovapi.model.Sponsor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.BaseStream;

@Service
public class SponsorServiceImpl implements SponsorService {

    private final DocumentDownloader documentDownloader;

    public SponsorServiceImpl(DocumentDownloader documentDownloader) {
        this.documentDownloader = documentDownloader;
    }

    @Override
    public Mono<Boolean> updateSponsorList() {
        return Mono.fromCallable(documentDownloader::downloadSponsorList);
    }

    @Override
    public Flux<Sponsor> getSponsors() {
            return Flux.using(
                    () -> Files.lines(Path.of(documentDownloader.getPathToFile())),
                            stringStream -> Flux.defer(() -> Flux.fromStream(stringStream)),
                            BaseStream::close)
                    .skip(1)
                    .map(SponsorMapper::parseCsvLine)
                    .map(SponsorMapper::toSponsor);
    }

    @Override
    public Mono<Sponsor> findSponsorByName(String name) {
        return getSponsors()
                .filter(sponsor -> sponsor.name().equalsIgnoreCase(name))
                .reduce((sponsor, sponsor2) -> {
                    sponsor.Route().addAll(sponsor2.Route());
                    return sponsor;
                })
                .switchIfEmpty(Mono.error(new SponsorNotFoundException("No company called "+name+ " exist in the list of sponsors")));
    }

    @Override
    public Flux<Sponsor> findSponsorsMatchingName(String name) {
        return getSponsors()
                .filter(sponsor -> sponsor.name().toLowerCase().contains(name))
                .switchIfEmpty(Mono.error(new SponsorNotFoundException("No company named "+name+ " exist in the sponsor list :(")));
    }

    @Override
    public Flux<Sponsor> getSponsorsWithType(String type) {
        return getSponsors()
                .filter(sponsor -> sponsor.type()
                        .equals(SponsorMapper.getSponsorTypeEnum(type)))
                .switchIfEmpty(Mono.error(new SponsorNotFoundException("Sponsor of type "+type+ " does not exist")));
    }

    @Override
    public Flux<Sponsor> getSponsorsWithType(String type, int start, int size) {
        return getSponsorsWithType(type)
                .skip(start)
                .take(size);
    }
}
