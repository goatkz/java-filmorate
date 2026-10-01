package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    private final FilmController filmController = new FilmController();

    @Test
    void createFilm_whenNameIsEmpty_throwsException() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(
                ValidationException.class,
                () -> filmController.createFilm(film)
        );
    }

    @Test
    void createFilm_whenDescriptionIsTooLong_throwsException() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(
                ValidationException.class,
                () -> filmController.createFilm(film)
        );
    }

    @Test
    void createFilm_whenReleaseDateIsTooEarly_throwsException() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        assertThrows(
                ValidationException.class,
                () -> filmController.createFilm(film)
        );
    }

    @Test
    void createFilm_whenReleaseDateIsMinimum_doesNotThrowException() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(120);

        assertDoesNotThrow(
                () -> filmController.createFilm(film)
        );
    }

    @Test
    void createFilm_whenDurationIsZero_throwsException() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);

        assertThrows(
                ValidationException.class,
                () -> filmController.createFilm(film)
        );
    }

    @Test
    void createFilm_whenDurationIsNegative_throwsException() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-1);

        assertThrows(
                ValidationException.class,
                () -> filmController.createFilm(film)
        );
    }

    @Test
    void createFilm_whenValid_returnsFilmWithId() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film createdFilm = filmController.createFilm(film);

        assertEquals(1, createdFilm.getId());
        assertEquals("Film", createdFilm.getName());
        assertEquals("Description", createdFilm.getDescription());
        assertEquals(
                LocalDate.of(2000, 1, 1),
                createdFilm.getReleaseDate()
        );
        assertEquals(120, createdFilm.getDuration());
    }
}