package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {

    private final List<Genre> genres = List.of(
            createGenre(1, "Комедия"),
            createGenre(2, "Драма"),
            createGenre(3, "Мультфильм"),
            createGenre(4, "Триллер"),
            createGenre(5, "Документальный"),
            createGenre(6, "Боевик")
    );

    @GetMapping
    public List<Genre> getGenres() {
        return genres;
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        return genres.stream()
                .filter(genre -> genre.getId() == id)
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException("Жанр с таким id не найден"));
    }

    private static Genre createGenre(int id, String name) {
        Genre genre = new Genre();
        genre.setId(id);
        genre.setName(name);
        return genre;
    }
}