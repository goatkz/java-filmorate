
package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    @Override
    @Transactional
    public Film create(Film film) {
        String sql = """
                INSERT INTO films (name, description, release_date, duration, mpa_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());

            if (film.getMpa() == null) {
                statement.setNull(5, java.sql.Types.INTEGER);
            } else {
                statement.setInt(5, film.getMpa().getId());
            }

            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();

        if (generatedId == null) {
            throw new IllegalStateException("Не удалось получить id фильма");
        }

        film.setId(generatedId.intValue());

        saveGenres(film);
        saveLikes(film);

        return findById(film.getId()).orElseThrow();
    }

    @Override
    @Transactional
    public Film update(Film film) {
        String sql = """
                UPDATE films
                SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
                WHERE id = ?
                """;

        int rowsUpdated = jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa() == null ? null : film.getMpa().getId(),
                film.getId()
        );

        if (rowsUpdated == 0) {
            throw new NotFoundException(
                    "Фильм с id " + film.getId() + " не найден"
            );
        }

        jdbcTemplate.update(
                "DELETE FROM film_genres WHERE film_id = ?",
                film.getId()
        );

        jdbcTemplate.update(
                "DELETE FROM film_likes WHERE film_id = ?",
                film.getId()
        );

        saveGenres(film);
        saveLikes(film);

        return findById(film.getId()).orElseThrow();
    }

    @Override
    public Optional<Film> findById(int id) {
        List<Film> films = jdbcTemplate.query(
                """
                SELECT f.id, f.name, f.description, f.release_date,
                       f.duration, f.mpa_id, m.name AS mpa_name
                FROM films AS f
                LEFT JOIN mpa AS m ON f.mpa_id = m.id
                WHERE f.id = ?
                """,
                filmRowMapper,
                id
        );

        if (films.isEmpty()) {
            return Optional.empty();
        }

        Film film = films.get(0);

        loadGenres(film);
        loadLikes(film);

        return Optional.of(film);
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query(
                """
                SELECT f.id, f.name, f.description, f.release_date,
                       f.duration, f.mpa_id, m.name AS mpa_name
                FROM films AS f
                LEFT JOIN mpa AS m ON f.mpa_id = m.id
                ORDER BY f.id
                """,
                filmRowMapper
        );

        films.forEach(this::loadGenres);
        films.forEach(this::loadLikes);

        return films;
    }

    private void saveGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        String sql = """
                INSERT INTO film_genres (film_id, genre_id, genre_order)
                VALUES (?, ?, ?)
                """;

        int genreOrder = 0;

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(
                    sql,
                    film.getId(),
                    genre.getId(),
                    genreOrder
            );

            genreOrder++;
        }
    }

    private void saveLikes(Film film) {
        if (film.getLikes() == null || film.getLikes().isEmpty()) {
            return;
        }

        String sql = """
                INSERT INTO film_likes (film_id, user_id)
                VALUES (?, ?)
                """;

        for (Integer userId : film.getLikes()) {
            jdbcTemplate.update(sql, film.getId(), userId);
        }
    }

    private void loadGenres(Film film) {
        List<Genre> genres = jdbcTemplate.query(
                """
                SELECT g.id, g.name
                FROM genres AS g
                JOIN film_genres AS fg ON g.id = fg.genre_id
                WHERE fg.film_id = ?
                ORDER BY fg.genre_order
                """,
                (rs, rowNum) -> {
                    Genre genre = new Genre();
                    genre.setId(rs.getInt("id"));
                    genre.setName(rs.getString("name"));
                    return genre;
                },
                film.getId()
        );

        film.getGenres().clear();
        film.getGenres().addAll(genres);
    }

    private void loadLikes(Film film) {
        List<Integer> likes = jdbcTemplate.queryForList(
                "SELECT user_id FROM film_likes WHERE film_id = ?",
                Integer.class,
                film.getId()
        );

        film.getLikes().clear();
        film.getLikes().addAll(likes);
    }
}
