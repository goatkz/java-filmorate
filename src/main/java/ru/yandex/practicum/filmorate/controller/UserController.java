package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        validateUser(user);

        User createdUser = userService.addUser(user);
        log.info("Добавлен пользователь: {}", createdUser);

        return createdUser;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        validateUser(user);

        User updatedUser = userService.updateUser(user);
        log.info("Обновлён пользователь: {}", updatedUser);

        return updatedUser;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable int id,
            @PathVariable int friendId
    ) {
        userService.addFriend(id, friendId);
        log.info("Пользователь {} добавил в друзья {}", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(
            @PathVariable int id,
            @PathVariable int friendId
    ) {
        userService.removeFriend(id, friendId);
        log.info("Пользователь {} удалил из друзей {}", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable int id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(
            @PathVariable int id,
            @PathVariable int otherId
    ) {
        return userService.getCommonFriends(id, otherId);
    }

    private void validateUser(User user) {
        if (user.getEmail() == null
                || user.getEmail().isBlank()
                || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный email");
        }

        if (user.getLogin() == null
                || user.getLogin().isBlank()
                || user.getLogin().contains(" ")) {
            throw new ValidationException(
                    "Логин не может быть пустым или содержать пробелы"
            );
        }

        if (user.getBirthday() != null
                && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException(
                    "Дата рождения не может быть в будущем"
            );
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}