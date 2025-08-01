package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmDirectorsDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreDbStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Validated
@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmGenreDbStorage filmGenreDbStorage;
    private final GenreService genreService;
    private final RatingService ratingService;
    private final DirectorService directorService;
    private final FilmDirectorsDbStorage filmDirectorsDbStorage;

    public Film getFilm(Long id) {
        return filmStorage.find(id)
                .orElseThrow(() -> new EntityNotFoundException("Film with id '%d' not found".formatted(id)));
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @Transactional
    public Film save(Film film) {
        validateReferencedEntities(film);
        Film savedFilm = filmStorage.persist(film);

        var genres    = film.getGenres();
        var directors = film.getDirectors();
        if (genres != null && !genres.isEmpty()) {
            filmGenreDbStorage.saveFilmGenres(savedFilm.getId(), genres);
        }
        if (directors != null && !directors.isEmpty()) {
            filmDirectorsDbStorage.createConnectionFilmDirector(savedFilm.getId(), directors);
        }
        return savedFilm;
    }

    @Transactional
    public Film update(Film film) {
        validateReferencedEntities(film);
        var updatedFilm = filmStorage.update(film)
                .orElseThrow(() -> new EntityNotFoundException("Film with id '%d' not found".formatted(film.getId())));

        filmGenreDbStorage.deleteFilmGenresByFilmId(updatedFilm.getId());
        var genres    = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            filmGenreDbStorage.saveFilmGenres(updatedFilm.getId(), genres);
        }

        filmDirectorsDbStorage.removeConnectionFilmDirector(updatedFilm.getId());
        var directors = film.getDirectors();
        if (directors != null && !directors.isEmpty()) {
            filmDirectorsDbStorage.createConnectionFilmDirector(updatedFilm.getId(), directors);
        }
        return updatedFilm;
    }

    public List<Film> findCommonFilms(Long userId, Long friendId) {
        return filmStorage.findCommonFilms(userId, friendId);
    }

    public Collection<Film> getListDirectorFilms(long directorId, String sortBy) {
        directorService.getDirectorById(directorId);
        return filmStorage.getListDirectorFilms(directorId, SortDirectorFilms.getSortByName(sortBy));
    }

    @Transactional(readOnly = true)
    public List<Film> search(String query, List<String> by) {
        if (query == null || query.isBlank()) {
            throw new ValidationException("Параметр 'query' не должен быть пустым");
        }
        if (by == null || by.isEmpty()) {
            throw new ValidationException("Параметр 'by' должен содержать хотя бы одно значение");
        }
        List<String> allowed = List.of("title", "director");
        for (String field : by) {
            if (!allowed.contains(field)) {
                throw new ValidationException("Недопустимое значение в 'by': " + field);   //  сообщение для невалидного поля
            }
        }
        return filmStorage.search(query, by);
    }

    @Transactional
    public void addLike(Long filmId, Long userId) {
        filmStorage.addLike(filmId, userId);
    }

    @Transactional
    public void removeLike(Long filmId, Long userId) {
        filmStorage.removeLike(filmId, userId);
    }

    private void validateReferencedEntities(Film film) {
        var allGenreIds = genreService.findAll().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        var filmGenres  = film.getGenres() == null
                ? Set.<Long>of()
                : film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());
        if (!allGenreIds.containsAll(filmGenres)) {
            throw new EntityNotFoundException("One or more genres do not exist.");
        }

        if (film.getRating() != null && film.getRating().getId() != null) {
            var allRatingIds = ratingService.findAll().stream()
                    .map(Rating::getId).collect(Collectors.toSet());
            if (!allRatingIds.contains(film.getRating().getId())) {
                throw new EntityNotFoundException(
                        "Rating with id %d not found".formatted(film.getRating().getId())
                );
            }
        }

        var filmDirectorIds = film.getDirectors() == null
                ? Set.<Long>of()
                : film.getDirectors().stream().map(Director::getId).collect(Collectors.toSet());
        var allDirectorIds  = directorService.getListDirectors().stream()
                .map(Director::getId).collect(Collectors.toSet());
        if (!allDirectorIds.containsAll(filmDirectorIds)) {
            throw new EntityNotFoundException("One or more directors do not exist.");
        }
    }
}
