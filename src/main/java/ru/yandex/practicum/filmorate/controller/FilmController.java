
package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final String FILM_LIKE_PATH = "/{id}/like/{userId}";

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        validateFilm(film);
        Film createdFilm = filmService.addFilm(film);
        log.info("Добавлен новый фильм: {}", createdFilm);
        return createdFilm;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        validateFilm(film);
        Film updatedFilm = filmService.updateFilm(film);
        log.info("Обновлён фильм: {}", updatedFilm);
        return updatedFilm;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(
            @RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    @PutMapping(FILM_LIKE_PATH)
    public void addLike(
            @PathVariable int id,
            @PathVariable int userId) {
        filmService.addLike(id, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, id);
    }

    @DeleteMapping(FILM_LIKE_PATH)
    public void removeLike(
            @PathVariable int id,
            @PathVariable int userId) {
        filmService.removeLike(id, userId);
        log.info("Пользователь {} убрал лайк у фильма {}", userId, id);
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException(
                    "Название фильма не может быть пустым"
            );
        }

        if (film.getDescription() != null
                && film.getDescription().length() > 200) {
            throw new ValidationException(
                    "Описание фильма не должно превышать 200 символов"
            );
        }

        if (film.getReleaseDate() == null) {
            throw new ValidationException(
                    "Дата релиза не может быть пустой"
            );
        }

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException(
                    "Дата релиза не может быть раньше 28 декабря 1895 года"
            );
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException(
                    "Продолжительность фильма должна быть положительной"
            );
        }
    }
}
