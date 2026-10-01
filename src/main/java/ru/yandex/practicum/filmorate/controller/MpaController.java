package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final List<Mpa> ratings = List.of(
            createMpa(1, "G"),
            createMpa(2, "PG"),
            createMpa(3, "PG-13"),
            createMpa(4, "R"),
            createMpa(5, "NC-17")
    );

    @GetMapping
    public List<Mpa> getRatings() {
        return ratings;
    }

    @GetMapping("/{id}")
    public Mpa getRatingById(@PathVariable int id) {
        return ratings.stream()
                .filter(rating -> rating.getId() == id)
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException("Рейтинг с таким id не найден"));
    }

    private static Mpa createMpa(int id, String name) {
        Mpa mpa = new Mpa();
        mpa.setId(id);
        mpa.setName(name);
        return mpa;
    }
}