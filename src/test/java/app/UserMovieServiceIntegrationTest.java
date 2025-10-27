package app;
// Тестирование сервиса UserMovieService
import app.models.Movie;
import app.models.User;
import app.models.UserMovie;
import app.repositories.UserMovieRepository;
import app.services.AuthenticationFacade;
import app.services.MovieApiService;
import app.services.MovieService;
import app.services.UserMovieService;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;

@DataJpaTest
@Testcontainers
@Import(UserMovieServiceIntegrationTest.TestConfig.class)
class UserMovieServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserMovieRepository userMovieRepository;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private MovieApiService movieApiService;

    @Autowired
    private MovieService movieService;

    private UserMovieService userMovieService;

    @BeforeEach
    void setUp() {
        userMovieService = new UserMovieService(
                userMovieRepository,
                authenticationFacade,
                movieService,
                movieApiService
        );
    }

    @Test
    void addToFavorites_shouldSaveUserMovie() {
        // Given
        String username = "testUser";
        Long movieId = 1L;

        Movie movie = new Movie();
        movie.setId(movieId);
        movie.setTitle("Test Movie");

        // Моки
        when(authenticationFacade.getCurrentUsername()).thenReturn(Optional.of(username));
        when(movieApiService.getMovieById(movieId)).thenReturn(Mono.just(new app.models.dto.MovieDTO()));
        when(movieService.getOrCreateMovie(any())).thenReturn(movie);
        when(userMovieRepository.findUserIdByUsername(username)).thenReturn(Optional.of(1L));

        // When
        userMovieService.addToFavorites(movieId);

        // Then
        Optional<UserMovie> saved = userMovieRepository
                .findByUserUsernameAndMovieId(username, movieId)
                .stream()
                .findFirst();

        assertThat(saved).isPresent();
        assertThat(saved.get().getViewDate()).isEqualTo(LocalDate.now());
        assertThat(saved.get().getRating()).isNull();
    }

    @Test
    void removeFavorite_shouldDeleteEntry() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Test Movie");

        UserMovie userMovie = new UserMovie();
        userMovie.setUser(user);
        userMovie.setMovie(movie);
        userMovie.setViewDate(LocalDate.now());

        UserMovie saved = userMovieRepository.save(userMovie);

        // When
        userMovieService.removeFavorite(saved.getId());

        // Then
        assertThat(userMovieRepository.findById(saved.getId())).isEmpty();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        AuthenticationFacade authenticationFacade() {
            return mock(AuthenticationFacade.class);
        }

        @Bean
        @Primary
        MovieApiService movieApiService() {
            return mock(MovieApiService.class);
        }

        @Bean
        @Primary
        MovieService movieService() {
            return mock(MovieService.class);
        }
    }
}