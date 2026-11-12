
package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    @Override
    @Transactional
    public User create(User user) {
        String sql = """
                INSERT INTO users (email, login, name, birthday)
                VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, user.getBirthday() == null
                    ? null
                    : Date.valueOf(user.getBirthday()));

            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();

        if (generatedId == null) {
            throw new IllegalStateException("Не удалось получить id пользователя");
        }

        user.setId(generatedId.intValue());

        saveFriends(user);

        return findById(user.getId()).orElseThrow();
    }

    @Override
    @Transactional
    public User update(User user) {
        String sql = """
                UPDATE users
                SET email = ?, login = ?, name = ?, birthday = ?
                WHERE id = ?
                """;

        int rowsUpdated = jdbcTemplate.update(
                sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday() == null
                        ? null
                        : Date.valueOf(user.getBirthday()),
                user.getId()
        );

        if (rowsUpdated == 0) {
            throw new NotFoundException(
                    "Пользователь с id " + user.getId() + " не найден"
            );
        }

        jdbcTemplate.update(
                "DELETE FROM user_friends WHERE user_id = ?",
                user.getId()
        );

        saveFriends(user);

        return findById(user.getId()).orElseThrow();
    }

    @Override
    public Optional<User> findById(int id) {
        List<User> users = jdbcTemplate.query(
                "SELECT * FROM users WHERE id = ?",
                userRowMapper,
                id
        );

        if (users.isEmpty()) {
            return Optional.empty();
        }

        User user = users.get(0);
        loadFriends(user);

        return Optional.of(user);
    }

    @Override
    public List<User> findAll() {
        List<User> users = jdbcTemplate.query(
                "SELECT * FROM users ORDER BY id",
                userRowMapper
        );

        users.forEach(this::loadFriends);

        return users;
    }

    private void saveFriends(User user) {
        if (user.getFriends() == null || user.getFriends().isEmpty()) {
            return;
        }

        String sql = """
                INSERT INTO user_friends (user_id, friend_id)
                VALUES (?, ?)
                """;

        for (Integer friendId : user.getFriends()) {
            jdbcTemplate.update(sql, user.getId(), friendId);
        }
    }

    private void loadFriends(User user) {
        List<Integer> friendIds = jdbcTemplate.queryForList(
                "SELECT friend_id FROM user_friends WHERE user_id = ?",
                Integer.class,
                user.getId()
        );

        user.getFriends().clear();
        user.getFriends().addAll(friendIds);
    }
}