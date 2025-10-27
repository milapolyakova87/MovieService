package app;
//  Интеграционные тесты для MovieService
import app.models.User;
import app.models.UserMovie;
import app.models.dto.MovieDTO;
import app.repositories.UserRepository;
import app.services.MovieApiService;
import app.services.UserMovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@WithMockUser(username = "testuser")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MovieServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMovieService userMovieService;

    @BeforeEach
    void setUp() {
        MovieApiService movieApiService = Mockito.mock(MovieApiService.class);
        ReflectionTestUtils.setField(userMovieService, "movieApiService", movieApiService);

        // Создаем тестового пользователя
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("{noop}password");
        user.setEmail("testuser@example.com");
        userRepository.save(user);

        // Создаем мок-фильм
        MovieDTO mockMovie = new MovieDTO();
        mockMovie.setId(1L);
        mockMovie.setTitle("American Beauty");
        mockMovie.setOverview("Lester Burnham, a depressed suburban father in a mid-life crisis...");
        mockMovie.setPosterUrl("/wby9315QzVKdW9BonAefg8jGTTb.jpg");
        mockMovie.setReleaseDate("1999-09-15");

        // Настраиваем поведение мока
        Mockito.when(movieApiService.getMovieById(1L)).thenReturn(Mono.just(mockMovie));
    }

    @Test
    void testAddAndRemoveMovie() {
        Long movieId = 1L;

        // Получаем текущего пользователя
        Long userId = getCurrentUserId();
        System.out.println("Текущий пользователь ID: " + userId);

        // Добавление фильма в Избранное
        System.out.println("Добавляем фильм с ID: " + movieId);
        userMovieService.addToFavorites(movieId);

        // Получаем список избранных
        List<UserMovie> favorites = userMovieService.getFavoritesForCurrentUser();
        System.out.println("Найдено избранных фильмов: " + favorites.size());
        favorites.forEach(f -> System.out.println("Фильм ID: " + f.getMovie().getId()));

        // Проверка, что фильм добавлен
        UserMovie added = favorites.stream()
                .filter(fav -> fav.getMovie().getId().equals(movieId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Фильм не добавлен в избранное"));

        assertThat(added).isNotNull();
        assertThat(added.getMovie().getId()).isEqualTo(movieId);

        // Удаление
        System.out.println("Удаляем запись с ID: " + added.getId());
        userMovieService.removeFavorite(added.getId());

        // Проверка удаления
        List<UserMovie> updatedFavorites = userMovieService.getFavoritesForCurrentUser();
        System.out.println("Осталось избранных: " + updatedFavorites.size());
        assertThat(updatedFavorites).noneMatch(fav -> fav.getId().equals(added.getId()));
    }

    private Long getCurrentUserId() {
        return userRepository.findByUsername("testuser")
                .map(User::getId)
                .orElseThrow(() -> new AssertionError("Пользователь не найден"));
    }
}