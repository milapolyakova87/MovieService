package app.models.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserMovieDTO {
    private Long id;
    private Long userId;
    private Long movieId;
    private LocalDate viewDate;
    private Integer rating;
}