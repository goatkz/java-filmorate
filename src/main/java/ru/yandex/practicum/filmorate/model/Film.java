package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class Film {

    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;

    private Set<Integer> likes = new HashSet<>();

    // Жанры с гарантированным сохранением порядка JSON
    @JsonDeserialize(using = GenreSetDeserializer.class)
    private Set<Genre> genres = new LinkedHashSet<>();

    // Возрастной рейтинг
    private Mpa mpa;
}