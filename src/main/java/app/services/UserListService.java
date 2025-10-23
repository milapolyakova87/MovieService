package app.services;

import app.models.User;
import app.models.UserList;
import app.models.UserListMovie;
import app.repositories.UserListRepository;
import app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserListService {

    private final UserListRepository userListRepository;
    private final UserRepository userRepository;

    public Optional<UserList> getPublicListById(String publicId) {
        return userListRepository.findByPublicId(publicId);
    }

    public String createPublicListFromFavorites(User user) {
        UserList list = new UserList();
        list.setPublicId(UUID.randomUUID().toString());
        list.setOwner(user);
        list.setPublic(true);

        List<UserListMovie> listMovies = user.getUserMovies().stream()
                .map(uml -> {
                    UserListMovie ulm = new UserListMovie();
                    ulm.setMovie(uml.getMovie());
                    ulm.setRating(uml.getRating());
                    ulm.setViewDate(uml.getViewDate());
                    ulm.setUserList(list);
                    return ulm;
                })
                .toList();

        list.setMovies(listMovies);
        userListRepository.save(list);

        return list.getPublicId();
    }
}
