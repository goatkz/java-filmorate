package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private static final LocalDate MIN_RELEASE_DATE =
            LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage = new FilmStorage();

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        validateFilm(film);

        Film createdFilm = filmStorage.create(film);

        log.info("Добавлен новый фильм: {}", createdFilm);

        return createdFilm;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        validateFilm(film);

        Film updatedFilm = filmStorage.update(film)
                .orElseThrow(() -> {
                    log.warn("Фильм с id={} не найден", film.getId());
                    return new NotFoundException(
                            "Фильм с таким id не найден"
                    );
                });

        log.info("Обновлён фильм: {}", updatedFilm);

        return updatedFilm;
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmStorage.findAll();
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Ошибка валидации: название фильма не может быть пустым");
            throw new ValidationException(
                    "Название фильма не может быть пустым"
            );
        }

        if (film.getDescription() != null
                && film.getDescription().length() > 200) {
            log.warn(
                    "Ошибка валидации: описание фильма длиннее 200 символов"
            );
            throw new ValidationException(
                    "Описание не может быть длиннее 200 символов"
            );
        }

        if (film.getReleaseDate() == null
                || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Ошибка валидации: некорректная дата релиза");
            throw new ValidationException(
                    "Дата релиза не может быть раньше 28 декабря 1895 года"
            );
        }

        if (film.getDuration() <= 0) {
            log.warn(
                    "Ошибка валидации: продолжительность фильма " +
                            "должна быть положительной"
            );
            throw new ValidationException(
                    "Продолжительность фильма должна быть " +
                            "положительным числом"
            );
        }

        validateMpa(film.getMpa());
        validateGenres(film.getGenres());
    }

    private void validateMpa(Mpa mpa) {
        if (mpa == null) {
            log.warn("Ошибка валидации: рейтинг MPA не указан");
            throw new ValidationException(
                    "Рейтинг MPA не может быть пустым"
            );
        }

        if (mpa.getId() < 1 || mpa.getId() > 5) {
            log.warn("Ошибка валидации: некорректный рейтинг MPA");
            throw new ValidationException(
                    "Некорректный рейтинг MPA"
            );
        }
    }

    private void validateGenres(List<Genre> genres) {
        if (genres == null) {
            log.warn("Ошибка валидации: список жанров не указан");
            throw new ValidationException(
                    "Список жанров не может быть null"
            );
        }

        for (Genre genre : genres) {
            if (genre == null
                    || genre.getId() < 1
                    || genre.getId() > 6) {
                log.warn("Ошибка валидации: некорректный жанр");
                throw new ValidationException(
                        "Некорректный жанр"
                );
            }
        }
    }
}