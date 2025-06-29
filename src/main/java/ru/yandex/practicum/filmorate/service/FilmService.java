package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreDbStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmGenreDbStorage filmGenreDbStorage;
    private final GenreService genreService;
    private final RatingService ratingService;

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

        List<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            filmGenreDbStorage.saveFilmGenres(savedFilm.getId(), genres);
        }
        return savedFilm;
    }

    @Transactional
    public Film update(Film film) {
        validateReferencedEntities(film);
        Film updatedFilm = filmStorage.update(film)
                .orElseThrow(() -> new EntityNotFoundException("Film with id '%d' not found".formatted(film.getId())));

        filmGenreDbStorage.deleteFilmGenresByFilmId(updatedFilm.getId());

        List<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            filmGenreDbStorage.saveFilmGenres(updatedFilm.getId(), genres);
        }
        return updatedFilm;
    }

    private void validateReferencedEntities(Film film) {
        Set<Long> existingGenreIds = genreService.findAll().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        Set<Long> genreIdsFromFilm = film.getGenres() == null
                ? Set.of()
                : film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        if (!existingGenreIds.containsAll(genreIdsFromFilm)) {
            throw new EntityNotFoundException("One or more genres do not exist.");
        }

        if (film.getRating() != null && film.getRating().getId() != null) {
            Set<Long> existingRatingIds = ratingService.findAll().stream()
                    .map(Rating::getId)
                    .collect(Collectors.toSet());

            if (!existingRatingIds.contains(film.getRating().getId())) {
                throw new EntityNotFoundException("Rating with id %d not found".formatted(film.getRating().getId()));
            }
        }
    }

}
