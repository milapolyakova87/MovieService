package app.models;

import app.models.dto.UserMovieDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMovieMapper {

    @Mapping(target = "movie", expression = "java(new app.models.Movie())")
    @Mapping(target = "movie.id", source = "movieId")
    @Mapping(target = "viewDate", source = "viewDate")
    @Mapping(target = "rating", source = "rating")
    UserMovie toEntity(UserMovieDTO dto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "movieId", source = "movie.id")
    @Mapping(target = "viewDate", source = "viewDate")
    @Mapping(target = "rating", source = "rating")
    UserMovieDTO toDTO(UserMovie entity);
}