package app.services;

import app.models.dto.MovieDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${TMBD_API_KEY}")
    private String tmdbApiKey;

    public List<MovieDTO> searchMovies(String query) {
        MovieSearchResponse response = webClient.get()
                .uri("/search/movie?query={query}&api_key={apiKey}&include_adult=false&language=en-US&page=1",
                        query, tmdbApiKey)
                .retrieve()
                .bodyToMono(MovieSearchResponse.class)
                .block();

        return response != null ? response.getResults() : List.of();
    }

    public MovieDTO getMovieById(Long id) {
        log.info("Запрашиваем фильм с ID: {}", id);

        return webClient.get()
                .uri("/movie/{movieId}?api_key={apiKey}&language=en-US", id, tmdbApiKey)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(responseBody -> {
                    try {
                        Object json = objectMapper.readValue(responseBody, Object.class);
                        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
                        log.info("=== TMDb API Response (Movie ID: {}) ===\n{}", id, prettyJson);
                    } catch (JsonProcessingException e) {
                        log.warn("Не удалось красиво распечатать JSON (не критично)", e);
                    }
                })
                .flatMap(body -> {
                    try {
                        MovieDTO movieDTO = objectMapper.readValue(body, MovieDTO.class);
                        return Mono.just(movieDTO);
                    } catch (JsonProcessingException e) {
                        return Mono.error(new RuntimeException("Не удалось распарсить ответ от TMDb API", e));
                    }
                })
                .onErrorMap(ex -> new RuntimeException("Ошибка при получении фильма с TMDb", ex))
                .block();
    }
}