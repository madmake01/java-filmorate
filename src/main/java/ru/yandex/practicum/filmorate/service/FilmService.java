package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmRepository filmRepository;

    public Collection<Film> findAll() {
        return filmRepository.findAll();
    }

    public Film save(Film film) {
        return filmRepository.persist(film);
    }

    public Optional<Film> update(Film film) {
        return filmRepository.update(film);
    }
}
