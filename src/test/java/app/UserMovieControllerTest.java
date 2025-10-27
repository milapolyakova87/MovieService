package app;
// Тестирование реализации REST API для добавления
// фильмов у пользователя в контроллере UserMovieController
import app.controllers.UserMovieController;
import app.models.dto.UserMovieDTO;
import app.services.UserMovieService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;

class UserMovieControllerTest {

    // инициализация и очистка ресурсов перед каждым тестом
    private AutoCloseable mocks;
    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userMovieController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    private MockMvc mockMvc;

    @Mock
    private UserMovieService userMovieService;

    // тестируемый контроллер , в который внедряются моки
    @InjectMocks
    private UserMovieController userMovieController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void addMovie_shouldReturnOk() throws Exception {
        UserMovieDTO inputDto = new UserMovieDTO();
        inputDto.setMovieId(100L);

        doNothing().when(userMovieService).addToFavorites(100L);

        mockMvc.perform(post("/api/users/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));

        verify(userMovieService, times(1)).addToFavorites(100L);
    }
}