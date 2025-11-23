package com.example.kafkadlq.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class ThirdPartyService {

    private final WebClient client;
    private final String endpoint;

    public ThirdPartyService(@Value("${app.third-party.base-url}") String baseUrl,
                             @Value("${app.third-party.endpoint}") String endpoint) {
        this.client = WebClient.builder().baseUrl(baseUrl).build();
        this.endpoint = endpoint;
    }

    public String callThirdParty(Object body) {
        return client.post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(s -> !s.is2xxSuccessful(),
                        r -> r.bodyToMono(String.class)
                                .flatMap(b -> Mono.error(new RuntimeException("Third party error: " + b))))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10))
                .block();
    }
}
