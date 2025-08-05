package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.RecommendationsService;

import java.util.Set;

/**
 * REST-контроллер для получения рекомендаций фильмов для пользователя.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{id}/recommendations")
public class RecommendationsController {
    private final RecommendationsService recommendationsService;

    /**
     * Обработчик GET-запроса для получения рекомендаций фильмов для пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя из пути запроса
     * @return множество рекомендованных фильмов
     */
    @GetMapping
    public Set<Film> getRecommendedFilms(@PathVariable("id") Long userId) {
        return recommendationsService.getRecommendedFilms(userId);
    }
}
