package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Optional<Film> find(Long id);

    Collection<Film> findAll();

    Film persist(Film film);

    Optional<Film> update(Film film);

    List<Film> findCommonFilms(Long firstUser, Long secondUser);
}
