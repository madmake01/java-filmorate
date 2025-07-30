package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.sql.LikeSql;

import java.util.List;

@Primary
@Repository
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Film> filmRowMapper;

    public LikeDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("filmRowMapper") RowMapper<Film> filmRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
    }

    @Override
    public void addLike(Like like) {
        jdbcTemplate.update(LikeSql.INSERT_LIKE, like.userId(), like.filmId());
    }

    @Override
    public void removeLike(Like like) {
        jdbcTemplate.update(LikeSql.DELETE_LIKE, like.userId(), like.filmId());
    }

    @Override
    public List<Film> findTopFilmsByLikes(int amount) {
        return jdbcTemplate.query(LikeSql.SELECT_TOP_FILMS_BY_LIKES, filmRowMapper, amount);
    }
}

