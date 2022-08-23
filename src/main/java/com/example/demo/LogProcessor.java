package com.example.demo;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public record LogProcessor(WebClient webClient) {

    public Mono<Object> push(Flux<Model> items) {
        return items
                .collectList()
                .flatMap(item -> webClient
                        .post()
                        .bodyValue(item)
                        .exchangeToMono(response -> Mono.just(response.headers())));
    }
}
