package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.LikeService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
@Validated
public class LikeController {
    public static final String DEFAULT_FILM_LIST_SIZE = "10";
    private final LikeService likeService;

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable Long id, @PathVariable Long userId) {
        likeService.addLike(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        likeService.removeLike(userId, id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @Positive @RequestParam(required = false, defaultValue = DEFAULT_FILM_LIST_SIZE) Integer count,
            @Positive @RequestParam(required = false) Long genreId,
            @Positive @RequestParam(required = false) Integer year) {
        if (count != null && genreId != null) {
            return likeService.getPopularFilmsWithCountAndGenreId(count, genreId);
        }

        if (count != null && year != null) {
            return likeService.getPopularFilmsWithCountAndYear(count, year);
        }

        if (genreId != null && year != null) {
            return likeService.getPopularFilmsWithGenreIdAndYear(genreId, year);
        }

        if (genreId != null) {
            return likeService.getPopularFilmsWithGenreId(genreId);
        }

        if (year != null) {
            return likeService.getPopularFilmsWithYear(year);
        }

        return count != null ? likeService.findMostLikedFilms(count) : List.of();
    }
}
