package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreDbStorage;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmGenreDbStorage filmGenreDbStorage;
    private final ConstraintExceptionHandler constraintExceptionHandler;

    public Film getFilm(Long id) {
        return filmStorage.find(id)
                .orElseThrow(() -> new EntityNotFoundException("Film with id '%d' not found".formatted(id)));
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @Transactional
    public Film save(Film film) {
        return constraintExceptionHandler.handleForeignKeyViolation(() -> {
            Film savedFilm = filmStorage.persist(film);
            List<Genre> genres = film.getGenres();
            if (genres != null && !genres.isEmpty()) {
                filmGenreDbStorage.saveFilmGenres(savedFilm.getId(), genres);
            }
            return savedFilm;
        }, "Referenced entity does not exist.");
    }

    @Transactional
    public Film update(Film film) {
        return constraintExceptionHandler.handleForeignKeyViolation(() -> {
            Film updatedFilm = filmStorage.update(film)
                    .orElseThrow(() -> new EntityNotFoundException("Film with id '%d' not found".formatted(film.getId())));

            filmGenreDbStorage.deleteFilmGenresByFilmId(updatedFilm.getId());
            List<Genre> genres = film.getGenres();
            if (genres != null && !genres.isEmpty()) {
                filmGenreDbStorage.saveFilmGenres(updatedFilm.getId(), genres);
            }
            return updatedFilm;
        }, "Referenced entity does not exist.");
    }
}
