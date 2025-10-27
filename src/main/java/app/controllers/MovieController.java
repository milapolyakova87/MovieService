package app.controllers;

import app.models.dto.MovieDTO;
import app.services.MovieApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Tag(name = "Фильмы", description = "API для поиска фильмов")
public class MovieController {

    private final MovieApiService movieApiService;

    @GetMapping("/search")
    @Operation(
            summary = "Поиск фильмов по названию",
            description = "Возвращает список фильмов, соответствующих запросу"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Список найденных фильмов",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = MovieDTO.class))
            }
    )
    public Mono<List<MovieDTO>> search(
            @Parameter(description = "Название фильма для поиска", example = "Inception")
            @RequestParam String title) {
        return movieApiService.searchMovies(title);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить фильм по ID",
            description = "Возвращает данные фильма по его идентификатору"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Данные фильма",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = MovieDTO.class))
            }
    )
    public Mono<MovieDTO> getMovieById(
            @Parameter(description = "ID фильма", example = "123")
            @org.springframework.web.bind.annotation.PathVariable Long id) {
        return movieApiService.getMovieById(id);
    }
}