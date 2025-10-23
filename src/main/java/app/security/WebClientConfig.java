package app.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    private final String token;

    public WebClientConfig(@Value("${AUTH_TOKEN}") String token) {
        this.token = token;
    }

    @PostConstruct
    void validateToken() {
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("Зависимость AUTH_TOKEN не установлена");
        }
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://api.themoviedb.org/3")
                .filter(this::addAuthHeader)
                .build();
    }

    private Mono<ClientResponse> addAuthHeader(ClientRequest request, ExchangeFunction next) {
        ClientRequest newRequest = ClientRequest.from(request)
                .header("Authorization", "Bearer " + token)
                .header("accept", "application/json")
                .build();
        return next.exchange(newRequest);
    }
}