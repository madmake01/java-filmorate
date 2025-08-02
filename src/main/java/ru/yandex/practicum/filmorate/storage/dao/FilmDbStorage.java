package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortDirectorFilms;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.sql.FilmSql;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Primary
@Repository

public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final ResultSetExtractor<Film> filmExtractor;
    private final ResultSetExtractor<List<Film>> filmsExtractor;
    private final RowMapper<Film> filmRowMapper;

    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         ResultSetExtractor<Film> filmExtractor,
                         ResultSetExtractor<List<Film>> filmsExtractor,
                         @Qualifier("filmRowMapperWithDetails") RowMapper<Film> filmRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmExtractor = filmExtractor;
        this.filmsExtractor = filmsExtractor;
        this.filmRowMapper = filmRowMapper;
    }

    public Optional<Film> find(Long id) {
        return Optional.ofNullable(
                jdbcTemplate.query(FilmSql.FIND_FILM_SQL, filmExtractor, id)
        );
    }

    public Collection<Film> findAll() {
        return jdbcTemplate.query(FilmSql.FIND_ALL_FILMS_SQL, filmsExtractor);
    }

    public Film persist(Film film) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(FilmSql.INSERT_FILM, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getRating().getId());
            return ps;
        }, keyHolder);

        film.setId(requireGeneratedId(keyHolder));

        return film;
    }

    public Optional<Film> update(Film film) {
        Long filmId = film.getId();

        int update = jdbcTemplate.update(FilmSql.UPDATE_FILM_SQL,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRating().getId(),
                filmId
        );

        if (update > 0) {
            return Optional.of(film);
        }
        return Optional.empty();
    }

    @Override
    public List<Film> findCommonFilms(Long firstUser, Long secondUser) {
        return jdbcTemplate.query(FilmSql.FIND_COMMON_FILMS, filmRowMapper, firstUser, secondUser);
    }

    public Collection<Film> getListDirectorFilms(long directorId, SortDirectorFilms sortDirectorFilms) {
        final String queryToSortByYear = FilmSql.BASE_FILM_SELECT + " " +
                """
                        WHERE fd.director_id = ?
                        ORDER BY EXTRACT(YEAR FROM  f.release_date) ASC
                        """;
        // Copy-paste запроса из класса FilmSql поле BASE_FILM_SELECT.
        // Как можно по другому вставить функцию COUNT(fl.user_id), чтобы это было безопасно?
        final String queryToSortByLikes =
                """
                          SELECT
                          f.film_id       AS film_id,
                          f.name          AS film_name,
                          f.description   AS film_description,
                          f.release_date  AS film_release_date,
                          f.duration      AS film_duration,
                          f.rating_id     AS rating_id,
                          r.name          AS rating_name,
                          g.genre_id      AS genre_id,
                          g.name          AS genre_name,
                          d.id            AS director_id,
                          d.name          AS director_name,
                          COUNT(fl.user_id) AS count
                        FROM films f
                        JOIN ratings r ON f.rating_id = r.rating_id
                        LEFT JOIN film_genres fg ON f.film_id = fg.film_id
                        LEFT JOIN genres g ON fg.genre_id = g.genre_id
                        LEFT JOIN films_directors AS fd ON fd.film_id=f.film_id
                        LEFT JOIN directors AS d ON fd.director_id=d.id
                        LEFT JOIN film_likes AS fl ON fl.film_id=f.film_id
                        WHERE fd.director_id = ?
                        GROUP BY f.film_id
                        ORDER BY count DESC
                        """;

        switch (sortDirectorFilms) {
            case YEAR -> {
                return jdbcTemplate.query(queryToSortByYear, filmsExtractor, directorId);
            }
            case LIKES -> {
                return jdbcTemplate.query(queryToSortByLikes, filmsExtractor, directorId);
            }
        }
        return List.of();
    }

    @Override
    public void remove(Long id) {
        jdbcTemplate.update(FilmSql.DELETE_FILM, id);
    }

    private Long requireGeneratedId(KeyHolder keyHolder) {
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated id");
        }
        return key.longValue();
    }
}
