package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.recommendations.RecommendationsStorage;

import java.util.Set;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.Map;
import java.util.HashSet;
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
        if (!userExists(userId)) {
            return Collections.emptySet();
        }

        Collection<Long> userFilms = storage.getUserFilms(userId);
        if (userFilms == null || userFilms.isEmpty()) {
            return Collections.emptySet();
        }

        List<Long> otherUserIds = getOtherUserIds(userId);
        if (otherUserIds.isEmpty()) {
            return Collections.emptySet();
        }

        Map<Long, List<Long>> filmsOfUsers = getFilmsOfUsers(otherUserIds);
        Set<Long> similarUsers = findSimilarUsers(userFilms, filmsOfUsers);

        if (similarUsers.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Long> recommendedFilmIds = recommendedFilmIds(similarUsers, filmsOfUsers, userFilms);
        if (recommendedFilmIds.isEmpty()) {
            return Collections.emptySet();
        }

        return getFilmsByIds(recommendedFilmIds);
    }

    /**
     * Проверяет, существует ли пользователь с заданным идентификатором.
     *
     * @param userId идентификатор пользователя
     * @return true, если пользователь существует; false — в противном случае
     */
    private boolean userExists(Long userId) {
        try {
            userService.getUser(userId);
            return true;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }

    /**
     * Получает список идентификаторов всех пользователей, кроме указанного.
     *
     * @param userId идентификатор исключаемого пользователя
     * @return список идентификаторов других пользователей
     */
    private List<Long> getOtherUserIds(Long userId) {
        return userService.findAll().stream()
                .map(User::getId)
                .filter(id -> !id.equals(userId))
                .collect(Collectors.toList());
    }

    /**
     * Получает отображение пользователей и списков их лайкнутых фильмов.
     *
     * @param userIds коллекция идентификаторов пользователей
     * @return карта: ключ — идентификатор пользователя, значение — список идентификаторов фильмов
     */
    private Map<Long, List<Long>> getFilmsOfUsers(Collection<Long> userIds) {
        return storage.getUsersFilms(userIds);
    }

    /**
     * Находит пользователей с максимальным количеством пересечений лайкнутых фильмов с текущим пользователем.
     *
     * @param userFilms список фильмов текущего пользователя
     * @param filmsOfUsers карта пользователей и их фильмов
     * @return множество идентификаторов пользователей с максимальным пересечением
     */
    private Set<Long> findSimilarUsers(Collection<Long> userFilms, Map<Long, List<Long>> filmsOfUsers) {
        long maxMatches = 0;
        Set<Long> similarUsers = new HashSet<>();

        for (Map.Entry<Long, List<Long>> entry : filmsOfUsers.entrySet()) {
            Collection<Long> otherUserFilms = entry.getValue();
            if (otherUserFilms == null || otherUserFilms.isEmpty()) continue;

            long matches = otherUserFilms.stream()
                    .filter(userFilms::contains)
                    .count();

            if (matches == 0) continue;

            if (matches > maxMatches) {
                maxMatches = matches;
                similarUsers.clear();
                similarUsers.add(entry.getKey());
            } else if (matches == maxMatches) {
                similarUsers.add(entry.getKey());
            }
        }

        return similarUsers;
    }

    /**
     * Формирует набор идентификаторов фильмов для рекомендации, исключая фильмы, которые пользователь уже лайкнул.
     *
     * @param similarUsers множество идентификаторов похожих пользователей
     * @param filmsOfUsers карта пользователей и их фильмов
     * @param userFilms коллекция фильмов текущего пользователя
     * @return множество идентификаторов фильмов для рекомендации
     */
    private Set<Long> recommendedFilmIds(Set<Long> similarUsers,
                                         Map<Long, List<Long>> filmsOfUsers,
                                         Collection<Long> userFilms) {
        return similarUsers.stream()
                .flatMap(id -> filmsOfUsers.getOrDefault(id, Collections.emptyList()).stream())
                .filter(filmId -> !userFilms.contains(filmId))
                .collect(Collectors.toSet());
    }

    /**
     * Получает множество объектов {@link Film} по их идентификаторам.
     *
     * @param filmIds множество идентификаторов фильмов
     * @return множество объектов фильмов
     */
    private Set<Film> getFilmsByIds(Set<Long> filmIds) {
        Set<Film> films = new HashSet<>();
        for (Long id : filmIds) {
            films.add(filmService.getFilm(id));
        }
        return films;
    }
}
