package dev.rexijie.ukgovapi.web;

import dev.rexijie.ukgovapi.config.ParserConfig;
import dev.rexijie.ukgovapi.model.Sponsor;
import dev.rexijie.ukgovapi.service.SponsorServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@WebFluxTest
@AutoConfigureWebTestClient
@Import(value = {SponsorServiceImpl.class, ParserConfig.class})
@ActiveProfiles("test")
class SponsorControllerTest {

    @Autowired
    WebTestClient webClient;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void fetchSponsorList() {
        webClient
                .get()
                .uri("/sponsors")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void getSponsor() {
        webClient
                .get()
                .uri("/sponsors?name=Barclays")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<List<Sponsor>>() {
                })
                .consumeWith(res -> Assertions.assertThat(res.getResponseBody())
                        .isNotEmpty());
    }

    @Test
    void getSponsorByName() {
        webClient
                .get()
                .uri("/sponsors/BBC")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Sponsor.class)
                .consumeWith(res -> Assertions.assertThat(res.getResponseBody())
                        .extracting(Sponsor::name)
                        .isEqualTo("BBC"));
    }
}