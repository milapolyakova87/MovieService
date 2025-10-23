package app.models;

import app.models.dto.UserDTO;
import app.models.dto.UserMovieDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    @Mapping(target = "watchedMovies", ignore = true)
    User toEntity(UserDTO userDTO);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "movieId", source = "movie.id")
    UserMovieDTO toDTO(UserMovie userMovie);

}