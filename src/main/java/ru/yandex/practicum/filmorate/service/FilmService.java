package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikeStorage likeStorage;

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

    public Film update(Film film) {
        return filmStorage.update(film)
                .orElseThrow(() -> new EntityNotFoundException("Film with id '%d' not found".formatted(film.getId())));
    }


    public void addLike(Long userId, Long filmId) {
        validateUserAndFilmExistence(userId, filmId);
        likeStorage.addLike(new Like(userId, filmId));
    }

    public void removeLike(Long userId, Long filmId) {
        validateUserAndFilmExistence(userId, filmId);
        likeStorage.removeLike(new Like(userId, filmId));
    }

    public List<Film> findMostLikedFilms(int amount) {
        return likeStorage.getLikeCountsByFilmId().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(amount)
                .map(entry -> getFilm(entry.getKey()))
                .toList();
    }

    private void validateUserAndFilmExistence(Long userId, Long filmId) {
        userService.getUser(userId);
        getFilm(filmId);
    }
}
