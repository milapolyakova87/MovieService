package app.controllers;

import app.models.UserMovie;
import app.services.UserMovieService;
import app.utils.SortFilms;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private static final int RECENT_MOVIES_LIMIT = 10;

    private final UserMovieService userMovieService;

    @GetMapping("/")
    public String showHomePage(
            @RequestParam(defaultValue = "date_desc") SortFilms sortBy,
            Model model) {

        List<UserMovie> recentMovies = userMovieService.getRecentUserMovies(RECENT_MOVIES_LIMIT, sortBy);
        model.addAttribute("recentMovies", recentMovies);
        model.addAttribute("sortBy", sortBy.name());

        return "base";
    }
}
