package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.sql.LikeSql;

import java.util.ArrayList;
import java.util.List;

import static ru.yandex.practicum.filmorate.storage.sql.LikeSql.BASE_QUERY_TO_GET_POPULAR_FILMS;

@Primary
@Repository
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Film> filmRowMapper;

    @Override
    public void addLike(Like like) {
        jdbcTemplate.update(LikeSql.INSERT_LIKE, like.userId(), like.filmId());
    }

    @Override
    public void removeLike(Like like) {
        jdbcTemplate.update(LikeSql.DELETE_LIKE, like.userId(), like.filmId());
    }

    @Override
    public List<Film> getPopularFilms(int count, Long genreId, Integer year) {
        String joinClause = (genreId != null)
                ? "JOIN film_genres fgf ON fgf.film_id = f.film_id AND fgf.genre_id = ?"
                : "";

        String whereClause = (year != null)
                ? "EXTRACT(YEAR FROM f.release_date) = ?"
                : "1=1";

        List<Object> params = new ArrayList<>();
        if (genreId != null) {
            params.add(genreId);
        }
        if (year != null) {
            params.add(year);
        }
        params.add(count);

        String sql = String.format(BASE_QUERY_TO_GET_POPULAR_FILMS, joinClause, whereClause);

        return jdbcTemplate.query(sql, filmRowMapper, params.toArray());
    }

}

