package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmDirectorsDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreDbStorage;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new EntityNotFoundException("Film with id '" + id + "' not found"));
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @Transactional
    public Film save(Film film) {
        validateReferencedEntities(film);
        return withRelations(() -> filmStorage.persist(film), film);
    }

    @Transactional
    public Film update(Film film) {
        validateReferencedEntities(film);
        Film updated = filmStorage.update(film)
                .orElseThrow(() -> new EntityNotFoundException("Film with id '" + film.getId() + "' not found"));
        return withRelations(() -> updated, film);
    }

    public List<Film> findCommonFilms(Long userId, Long friendId) {
        return filmStorage.findCommonFilms(userId, friendId);
    }

    public List<Film> search(String query, Set<String> by) {
        String w = "%" + query.toLowerCase() + "%";
        boolean t = by.contains("title"), d = by.contains("director");
        return t && !d ? filmStorage.findByTitleLike(w)
                : d && !t ? filmStorage.findByDirectorLike(w)
                : filmStorage.findByBoth(w);
    }

    public Collection<Film> getListDirectorFilms(long directorId, String sortBy) {
        directorService.getDirectorById(directorId);
        return filmStorage.getListDirectorFilms(directorId, SortDirectorFilms.getSortByName(sortBy));
    }

    private Film withRelations(Supplier<Film> op, Film film) {
        Film f = op.get();
        long id = f.getId();
        filmGenreDbStorage.deleteFilmGenresByFilmId(id);
        Optional.ofNullable(film.getGenres()).filter(g -> !g.isEmpty())
                .ifPresent(g -> filmGenreDbStorage.saveFilmGenres(id, g));
        filmDirectorsDbStorage.removeConnectionFilmDirector(id);
        Optional.ofNullable(film.getDirectors()).filter(d -> !d.isEmpty())
                .ifPresent(d -> filmDirectorsDbStorage.createConnectionFilmDirector(id, d));
        return f;
    }

    private void validateReferencedEntities(Film film) {
        Set<Long> genres = genreService.findAll().stream().map(Genre::getId).collect(Collectors.toSet());
        Set<Long> fG = Optional.ofNullable(film.getGenres()).orElse(List.of()).stream().map(Genre::getId).collect(Collectors.toSet());
        if (!genres.containsAll(fG)) throw new EntityNotFoundException("One or more genres do not exist.");

        Optional.ofNullable(film.getRating()).map(Rating::getId).ifPresent(r -> {
            if (ratingService.findAll().stream().map(Rating::getId).noneMatch(id -> id.equals(r)))
                throw new EntityNotFoundException("Rating with id " + r + " not found");
        });

        Set<Long> dirs = directorService.getListDirectors().stream().map(Director::getId).collect(Collectors.toSet());
        Set<Long> fD = Optional.ofNullable(film.getDirectors()).orElse(List.of()).stream().map(Director::getId).collect(Collectors.toSet());
        if (!dirs.containsAll(fD)) throw new EntityNotFoundException("One or more directors do not exist.");
    }
}
