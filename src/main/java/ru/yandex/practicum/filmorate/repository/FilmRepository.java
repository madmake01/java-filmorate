package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class FilmRepository {
    private final Map<Long, Film> films = new HashMap<>();
    private long id = 0;

    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film persist(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    public Optional<Film> update(Film film) {
        return Optional.ofNullable(films.computeIfPresent(film.getId(), (k, v) -> film));
    }

    private long generateId() {
        return ++id;
    }
}
