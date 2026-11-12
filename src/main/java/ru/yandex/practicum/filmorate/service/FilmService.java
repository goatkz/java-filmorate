
package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.findAll();
    }

    public Film getFilmById(int id) {
        return filmStorage.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Фильм с id " + id + " не найден"));
    }

    public void addLike(int filmId, int userId) {
        Film film = getFilmById(filmId);

        userStorage.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("Пользователь с id " + userId + " не найден"));

        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }

        film.getLikes().add(userId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = getFilmById(filmId);

        userStorage.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("Пользователь с id " + userId + " не найден"));

        if (film.getLikes() != null) {
            film.getLikes().remove(userId);
        }
    }

    public Collection<Film> getPopularFilms(int count) {
        if (count < 1) {
            throw new ValidationException(
                    "Количество популярных фильмов должно быть положительным"
            );
        }

        List<Film> films = filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(
                        (Film film) -> film.getLikes() == null
                                ? 0
                                : film.getLikes().size()
                ).reversed())
                .limit(count)
                .toList();

        return films;
    }
}
