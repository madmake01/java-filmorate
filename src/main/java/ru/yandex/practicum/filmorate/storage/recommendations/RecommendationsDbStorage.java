package ru.yandex.practicum.filmorate.storage.recommendations;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Реализация {@link RecommendationsStorage}, использующая базу данных.
 * Выполняет запросы к таблице с лайками фильмов для получения данных.
 */
@Repository
@RequiredArgsConstructor
public class RecommendationsDbStorage implements RecommendationsStorage {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Получить отображение пользователей и списков идентификаторов фильмов,
     * которые пользователь лайкнул, выполняя SQL-запрос к таблице film_likes.
     *
     * @param userIds коллекция идентификаторов пользователя
     * @return карта, где ключ — идентификатор пользователя,
     * значение — список идентификаторов лайкнутых фильмов
     */
    @Override
    public Map<Long, List<Long>> getUsersFilms(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String inSql = userIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        String sql = "SELECT user_id, film_id FROM film_likes WHERE user_id IN (" + inSql + ")";

        List<Long> userIdList = new ArrayList<>(userIds);

        List<Like> userFilms = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Like(
                        rs.getLong("user_id"),
                        rs.getLong("film_id")
                ),
                userIdList.toArray()
        );

        return userFilms.stream()
                .collect(Collectors.groupingBy(
                        Like::userId,
                        Collectors.mapping(Like::filmId, Collectors.toList())
                ));
    }

    /**
     * Получить коллекцию идентификаторов фильмов, которые лайкнул конкретный пользователь.
     *
     * @param userId идентификатор пользователя
     * @return коллекция идентификаторов фильмов, лайкнутых пользователем
     */
    @Override
    public Collection<Long> getUserFilms(Long userId) {
        String sql = "SELECT film_id FROM film_likes WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("film_id"), userId);
    }
}
