package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.SortDirectorFilms;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmDirectorsDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreDbStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
                .orElseThrow(() -> new EntityNotFoundException(
                        "Film with id '" + id + "' not found"));
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
        Film updatedFilm = filmStorage.update(film)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Film with id '" + film.getId() + "' not found"));
        return withRelations(() -> updatedFilm, film);
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
        return filmStorage.findByBoth(searchPattern);
    }

    public Collection<Film> getListDirectorFilms(long directorId, String sortBy) {
        directorService.getDirectorById(directorId);
        SortDirectorFilms sortOption = SortDirectorFilms.getSortByName(sortBy);
        return filmStorage.getListDirectorFilms(directorId, sortOption);
    }

    private Film withRelations(Supplier<Film> filmSupplier, Film inputFilm) {
        Film persistedFilm = filmSupplier.get();
        long filmId = persistedFilm.getId();

        // Обновление жанров
        filmGenreDbStorage.deleteFilmGenresByFilmId(filmId);
        Optional.ofNullable(inputFilm.getGenres())
                .filter(genres -> !genres.isEmpty())
                .ifPresent(genres -> filmGenreDbStorage.saveFilmGenres(filmId, genres));

        // Обновление режиссёров
        filmDirectorsDbStorage.removeConnectionFilmDirector(filmId);
        Optional.ofNullable(inputFilm.getDirectors())
                .filter(directors -> !directors.isEmpty())
                .ifPresent(directors -> filmDirectorsDbStorage.createConnectionFilmDirector(filmId, directors));

        return persistedFilm;
    }

    private void validateReferencedEntities(Film film) {
        // Проверка жанров
        Set<Long> availableGenreIds = genreService.findAll().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        Set<Long> filmGenreIds = Optional.ofNullable(film.getGenres())
                .orElse(List.of()).stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        if (!availableGenreIds.containsAll(filmGenreIds)) {
            throw new EntityNotFoundException("One or more genres do not exist.");
        }

        // Проверка рейтинга
        Optional.ofNullable(film.getRating())
                .map(Rating::getId)
                .ifPresent(ratingId -> {
                    boolean exists = ratingService.findAll().stream()
                            .map(Rating::getId)
                            .anyMatch(id -> id.equals(ratingId));
                    if (!exists) {
                        throw new EntityNotFoundException("Rating with id " + ratingId + " not found");
                    }
                });

        // Проверка режиссёров
        Set<Long> availableDirectorIds = directorService.getListDirectors().stream()
                .map(Director::getId)
                .collect(Collectors.toSet());
        Set<Long> filmDirectorIds = Optional.ofNullable(film.getDirectors())
                .orElse(List.of()).stream()
                .map(Director::getId)
                .collect(Collectors.toSet());
        if (!availableDirectorIds.containsAll(filmDirectorIds)) {
            throw new EntityNotFoundException("One or more directors do not exist.");
        }
    }
}
