package app.controllers;

import app.services.MovieService;
import app.services.UserMovieService;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import app.models.dto.UserMovieDTO;

@Controller
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "Избранное", description = "API для работы с избранными фильмами")
public class FavoriteController {
    private final UserMovieService userMovieService;
    private final MovieService movieService;

    @PostMapping("/{userMovieId}")
    @Operation(
            summary = "Добавить фильм в избранное",
            description = "Добавляет фильм в избранное текущего пользователя"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Фильм успешно добавлен",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserMovieDTO.class)
                    )
            }
    )
    public String addToFavorites(@PathVariable Long userMovieId) {
        userMovieService.addToFavorites(userMovieId);
        return "redirect:/favorites";
    }

    @PutMapping("/{userMovieId}/rating")
    @Operation(
            summary = "Установить рейтинг фильму",
            description = "Устанавливает рейтинг фильму в избранном"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Рейтинг успешно установлен"
    )
    public String setRating(
            @Parameter(description = "ID связи пользователь-фильм", example = "101")
            @PathVariable Long userMovieId,

            @RequestParam Integer rating
    ) {
        userMovieService.setRating(userMovieId, rating);
        return "redirect:/favorites";
    }

    @DeleteMapping("/{userMovieId}")
    @Operation(
            summary = "Удалить фильм из избранного",
            description = "Удаляет фильм из списка избранных"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Фильм успешно удалён"
    )
    public String removeFavorite(
            @Parameter(description = "ID связи пользователь-фильм", example = "101")
            @PathVariable Long userMovieId
    ) {
        userMovieService.removeFavorite(userMovieId);
        return "redirect:/favorites";
    }
}
