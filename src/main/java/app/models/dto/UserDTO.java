package app.models.dto;

import app.security.ValidPassword;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserDTO {
    private Long id;
    private String username;

    @ValidPassword
    private String password;

    @Email(message = "Неверный формат email")
    private String email;

    private List<UserMovieDTO> watchedMovies;
}