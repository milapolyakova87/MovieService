package app.controllers;

import app.models.dto.UserDTO;
import app.services.SecurityService;
import app.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegisterController {

    private final UserService userService;
    private final SecurityService securityService;

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping
    public String registerUser(
            @Valid @ModelAttribute("user") UserDTO userDTO,
            BindingResult result,
            Model model,
            HttpServletRequest request) {

        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.createUser(userDTO);

            // Аутентифицирую пользователя через Security сервис
            securityService.authenticateAndSetContext(userDTO.getUsername(), userDTO.getPassword());


            // Эти 3 строки для для немедленного логина после регистрации
            HttpSession session = request.getSession(); // Получаю текущую сессию пользователя
            session.setAttribute("SPRING_SECURITY_CONTEXT",
                    SecurityContextHolder.getContext());  // Сохраняю контекст в сесии, чтобы Spring Security "запомнил" пользователя после редиректа

            return "redirect:/films";

        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Ошибка регистрации: " + ex.getMessage());
            return "register";
        }
    }
}