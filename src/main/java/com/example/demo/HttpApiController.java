package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public record HttpApiController(LogProcessor logProcessor) {

    @PostMapping("/helloworld")
    public Mono<ResponseEntity<Void>> helloWorld(Model logs, ServerWebExchange exchange) {
        return logProcessor
                .push(Flux.just(logs))
                .then(Mono.just(new ResponseEntity<>(HttpStatus.OK)));
    }

    @PostMapping("/helloworldnoctx")
    public Mono<ResponseEntity<Void>> helloWorldnoctx(Model logs, ServerWebExchange exchange) {
        logProcessor.push(Flux.just(logs)).subscribe();
        return Mono.just(new ResponseEntity<>(HttpStatus.OK));
    }
}
