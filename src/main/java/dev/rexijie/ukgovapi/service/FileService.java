package dev.rexijie.ukgovapi.service;

import reactor.core.publisher.Flux;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.BaseStream;

public interface FileService {

    static Flux<String> readFile(Path path) {
        return Flux.using(() -> Files.lines(path),
                stringStream -> Flux.defer(() -> Flux.fromStream(stringStream)),
                BaseStream::close);
    }
}
