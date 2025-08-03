package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
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
    private final ResultSetExtractor<List<Film>> filmsExtractor;

    public LikeDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("filmRowMapper") RowMapper<Film> filmRowMapper, ResultSetExtractor<List<Film>> filmsExtractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
        this.filmsExtractor = filmsExtractor;
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

    @Override
    public List<Film> getPopularFilmsWithCountAndGenreId(int count, long genreId) {
        final String queryToGetPopularFilmsWithCountAndGenreId = LikeSql.BASE_QUERY_TO_GET_POPULAR_FILMS + """
                WHERE g.genre_id = ?
                GROUP BY f.film_id
                ORDER BY like_count DESC
                LIMIT ?
                """;
        return jdbcTemplate.query(queryToGetPopularFilmsWithCountAndGenreId, filmsExtractor, genreId, count);
    }

    @Override
    public List<Film> getPopularFilmsWithCountAndYear(int count, int year) {
        final String queryToGetPopularFilmsWithCountAndYear = LikeSql.BASE_QUERY_TO_GET_POPULAR_FILMS + """
                WHERE EXTRACT(YEAR FROM  f.release_date) = ?
                GROUP BY f.film_id, g.genre_id
                ORDER BY like_count DESC
                LIMIT ?
                """;
        return jdbcTemplate.query(queryToGetPopularFilmsWithCountAndYear, filmsExtractor, year, count);
    }

    @Override
    public List<Film> getPopularFilmsWithGenreId(long genreId) {
        final String queryToGetPopularFilmsWithGenreId = LikeSql.BASE_QUERY_TO_GET_POPULAR_FILMS + """
                WHERE g.genre_id = ?
                GROUP BY f.film_id, g.genre_id
                ORDER BY like_count DESC
                """;
        return jdbcTemplate.query(queryToGetPopularFilmsWithGenreId, filmsExtractor, genreId);
    }

    @Override
    public List<Film> getPopularFilmsWithYear(int year) {
        final String queryToGetPopularFilmsWithYear = LikeSql.BASE_QUERY_TO_GET_POPULAR_FILMS + """
                WHERE EXTRACT(YEAR FROM  f.release_date) = ?
                GROUP BY f.film_id, g.genre_id
                ORDER BY like_count DESC
                """;
        return jdbcTemplate.query(queryToGetPopularFilmsWithYear, filmsExtractor, year);
    }

    @Override
    public List<Film> getPopularFilmsWithGenreIdAndYear(long genreId, int year) {
        final String queryToGetPopularFilmsWithGenreIdAndYear = LikeSql.BASE_QUERY_TO_GET_POPULAR_FILMS + """
                WHERE g.genre_id = ? AND EXTRACT(YEAR FROM  f.release_date) = ?
                GROUP BY f.film_id, g.genre_id
                ORDER BY like_count DESC
                """;
        return jdbcTemplate.query(queryToGetPopularFilmsWithGenreIdAndYear, filmsExtractor, genreId, year);
    }
}

