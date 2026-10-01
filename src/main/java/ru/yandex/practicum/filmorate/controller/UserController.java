package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage = new UserStorage();

    @PostMapping
    public User createUser(@RequestBody User user) {
        validateUser(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        User createdUser = userStorage.create(user);

        log.info("Добавлен новый пользователь: {}", createdUser);

        return createdUser;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        validateUser(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        User updatedUser = userStorage.update(user)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id={} не найден", user.getId());
                    return new ValidationException(
                            "Пользователь с таким id не найден"
                    );
                });

        log.info("Обновлён пользователь: {}", updatedUser);

        return updatedUser;
    }

    @GetMapping
    public List<User> getUsers() {
        return userStorage.findAll();
    }

    private void validateUser(User user) {
        if (user.getEmail() == null
                || user.getEmail().isBlank()
                || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации: некорректный email");
            throw new ValidationException("Некорректный email");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Ошибка валидации: логин не может быть пустым");
            throw new ValidationException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            log.warn("Ошибка валидации: логин не может содержать пробелы");
            throw new ValidationException(
                    "Логин не может содержать пробелы"
            );
        }

        if (user.getBirthday() == null) {
            log.warn("Ошибка валидации: дата рождения не может быть пустой");
            throw new ValidationException(
                    "Дата рождения не может быть пустой"
            );
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn(
                    "Ошибка валидации: дата рождения не может быть в будущем"
            );
            throw new ValidationException(
                    "Дата рождения не может быть в будущем"
            );
        }
    }
}