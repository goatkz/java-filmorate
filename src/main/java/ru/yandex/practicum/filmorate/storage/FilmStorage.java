package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FilmStorage implements Storage<Film> {

    private final List<Film> films = new ArrayList<>();
    private int nextId = 1;

    @Override
    public Film create(Film film) {
        film.setId(nextId++);
        films.add(film);

        return film;
    }

    @Override
    public Optional<Film> update(Film film) {
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId() == film.getId()) {
                films.set(i, film);
                return Optional.of(film);
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Film> findAll() {
        return films;
    }
}