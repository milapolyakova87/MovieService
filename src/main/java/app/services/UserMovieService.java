package app.services;

import app.models.Movie;
import app.models.User;
import app.models.UserMovie;
import app.models.dto.MovieDTO;
import app.repositories.UserMovieRepository;
import app.utils.SortFilms;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public List<UserMovie> getFavoritesForCurrentUser() {
        return getFavoritesForCurrentUser(PageRequest.of(0, Integer.MAX_VALUE)).getContent();
    }

    public Page<UserMovie> getFavoritesForCurrentUser(Pageable pageable) {
        String username = getCurrentUsername();
        return userMovieRepository.findByUserUsername(username, pageable);
    }

    public Long getCurrentUserId() {
        String username = getCurrentUsername();
        return userMovieRepository.findUserIdByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    public void addToFavorites(Long movieId) {
        String username = getCurrentUsername();
        if (username == null) {
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }

        MovieDTO movieDTO = movieApiService.getMovieById(movieId)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Фильм с ID " + movieId + " не найден в TMDb API"));

        Movie movie = movieService.getOrCreateMovie(movieDTO);

        if (userMovieRepository.findByUserUsernameAndMovieId(username, movie.getId()).isEmpty()) {
            UserMovie userMovie = new UserMovie();
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

    public List<UserMovie> getRecentUserMovies(int limit, SortFilms sortBy) {
        return userMovieRepository.findAll(sortBy.getSort())
                .stream()
                .limit(limit)
                .toList();
    }
}