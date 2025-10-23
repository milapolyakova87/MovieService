package app.repositories;

import app.models.UserList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserListRepository extends JpaRepository<UserList, Long> {
    Optional<UserList> findByPublicId(String publicId);
}
