package app.controllers;

import app.models.UserMovie;
import app.services.UserMovieService;
import app.utils.SortResults;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    // Макс. кол-во фильмов, которые отображаются на главной
    private static final int RECENT_MOVIES_LIMIT = 10;

    private final UserMovieService userMovieService;

    @GetMapping("/")
    public String showHomePage(
            @RequestParam(defaultValue = "date_desc") SortResults sortBy,
            Model model) {

        List<UserMovie> recentMovies = userMovieService.getRecentUserMovies(RECENT_MOVIES_LIMIT, sortBy);
        model.addAttribute("recentMovies", recentMovies);
        model.addAttribute("sortBy", sortBy.name());// Вывожу строку в шаблоне на базовой странице для сортировки по дате просмотра и рейтингу

        return "base";
    }
}
