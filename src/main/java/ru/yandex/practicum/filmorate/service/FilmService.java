package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;

    public FilmService(
            FilmStorage filmStorage,
            UserStorage userStorage,
            GenreDbStorage genreStorage,
            MpaDbStorage mpaStorage
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public Film addFilm(Film film) {
        validateGenresAndMpa(film);
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        validateGenresAndMpa(film);
        return filmStorage.update(film);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.findAll();
    }

    public Film getFilmById(int id) {
        return filmStorage.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Фильм с id " + id + " не найден"
                        ));
    }

    public void addLike(int filmId, int userId) {
        Film film = getFilmById(filmId);

        userStorage.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Пользователь с id " + userId + " не найден"
                        ));

        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }

        film.getLikes().add(userId);

        filmStorage.update(film);
    }

    public void removeLike(int filmId, int userId) {
        Film film = getFilmById(filmId);

        userStorage.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Пользователь с id " + userId + " не найден"
                        ));

        if (film.getLikes() != null) {
            film.getLikes().remove(userId);
        }

        filmStorage.update(film);
    }

    public Collection<Film> getPopularFilms(int count) {
        if (count < 1) {
            throw new ValidationException(
                    "Количество популярных фильмов должно быть положительным"
            );
        }

        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(
                        (Film film) -> film.getLikes() == null
                                ? 0
                                : film.getLikes().size()
                ).reversed())
                .limit(count)
                .toList();
    }

    private void validateGenresAndMpa(Film film) {
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (genre == null) {
                    throw new ValidationException(
                            "Жанр фильма не может быть пустым"
                    );
                }

                genreStorage.findById(genre.getId())
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Жанр с id " + genre.getId()
                                                + " не найден"
                                ));
            }
        }

        if (film.getMpa() != null) {
            mpaStorage.findById(film.getMpa().getId())
                    .orElseThrow(() ->
                            new NotFoundException(
                                    "Рейтинг MPA с id "
                                            + film.getMpa().getId()
                                            + " не найден"
                            ));
        }
    }
}