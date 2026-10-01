package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Optional;

public interface Storage<T> {

    T create(T item);

    Optional<T> update(T item);

    List<T> findAll();
}