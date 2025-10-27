package app.services;

import app.models.dto.MovieDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MovieSearchResponse {
    private int page;
    private List<MovieDTO> results;
    private int totalPages;
    private int totalResults;

}
