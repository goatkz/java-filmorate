package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    private final FilmController filmController = new FilmController(
            new FilmService(
                    new InMemoryFilmStorage(),
                    new InMemoryUserStorage()
            )
    );

    @Test
    void shouldCreateFilm() {
        Film film = createFilm("Film");

        Film createdFilm = filmController.createFilm(film);

        assertNotNull(createdFilm);
        assertEquals(1, createdFilm.getId());
        assertEquals("Film", createdFilm.getName());
    }

    @Test
    void shouldReturnAllFilms() {
        Film film = createFilm("Film");
        filmController.createFilm(film);

        Collection<Film> films = filmController.getFilms();

        assertEquals(1, films.size());
    }

    @Test
    void shouldReturnFilmById() {
        Film film = createFilm("Film");
        Film createdFilm = filmController.createFilm(film);

        Film foundFilm = filmController.getFilmById(createdFilm.getId());

        assertNotNull(foundFilm);
        assertEquals(createdFilm.getId(), foundFilm.getId());
        assertEquals("Film", foundFilm.getName());
    }

    @Test
    void shouldUpdateFilm() {
        Film film = createFilm("Film");
        Film createdFilm = filmController.createFilm(film);

        Film updatedFilm = createFilm("Updated film");
        updatedFilm.setId(createdFilm.getId());

        Film result = filmController.updateFilm(updatedFilm);

        assertEquals(createdFilm.getId(), result.getId());
        assertEquals("Updated film", result.getName());
    }

    @Test
    void shouldRejectFilmWithEmptyName() {
        Film film = createFilm(" ");

        assertThrows(
                RuntimeException.class,
                () -> filmController.createFilm(film)
        );
    }

    @Test
    void shouldRejectFilmWithDescriptionLongerThan200Characters() {
        Film film = createFilm("Film");
        film.setDescription("a".repeat(201));

        assertThrows(
                RuntimeException.class,
                () -> filmController.createFilm(film)
        );
    }

    @Test
    void shouldRejectFilmWithReleaseDateBeforeMinimum() {
        Film film = createFilm("Film");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        assertThrows(
                RuntimeException.class,
                () -> filmController.createFilm(film)
        );
    }

    @Test
    void shouldRejectFilmWithNonPositiveDuration() {
        Film film = createFilm("Film");
        film.setDuration(0);

        assertThrows(
                RuntimeException.class,
                () -> filmController.createFilm(film)
        );
    }

    @Test
    void shouldReturnPopularFilms() {
        Film firstFilm = createFilm("First film");
        Film secondFilm = createFilm("Second film");

        filmController.createFilm(firstFilm);
        filmController.createFilm(secondFilm);

        Collection<Film> popularFilms = filmController.getPopularFilms(10);

        assertEquals(2, popularFilms.size());
    }

    private Film createFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Film description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        return film;
    }
}