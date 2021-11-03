package dev.rexijie.ukgovapi.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/loaderio-81b805e0b740290f0a0a0fcb06d0371a")
public class LoadTestController {

    @GetMapping
    public Mono<String> returnToken() {
        return Mono.just("loaderio-81b805e0b740290f0a0a0fcb06d0371a");
    }
}
