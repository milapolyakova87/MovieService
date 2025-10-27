package app.services;

import app.models.Movie;
import app.models.dto.MovieDTO;
import app.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public Movie getOrCreateMovie(MovieDTO movieDTO) {
        return movieRepository.findById(movieDTO.getId())
                .orElseGet(() -> movieRepository.save(mapToMovie(movieDTO)));
    }

    private Movie mapToMovie(MovieDTO movieDTO) {
        Movie movie = new Movie();
        movie.setTitle(movieDTO.getTitle());
        movie.setDescription(movieDTO.getOverview());
        movie.setReleaseDate(movieDTO.getReleaseDate());
        movie.setPosterUrl(movieDTO.getPosterUrl());
        return movie;
    }
}