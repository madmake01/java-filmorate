package ru.yandex.practicum.filmorate.storage.recommendations;

import java.util.Collection;

/**
 * Интерфейс для доступа к данным рекомендаций фильмов.
 * Определяет методы для получения списка фильмов, которые пользователь лайкнул.
 */
public interface RecommendationsStorage {

    /**
     * Получить коллекцию идентификаторов фильмов, которые пользователь лайкнул.
     *
     * @param userId идентификатор пользователя
     * @return коллекция идентификаторов фильмов
     */
    Collection<Long> getUsersFilms(Long userId);
}
