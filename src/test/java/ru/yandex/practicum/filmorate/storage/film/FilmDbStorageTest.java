
package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({FilmDbStorage.class, FilmRowMapper.class})
class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    FilmDbStorageTest(FilmDbStorage filmDbStorage, JdbcTemplate jdbcTemplate) {
        this.filmDbStorage = filmDbStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    void shouldCreateFilm() {
        Film film = makeFilm("Тестовый фильм");

        Film createdFilm = filmDbStorage.create(film);

        assertTrue(createdFilm.getId() > 0);
        assertEquals("Тестовый фильм", createdFilm.getName());
        assertEquals(1, createdFilm.getMpa().getId());
    }

    @Test
    void shouldUpdateFilm() {
        Film film = filmDbStorage.create(makeFilm("Старое название"));

        film.setName("Новое название");
        film.setDescription("Новое описание");
        film.setDuration(120);

        Film updatedFilm = filmDbStorage.update(film);

        assertEquals("Новое название", updatedFilm.getName());
        assertEquals("Новое описание", updatedFilm.getDescription());
        assertEquals(120, updatedFilm.getDuration());
    }

    @Test
    void shouldFindFilmById() {
        Film film = filmDbStorage.create(makeFilm("Фильм для поиска"));

        Optional<Film> foundFilm = filmDbStorage.findById(film.getId());

        assertTrue(foundFilm.isPresent());
        assertEquals(film.getId(), foundFilm.get().getId());
        assertEquals("Фильм для поиска", foundFilm.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenFilmNotFound() {
        Optional<Film> foundFilm = filmDbStorage.findById(999);

        assertTrue(foundFilm.isEmpty());
    }

    @Test
    void shouldFindAllFilms() {
        filmDbStorage.create(makeFilm("Первый фильм"));
        filmDbStorage.create(makeFilm("Второй фильм"));

        List<Film> films = filmDbStorage.findAll();

        assertEquals(2, films.size());
    }

    @Test
    void shouldSaveAndLoadGenres() {
        Film film = makeFilm("Фильм с жанрами");

        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");

        film.setGenres(new HashSet<>(Set.of(genre)));

        Film createdFilm = filmDbStorage.create(film);

        Film foundFilm = filmDbStorage.findById(createdFilm.getId())
                .orElseThrow();

        assertEquals(1, foundFilm.getGenres().size());
        assertTrue(foundFilm.getGenres().stream()
                .anyMatch(savedGenre -> savedGenre.getId() == 1));
    }

    @Test
    void shouldSaveAndLoadLikes() {
        Integer userId = createTestUser();

        Film film = makeFilm("Фильм с лайком");
        film.setLikes(new HashSet<>(Set.of(userId)));

        Film createdFilm = filmDbStorage.create(film);

        Film foundFilm = filmDbStorage.findById(createdFilm.getId())
                .orElseThrow();

        assertEquals(Set.of(userId), foundFilm.getLikes());
    }

    private Film makeFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Описание тестового фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        film.setGenres(new HashSet<>());
        film.setLikes(new HashSet<>());

        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        film.setMpa(mpa);

        return film;
    }

    private Integer createTestUser() {
        jdbcTemplate.update(
                "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "film-test@example.com",
                "filmtestuser",
                "Тестовый пользователь",
                Date.valueOf(LocalDate.of(1995, 5, 15))
        );

        return jdbcTemplate.queryForObject(
                "SELECT MAX(id) FROM users",
                Integer.class
        );
    }
}
