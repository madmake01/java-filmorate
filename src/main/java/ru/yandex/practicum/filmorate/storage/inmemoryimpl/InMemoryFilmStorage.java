package ru.yandex.practicum.filmorate.storage.inmemoryimpl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortDirectorFilms;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

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
        return films.values().stream()
                .filter(f -> f.getDirectors().stream().anyMatch(d -> Objects.equals(d.getId(), directorId)))
                .sorted(sortDirectorFilms == SortDirectorFilms.LIKES
                        ? Comparator.comparingInt((Film f) -> f.getLikedUsers().size()).reversed()
                        : Comparator.comparing(Film::getReleaseDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> findCommonFilms(Long firstUser, Long secondUser) {
        Set<Long> first = films.values().stream()
                .filter(f -> f.getLikedUsers().contains(firstUser))
                .map(Film::getId)
                .collect(Collectors.toSet());
        Set<Long> second = films.values().stream()
                .filter(f -> f.getLikedUsers().contains(secondUser))
                .map(Film::getId)
                .collect(Collectors.toSet());
        first.retainAll(second);
        return first.stream()
                .map(films::get)
                .sorted(Comparator.comparingInt((Film f) -> f.getLikedUsers().size()).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> search(String query, List<String> by) {
        String lower = query.toLowerCase();
        boolean byTitle    = by.contains("title");
        boolean byDirector = by.contains("director");
        return films.values().stream()
                .filter(f -> (byTitle && f.getName().toLowerCase().contains(lower)) ||
                        (byDirector && f.getDirectors().stream()
                                .anyMatch(d -> d.getName().toLowerCase().contains(lower))))
                .sorted(Comparator.comparingInt((Film f) -> f.getLikedUsers().size()).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = find(filmId)
                .orElseThrow(() -> new EntityNotFoundException("Film with id " + filmId + " not found"));
        film.getLikedUsers().add(userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = find(filmId)
                .orElseThrow(() -> new EntityNotFoundException("Film with id " + filmId + " not found"));
        film.getLikedUsers().remove(userId);
    }

    private long generateId() {
        return ++id;
    }
}
