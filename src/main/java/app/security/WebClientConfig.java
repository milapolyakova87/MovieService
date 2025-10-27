package app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class WebClientConfig {

    private final String token;

    public WebClientConfig(@Value("${AUTH_TOKEN}") String token) {
        this.token = token;
    }

    @Bean
    public ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (!log.isDebugEnabled()) {
                return Mono.just(clientResponse);
            }

            return clientResponse.bodyToFlux(DataBuffer.class)
                    .publish()
                    .refCount(2)
                    .map(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer);
                        return new String(bytes);
                    })
                    .reduce(String::concat)
                    .doOnNext(body -> {
                        try {
                            Object json = new ObjectMapper().readValue(body, Object.class);
                            String pretty = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(json);
                            log.debug("=== HTTP Response ===\n{}", pretty);
                        } catch (Exception e) {
                            log.debug("Cannot parse JSON body: {}", body);
                        }
                    })
                    .then(Mono.just(clientResponse));
        });
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
                .filter(logResponse())
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