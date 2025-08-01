package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;
import java.util.Arrays;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable @Positive Long id) {
        return filmService.getFilm(id);
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.findAll();
    }

    @GetMapping("/common")
    public List<Film> getCommonLikedFilms(
            @RequestParam @Positive Long userId,
            @RequestParam @Positive Long friendId) {
        return filmService.findCommonFilms(userId, friendId);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.save(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getListDirectorFilms(
            @PathVariable @Positive long directorId,
            @RequestParam(defaultValue = "likes") String sortBy) {
        return filmService.getListDirectorFilms(directorId, sortBy);
    }

    @GetMapping("/search")
    public Collection<Film> searchFilms(
            @RequestParam(name = "query") @NotBlank String query,    // ► проверяем, что не пусто
            @RequestParam(name = "by")    @NotBlank String by        // ► проверяем, что не пусто
    ) {
        var byList = Arrays.stream(by.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();
        return filmService.search(query, byList);
    }

    // Добавление лайка фильму
    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(
            @PathVariable @Positive Long filmId,
            @PathVariable @Positive Long userId
    ) {
        filmService.addLike(filmId, userId);
    }

    // Удаление лайка у фильма
    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(
            @PathVariable @Positive Long filmId,
            @PathVariable @Positive Long userId
    ) {
        filmService.removeLike(filmId, userId);
    }
}
