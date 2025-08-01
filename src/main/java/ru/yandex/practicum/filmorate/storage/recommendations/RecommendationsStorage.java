package ru.yandex.practicum.filmorate.storage.recommendations;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Интерфейс для доступа к данным рекомендаций фильмов.
 * Определяет методы для получения списка фильмов, которые пользователь лайкнул.
 */
public interface RecommendationsStorage {

    /**
     * Получить отображение пользователей и списков идентификаторов фильмов, которые пользователь лайкнул.
     *
     * @param userIds коллекция идентификаторов пользователя
     * @return карта, где ключ — идентификатор пользователя,
     * значение — список идентификаторов лайкнутых фильмов
     */
    Map<Long, List<Long>> getUsersFilms(Collection<Long> userIds);

    /**
     * Получить коллекцию идентификаторов фильмов, которые лайкнул конкретный пользователь.
     *
     * @param userId идентификатор пользователя
     * @return коллекция идентификаторов фильмов, лайкнутых пользователем;
     * пустая коллекция, если пользователь не лайкнул ни одного фильма
     */
    Collection<Long> getUserFilms(Long userId);
}
