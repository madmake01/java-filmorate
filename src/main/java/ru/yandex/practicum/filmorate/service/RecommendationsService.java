package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.recommendations.RecommendationsStorage;

import java.util.Set;
import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Сервис для получения рекомендаций фильмов для пользователя.
 * Использует фильтрацию на основе пересечения лайкнутых фильмов.
 */
@Service
@RequiredArgsConstructor
public class RecommendationsService {
    private final FilmService filmService;
    private final UserService userService;
    private final RecommendationsStorage storage;

    /**
     * Получить набор рекомендованных фильмов для пользователя.
     * Рекомендации формируются на основе пользователей, у которых максимальное пересечение
     * с текущим пользователем по лайкнутым фильмам.
     *
     * @param userId идентификатор пользователя, для которого нужны рекомендации
     * @return множество рекомендованных фильмов; пустое множество, если рекомендации отсутствуют
     */
    public Set<Film> getRecommendedFilms(Long userId) {
        if (userService.getUser(userId) == null) {
            return Collections.emptySet();
        }
        Map<Long, Collection<Long>> filmsOfUsers = new HashMap<>();
        Collection<User> users = userService.findAll();
        for (User user : users) {
            filmsOfUsers.put(user.getId(), storage.getUsersFilms(user.getId()));
        }

        Collection<Long> userFilms = filmsOfUsers.get(userId);
        if (userFilms == null || userFilms.isEmpty()) {
            return Collections.emptySet();
        }

        long maxMatches = 0;
        Set<Long> similarity = new HashSet<>();
        for (Long id : filmsOfUsers.keySet()) {
            if (Objects.equals(id, userId)) continue;

            long numberOfMatches = filmsOfUsers.get(id).stream()
                    .filter(userFilms::contains)
                    .count();

            if (numberOfMatches == maxMatches & numberOfMatches != 0) {
                similarity.add(id);
            }

            if (numberOfMatches > maxMatches) {
                maxMatches = numberOfMatches;
                similarity = new HashSet<>();
                similarity.add(id);
            }
        }

        if (maxMatches == 0) {
            return Collections.emptySet();
        } else return similarity.stream()
                .flatMap(idUser -> storage.getUsersFilms(idUser).stream())
                .filter(filmId -> !userFilms.contains(filmId))
                .map(filmService::getFilm)
                .collect(Collectors.toSet());
    }
}
