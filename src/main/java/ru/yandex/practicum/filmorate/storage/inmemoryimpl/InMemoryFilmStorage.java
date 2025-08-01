package ru.yandex.practicum.filmorate.storage.inmemoryimpl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortDirectorFilms;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long id = 0;

    @Override
    public Optional<Film> find(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film persist(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> update(Film film) {
        return Optional.ofNullable(films.computeIfPresent(film.getId(), (k, v) -> film));
    }

    @Override
    public Collection<Film> getListDirectorFilms(long directorId, SortDirectorFilms sortDirectorFilms) {
        return List.of();
    }

    @Override
    public List<Film> findCommonFilms(Long firstUser, Long secondUser) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private long generateId() {
        return ++id;
    }
}
