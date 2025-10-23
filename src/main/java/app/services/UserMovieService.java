package app.services;

import app.models.Movie;
import app.models.User;
import app.models.UserMovie;
import app.models.dto.MovieDTO;
import app.repositories.UserMovieRepository;
import app.utils.SortResults;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMovieService {
    private final UserMovieRepository userMovieRepository;
    private final AuthenticationFacade authenticationFacade;
    private final MovieService movieService;
    private final MovieApiService movieApiService;

    public String getCurrentUsername() {
        return authenticationFacade.getCurrentUsername()
                .orElseThrow(() -> new IllegalStateException("Пользователь не аутентифицирован"));
    }

    // Возвращает весь список избранных фильмов
    public List<UserMovie> getFavoritesForCurrentUser() {
        return getFavoritesForCurrentUser(PageRequest.of(0, Integer.MAX_VALUE))
                .getContent();
    }

    // Пагинация
    public Page<UserMovie> getFavoritesForCurrentUser(Pageable pageable) {
        String username = getCurrentUsername();
        return userMovieRepository.findByUserUsername(username, pageable);
    }

    public Long getCurrentUserId() {
        String username = authenticationFacade.getCurrentUsername()
                .orElseThrow(() -> new IllegalStateException("Пользователь не аутентифицирован"));

        return userMovieRepository.findUserIdByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    // Текущий юзер добавляет фильм в избранное
    public void addToFavorites(Long movieId) {
        String username = getCurrentUsername();
        if (username == null) {
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }

        // Если фильм в базе есть - добавляем его
        // Если нет - создается новая запись на шаблоне DTO
        MovieDTO movieDTO = movieApiService.getMovieById(movieId);
        Movie movie = movieService.getOrCreateMovie(movieDTO);

        // Проверяю, что текущий юзер еще не добавлял этот фильм
        if (userMovieRepository.findByUserUsernameAndMovieId(username, movie.getId()).isEmpty()) {
            UserMovie userMovie = new UserMovie();

            // временный юзер,чтобы связать фильм
            // с пользователем, не загружая из базы
            userMovie.setUser(new User(getCurrentUserId()));
            userMovie.setMovie(movie);
            userMovie.setViewDate(LocalDate.now());
            userMovie.setRating(null);
            userMovieRepository.save(userMovie);
        }
    }

    public void setRating(Long userMovieId, Integer rating) {
        UserMovie userMovie = userMovieRepository.findById(userMovieId)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));
        userMovie.setRating(rating);
        userMovieRepository.save(userMovie);
    }

    public void removeFavorite(Long userMovieId) {
        userMovieRepository.deleteById(userMovieId);
    }


    public List<UserMovie> getRecentUserMovies(int limit, SortResults sortBy) {
        Sort sort = switch (sortBy) {
            case rating_desc -> Sort.by(Sort.Direction.DESC, "rating");
            case rating_asc -> Sort.by(Sort.Direction.ASC, "rating");
            case date_asc -> Sort.by(Sort.Direction.ASC, "viewDate");
            case date_desc -> Sort.by(Sort.Direction.DESC, "viewDate");
        };

        return userMovieRepository.findAll(sort)
                .stream()
                .limit(limit)
                .toList();
    }
}
