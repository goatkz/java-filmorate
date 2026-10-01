package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    private final FilmController controller = new FilmController();

    private Film createValidFilm() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        film.setMpa(mpa);

        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        film.setGenres(List.of(genre));

        return film;
    }

    @Test
    void createFilm_whenNameIsEmpty_throwsException() {
        Film film = createValidFilm();
        film.setName("");

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    void createFilm_whenDescriptionIsTooLong_throwsException() {
        Film film = createValidFilm();
        film.setDescription("a".repeat(201));

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    void createFilm_whenReleaseDateIsBeforeMinimum_throwsException() {
        Film film = createValidFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    void createFilm_whenReleaseDateEqualsMinimum_createsFilm() {
        Film film = createValidFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        Film createdFilm = controller.createFilm(film);

        assertEquals(1, createdFilm.getId());
    }

    @Test
    void createFilm_whenDurationIsZero_throwsException() {
        Film film = createValidFilm();
        film.setDuration(0);

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    void createFilm_whenDurationIsNegative_throwsException() {
        Film film = createValidFilm();
        film.setDuration(-10);

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    void createFilm_whenRequestIsEmpty_throwsException() {
        Film film = new Film();

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    void createFilm_whenMpaIsMissing_throwsException() {
        Film film = createValidFilm();
        film.setMpa(null);

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    void createFilm_whenMpaIdIsInvalid_throwsException() {
        Film film = createValidFilm();

        Mpa mpa = new Mpa();
        mpa.setId(6);
        mpa.setName("Invalid");

        film.setMpa(mpa);

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    void createFilm_whenGenreIdIsInvalid_throwsException() {
        Film film = createValidFilm();

        Genre genre = new Genre();
        genre.setId(7);
        genre.setName("Invalid");

        film.setGenres(List.of(genre));

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }
}