package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
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

    @Override
    public Optional<Film> find(Long id) {
        return Optional.ofNullable(
                jdbcTemplate.query(FilmSql.FIND_FILM_SQL, filmExtractor, id)
        );
    }

    @Override
    public Collection<Film> findAll() {
        return jdbcTemplate.query(FilmSql.FIND_ALL_FILMS_SQL, filmsExtractor);
    }

    @Override
    public Film persist(Film film) {
        var keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();

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

    @Override
    public Optional<Film> update(Film film) {
        Long filmId = film.getId();
        int updated = jdbcTemplate.update(FilmSql.UPDATE_FILM_SQL,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRating().getId(),
                filmId
        );
        return updated > 0 ? Optional.of(film) : Optional.empty();
    }

    @Override
    public List<Film> findCommonFilms(Long firstUser, Long secondUser) {
        return jdbcTemplate.query(FilmSql.FIND_COMMON_FILMS, filmRowMapper, firstUser, secondUser);
    }

    @Override
    public Collection<Film> getListDirectorFilms(long directorId, SortDirectorFilms sortDirectorFilms) {
        final String queryToSortByYear = FilmSql.BASE_FILM_SELECT + " " +
                """
                        WHERE fd.director_id = ?
                        ORDER BY EXTRACT(YEAR FROM  f.release_date) ASC
                        """;

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

        return switch (sortDirectorFilms) {
            case YEAR -> jdbcTemplate.query(queryToSortByYear, filmsExtractor, directorId);
            case LIKES -> jdbcTemplate.query(queryToSortByLikes, filmsExtractor, directorId);
        };
    }

    @Override
    public List<Film> search(String query, List<String> by) {
        boolean searchTitle    = by.contains("title");
        boolean searchDirector = by.contains("director");
        if (!searchTitle && !searchDirector) {
            return List.of();
        }
        String pattern = "%" + query.toLowerCase() + "%";
        String sql = """
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
              LEFT JOIN films_directors fd ON fd.film_id = f.film_id
              LEFT JOIN directors d ON fd.director_id = d.id
              LEFT JOIN film_likes fl ON fl.film_id = f.film_id
            WHERE
            """ +
                (searchTitle && searchDirector
                        ? "(LOWER(f.name) LIKE ? OR LOWER(d.name) LIKE ?)"
                        : searchTitle
                        ? "LOWER(f.name) LIKE ?"
                        : "LOWER(d.name) LIKE ?")
                + """
            GROUP BY f.film_id
            ORDER BY COUNT(fl.user_id) DESC
            """;
        if (searchTitle && searchDirector) {
            return jdbcTemplate.query(sql, filmsExtractor, pattern, pattern);
        } else {
            return jdbcTemplate.query(sql, filmsExtractor, pattern);
        }
    }

    private Long requireGeneratedId(org.springframework.jdbc.support.KeyHolder keyHolder) {
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated id");
        }
        return key.longValue();
    }
}