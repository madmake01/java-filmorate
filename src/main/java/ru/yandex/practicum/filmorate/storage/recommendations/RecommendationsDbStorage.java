package ru.yandex.practicum.filmorate.storage.recommendations;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * Реализация {@link RecommendationsStorage}, использующая базу данных.
 * Выполняет запросы к таблице с лайками фильмов для получения данных.
 */
@Repository
@RequiredArgsConstructor
public class RecommendationsDbStorage implements RecommendationsStorage {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Получить коллекцию идентификаторов фильмов, которые пользователь лайкнул,
     * выполняя SQL-запрос к таблице film_likes.
     *
     * @param userId идентификатор пользователя
     * @return коллекция идентификаторов фильмов, лайкнутых пользователем
     */
    @Override
    public Collection<Long> getUsersFilms(Long userId) {
        String sql = "SELECT film_id FROM film_likes WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("film_id"), userId);
    }
}
