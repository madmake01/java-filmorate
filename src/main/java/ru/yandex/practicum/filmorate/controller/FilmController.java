package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.stream.Collectors;
import java.util.Collection;
import java.util.List;
import java.util.Arrays;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) {
        return filmService.getFilm(id);
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.findAll();
    }

    @GetMapping("/common")
    public List<Film> getCommonLikedFilms(
            @RequestParam Long userId,
            @RequestParam Long friendId) {
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
            @PathVariable long directorId,
            @RequestParam(defaultValue = "likes") String sortBy) {

        return filmService.getListDirectorFilms(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(
            @RequestParam String query,
            @RequestParam(defaultValue = "title") String by
    ) {
        Set<String> criteria = Arrays.stream(by.split(","))
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        return filmService.search(query, criteria);
    }
}
