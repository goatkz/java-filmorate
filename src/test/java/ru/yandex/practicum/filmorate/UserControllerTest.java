
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
    void createUser_whenEmailIsInvalid_throwsException() {
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
    void createUser_whenLoginIsEmpty_throwsException() {
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
    void createUser_whenLoginContainsSpaces_throwsException() {
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
    void createUser_whenBirthdayIsInFuture_throwsException() {
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
    void createUser_whenNameIsEmpty_usesLoginAsName() {
        User user = new User();
        user.setEmail("alex@mail.com");
        user.setLogin("alex");
        user.setName("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = controller.createUser(user);

        assertEquals("alex", createdUser.getName());
    }

    @Test
    void createUser_whenRequestIsEmpty_throwsException() {
        User user = new User();

        assertThrows(
                ValidationException.class,
                () -> controller.createUser(user)
        );
    }
}
