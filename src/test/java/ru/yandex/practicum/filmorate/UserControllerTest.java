
package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    private final UserController controller =
            new UserController(
                    new UserService(new InMemoryUserStorage())
            );

    @Test
    void createUserWithInvalidEmail() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("alex");
        user.setName("Alex");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(
                ValidationException.class,
                () -> controller.createUser(user)
        );
    }

    @Test
    void createUserWithEmptyLogin() {
        User user = new User();
        user.setEmail("alex@mail.com");
        user.setLogin("");
        user.setName("Alex");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(
                ValidationException.class,
                () -> controller.createUser(user)
        );
    }

    @Test
    void createUserWithLoginSpaces() {
        User user = new User();
        user.setEmail("alex@mail.com");
        user.setLogin("alex test");
        user.setName("Alex");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(
                ValidationException.class,
                () -> controller.createUser(user)
        );
    }

    @Test
    void createUserWithFutureBirthday() {
        User user = new User();
        user.setEmail("alex@mail.com");
        user.setLogin("alex");
        user.setName("Alex");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(
                ValidationException.class,
                () -> controller.createUser(user)
        );
    }

    @Test
    void createUserWithEmptyName() {
        User user = new User();
        user.setEmail("alex@mail.com");
        user.setLogin("alex");
        user.setName("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = controller.createUser(user);

        assertEquals("alex", createdUser.getName());
    }

    @Test
    void createUserWithEmptyRequest() {
        User user = new User();

        assertThrows(
                ValidationException.class,
                () -> controller.createUser(user)
        );
    }
}
