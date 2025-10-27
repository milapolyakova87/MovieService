package app.controllers;

import app.models.dto.UserMovieDTO;
import app.services.UserMovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users/movies")
@RequiredArgsConstructor
@Tag(name = "Фильмы пользователя", description = "API для добавления фильмов пользователями")
public class UserMovieController {
    private final UserMovieService userMovieService;

    @PostMapping
    @Operation(summary = "Добавить фильм пользователю", description = "Добавляет фильм в список пользователя")
    @ApiResponse(responseCode = "200", description = "Фильм успешно добавлен", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = UserMovieDTO.class))
    })
    public ResponseEntity<Void> addMovie(@RequestBody UserMovieDTO userMovieDTO) {

        Long movieId = userMovieDTO.getMovieId();
        if (movieId == null) {
            throw new IllegalArgumentException("movieId не может быть null");
        }

        userMovieService.addToFavorites(movieId);
        return ResponseEntity.ok().build();
    }
}
