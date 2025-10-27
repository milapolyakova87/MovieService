package app.repositories;

import app.models.UserMovie;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMovieRepository extends JpaRepository<UserMovie, Long> {

    List<UserMovie> findByUserUsernameAndMovieId(String username, Long movieId);

    // Оставила так, потому что пробовала
    // кейсы MilaPolyakova и milapolyakova - это нужно для точного совпадения
    @Query("SELECT u.id FROM User u WHERE LOWER(u.username) = LOWER(:username)")
    Optional<Long> findUserIdByUsername(@Param("username") String username);

    @EntityGraph(attributePaths = {"user", "movie"})
    Page<UserMovie> findByUserUsername(String username, Pageable pageable);
}