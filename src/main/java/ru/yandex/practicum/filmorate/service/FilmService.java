package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public Film getFilm(Long id) {
        return filmStorage.find(id)
                .orElseThrow(() -> new EntityNotFoundException("Film with id '%d' not found".formatted(id)));
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film save(Film film) {
        return filmStorage.persist(film);
    }

    public Optional<Film> update(Film film) {
        return filmStorage.update(film);
    }
}
