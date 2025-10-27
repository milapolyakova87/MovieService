package app.services;

import app.models.dto.MovieDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieApiService {

    private final WebClient webClient;

    @Value("${TMBD_API_KEY}")
    private String tmdbApiKey;

    public Mono<List<MovieDTO>> searchMovies(String query) {
        return webClient.get()
                .uri("/search/movie?query={query}&api_key={apiKey}&include_adult=false&language=en-US&page=1",
                        query, tmdbApiKey)
                .retrieve()
                .bodyToMono(MovieSearchResponse.class)
                .map(response -> {
                    if (response != null && response.getResults() != null) {
                        return response.getResults().stream()
                                .toList();
                    }
                    return List.<MovieDTO>of();
                })
                .doOnNext(results -> log.info("Найдено {} фильмов по запросу '{}'", results.size(), query))
                .onErrorMap(ex -> new RuntimeException("Ошибка при поиске фильмов на TMDb", ex));
    }

    public Mono<MovieDTO> getMovieById(Long id) {
        log.info("Запрашиваем фильм с ID: {}", id);

        return webClient.get()
                .uri("/movie/{movieId}?api_key={apiKey}&language=en-US", id, tmdbApiKey)
                .retrieve()
                .bodyToMono(MovieDTO.class)
                .onErrorMap(ex -> new RuntimeException("Ошибка при получении фильма с ID " + id + " с TMDb", ex));
    }
}