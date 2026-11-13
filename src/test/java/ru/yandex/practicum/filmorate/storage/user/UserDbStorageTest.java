
package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    @Autowired
    UserDbStorageTest(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    @Test
    void shouldCreateUser() {
        User user = makeUser("user1@example.com", "user1");

        User createdUser = userDbStorage.create(user);

        assertNotNull(createdUser.getId());
        assertEquals("user1@example.com", createdUser.getEmail());
        assertEquals("user1", createdUser.getLogin());
    }

    @Test
    void shouldUpdateUser() {
        User user = userDbStorage.create(
                makeUser("user2@example.com", "user2")
        );

        user.setName("Новое имя");
        user.setEmail("updated@example.com");

        User updatedUser = userDbStorage.update(user);

        assertEquals("Новое имя", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    void shouldFindUserById() {
        User user = userDbStorage.create(
                makeUser("user3@example.com", "user3")
        );

        Optional<User> foundUser = userDbStorage.findById(user.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(user.getId(), foundUser.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        Optional<User> foundUser = userDbStorage.findById(999);

        assertTrue(foundUser.isEmpty());
    }

    @Test
    void shouldFindAllUsers() {
        userDbStorage.create(makeUser("user4@example.com", "user4"));
        userDbStorage.create(makeUser("user5@example.com", "user5"));

        List<User> users = userDbStorage.findAll();

        assertTrue(users.size() >= 2);
    }

    @Test
    void shouldSaveAndLoadFriends() {
        User firstUser = userDbStorage.create(
                makeUser("user6@example.com", "user6")
        );

        User secondUser = userDbStorage.create(
                makeUser("user7@example.com", "user7")
        );

        firstUser.setFriends(new HashSet<>(Set.of(secondUser.getId())));
        userDbStorage.update(firstUser);

        User savedUser = userDbStorage.findById(firstUser.getId())
                .orElseThrow();

        assertEquals(Set.of(secondUser.getId()), savedUser.getFriends());
    }

    private User makeUser(String email, String login) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(login);
        user.setBirthday(LocalDate.of(1995, 5, 15));
        user.setFriends(new HashSet<>());

        return user;
    }
}
