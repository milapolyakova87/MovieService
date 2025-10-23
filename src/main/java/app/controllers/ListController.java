package app.controllers;

import app.models.User;
import app.models.UserList;
import app.repositories.UserRepository;
import app.services.UserListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ListController {
    private final UserListService userListService;
    private final UserRepository userRepository;

    @GetMapping("/list/{publicId}")
    public String showPublicList(@PathVariable String publicId, Model model) {
        Optional<UserList> listOpt = userListService.getPublicListById(publicId);
        if (listOpt.isEmpty()) {
            return "error";
        }
        UserList list = listOpt.get();
        model.addAttribute("list", list);
        model.addAttribute("movies", list.getMovies());
        return "shared-list";
    }

    @PostMapping("/share")
    public String shareFavorites(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        String publicId = userListService.createPublicListFromFavorites(user);
        return "redirect:/list/" + publicId;
    }
}