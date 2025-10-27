package app.controllers;

import app.models.UserMovie;
import app.models.dto.MovieDTO;
import app.services.MovieApiService;
import app.services.UserMovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class FilmController {

    private final MovieApiService movieApiService;
    private final UserMovieService userMovieService;

    @GetMapping("/films")
    public String showFilmsPage(
            @RequestParam(name = "query", required = false) String query,
            Model model) {

        List<MovieDTO> movies = List.of();

        if (query != null && !query.isBlank()) {
            movies = movieApiService.searchMovies(query).block();
        }

        model.addAttribute("query", query == null ? "" : query);
        model.addAttribute("movies", movies);
        return "films";
    }

    @GetMapping("/favorites")
    public String showFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            Model model) {

        var pageable = PageRequest.of(page, size);
        Page<UserMovie> favorites = userMovieService.getFavoritesForCurrentUser(pageable);

        model.addAttribute("userMovies", favorites.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", favorites.getTotalPages());
        model.addAttribute("totalItems", favorites.getTotalElements());
        model.addAttribute("size", size);

        return "favorites";
    }
}