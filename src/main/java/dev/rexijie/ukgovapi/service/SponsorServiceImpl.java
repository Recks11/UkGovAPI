package dev.rexijie.ukgovapi.service;

import dev.rexijie.ukgovapi.batch.DocumentDownloader;
import dev.rexijie.ukgovapi.converter.SponsorMapper;
import dev.rexijie.ukgovapi.errors.InvalidRequestException;
import dev.rexijie.ukgovapi.errors.SponsorNotFoundException;
import dev.rexijie.ukgovapi.model.Sponsor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SponsorServiceImpl implements SponsorService {
    private static final Logger LOG = LoggerFactory.getLogger(SponsorServiceImpl.class);
    private final DocumentDownloader documentDownloader;
    private final Map<String, Sponsor> sponsorMap = new ConcurrentHashMap<>();
    private final AtomicInteger atomicLength = new AtomicInteger(0);

    public SponsorServiceImpl(DocumentDownloader documentDownloader) {
        this.documentDownloader = documentDownloader;
    }

    @Override
    public Mono<Integer> updateSponsorList() {

        return Mono.fromCallable(documentDownloader::downloadSponsorList)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(aBoolean -> {
                    if (aBoolean) sponsorMap.clear();
                })
                .map(aBoolean -> Path.of(documentDownloader.getPathToFile()))
                .flatMapMany(FileService::readFile)
                .skip(1) // skip the headers
                .map(SponsorMapper::parseCsvLine)
                .map(SponsorMapper::toSponsor)
                .doOnNext(sponsor -> sponsorMap.merge(sponsor.name(), sponsor, (existing, newSponsor) -> {
                    existing.route().addAll(newSponsor.route());
                    return existing;
                }))
                .doOnNext(sponsor -> atomicLength.getAndAccumulate(sponsor.name().length(), Math::max)) // get the maximum value
                .doOnComplete(() -> LOG.debug("Sponsor Map contains %s elements".formatted(String.valueOf(sponsorMap.size()))))
                .then(Mono.fromCallable(sponsorMap::size));
    }

    @Override
    public Flux<Sponsor> getSponsors() {
        return Flux.fromIterable(sponsorMap.values());
    }

    @Override
    public Mono<Sponsor> findSponsorByName(String name) {
        return validateName(name)
                .then(Mono.fromCallable(() -> sponsorMap.get(name))
                        .doOnNext((s) -> LOG.debug("Found %s".formatted(s)))
                        .switchIfEmpty(Mono.error(new SponsorNotFoundException("No company called " + name + " exist in the list of sponsors"))));

    }

    @Override
    public Flux<Sponsor> findSponsorsMatchingName(String name) {
        return validateName(name)
                .thenMany(Flux.fromStream(sponsorMap.keySet().stream())
                        .filter(spName -> spName.toLowerCase().contains(name.toLowerCase()))
                        .switchIfEmpty(Flux.error(new SponsorNotFoundException("No company named " + name + " exist in the sponsor list :(")))
                        .map(sponsorMap::get))
                        .doOnNext((s) -> LOG.debug("Found %s".formatted(s)));

    }

    @Override
    public Flux<Sponsor> getSponsorsWithType(String type) {
        return getSponsors()
                .filter(sponsor -> sponsor.type()
                        .equals(SponsorMapper.getSponsorTypeEnum(type)))
                .switchIfEmpty(Mono.error(new SponsorNotFoundException("Sponsor of type " + type + " does not exist")));
    }

    @Override
    public Flux<Sponsor> getSponsorsWithType(String type, int start, int size) {
        return getSponsorsWithType(type)
                .skip(start)
                .take(size);
    }

    @Override
    public Mono<String> validateName(String name) {
        if (!StringUtils.hasLength(name)) throw new SponsorNotFoundException("No name provided");
        if (name.length() < 3) throw new InvalidRequestException("No name provided");
        return Mono.fromCallable(atomicLength::get)
                .flatMap(len -> len >= name.length() ? Mono.just(name) : Mono.error(new SponsorNotFoundException("Invalid name provided")));

    }

    @PostConstruct
    private void downloadSponsorList() {
        updateSponsorList()
                .doOnSuccess(integer -> LOG.info("Finished Executing Startup Event"))
                .subscribe(integer -> LOG.info("Downloaded Sponsor list containing {} sponsors", integer));
    }
}
