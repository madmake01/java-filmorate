package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.SortDirectorFilms;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmDirectorsDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreDbStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmGenreDbStorage filmGenreDbStorage;
    private final DirectorService directorService;
    private final FilmDirectorsDbStorage filmDirectorsDbStorage;

    public Film getFilm(Long id) {
        return filmStorage.find(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Film with id '" + id + "' not found"));
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film save(Film film) {
        return withIntegrityHandling(() -> {
            Film saved = filmStorage.persist(film);
            Film normalized = normalizeFilmRelations(saved);
            saveFilmRelations(normalized);
            return normalized;
        });
    }

    public Film update(Film film) {
        return withIntegrityHandling(() -> {
            Film updated = filmStorage.update(film)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Film with id '" + film.getId() + "' not found"));
            Film normalized = normalizeFilmRelations(updated);
            deleteFilmRelations(normalized);
            saveFilmRelations(normalized);
            return normalized;
        });
    }

    public List<Film> findCommonFilms(Long userId, Long friendId) {
        return filmStorage.findCommonFilms(userId, friendId);
    }

    public List<Film> search(String query, Set<String> by) {
        String searchPattern = "%" + query.toLowerCase() + "%";
        boolean searchByTitle = by.contains("title");
        boolean searchByDirector = by.contains("director");

        if (searchByTitle && !searchByDirector) {
            return filmStorage.findByTitleLike(searchPattern);
        } else if (searchByDirector && !searchByTitle) {
            return filmStorage.findByDirectorLike(searchPattern);
        }
        return filmStorage.findByDirectorAndTitle(searchPattern);
    }

    public Collection<Film> getListDirectorFilms(long directorId, String sortBy) {
        directorService.getDirectorById(directorId);
        SortDirectorFilms sortOption = SortDirectorFilms.getSortByName(sortBy);
        return filmStorage.getListDirectorFilms(directorId, sortOption);
    }

    private Film withIntegrityHandling(Supplier<Film> operation) {
        try {
            return operation.get();
        } catch (DataIntegrityViolationException e) {
            throw new EntityNotFoundException("Related entity not found");
        }
    }

    private void deleteFilmRelations(Film film) {
        long filmId = film.getId();
        filmGenreDbStorage.deleteFilmGenresByFilmId(filmId);
        filmDirectorsDbStorage.removeConnectionFilmDirector(filmId);
    }

    private void saveFilmRelations(Film film) {
        long filmId = film.getId();

        Optional.ofNullable(film.getGenres())
                .filter(genres -> !genres.isEmpty())
                .ifPresent(genres -> filmGenreDbStorage.saveFilmGenres(filmId, genres));

        Optional.ofNullable(film.getDirectors())
                .filter(directors -> !directors.isEmpty())
                .ifPresent(directors -> filmDirectorsDbStorage.createConnectionFilmDirector(filmId, directors));

    }

    private Film normalizeFilmRelations(Film film) {
        List<Genre> normalizedGenres = Optional.ofNullable(film.getGenres())
                .map(genres -> genres.stream()
                        .filter(Objects::nonNull)
                        .distinct()
                        .sorted(Comparator.comparing(Genre::getId))
                        .toList())
                .orElse(List.of());
        film.setGenres(normalizedGenres);

        List<Director> normalizedDirectors = Optional.ofNullable(film.getDirectors())
                .map(directors -> directors.stream()
                        .filter(Objects::nonNull)
                        .distinct()
                        .sorted(Comparator.comparing(Director::getId))
                        .toList())
                .orElse(List.of());
        film.setDirectors(normalizedDirectors);
        return film;
    }
}
