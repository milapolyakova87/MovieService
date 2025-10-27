package app.services;

import app.models.User;
import app.models.UserMapper;
import app.models.dto.UserDTO;
import app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public void createUser(UserDTO userDTO) {
        log.info("Получен UserDTO: {}", userDTO);
        User user = userMapper.toEntity(userDTO);
        log.info("Создан User: {}", user);

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            log.error("Пароль не может быть пустым");
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        log.info("Пользователь успешно зарегистрирован: {}", user.getUsername());
    }
}