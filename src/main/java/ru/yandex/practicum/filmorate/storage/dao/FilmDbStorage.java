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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Primary
@Repository
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final ResultSetExtractor<Film> filmExtractor;
    private final ResultSetExtractor<List<Film>> filmsExtractor;
    private final RowMapper<Film> filmRowMapper;
    private final RowMapper<Film> filmRowMapperWithLikesCount;

    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         ResultSetExtractor<Film> filmExtractor,
                         ResultSetExtractor<List<Film>> filmsExtractor,
                         @Qualifier("filmRowMapperWithDetails") RowMapper<Film> filmRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmExtractor = filmExtractor;
        this.filmsExtractor = filmsExtractor;
        this.filmRowMapper = filmRowMapper;
        this.filmRowMapperWithLikesCount = (ResultSet rs, int rowNum) -> {
            Film film = this.filmRowMapper.mapRow(rs, rowNum);
            film.setLikesCount(rs.getInt("likes_count"));
            return film;
        };
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

    @Override
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

    @Override
    public Collection<Film> getListDirectorFilms(long directorId, SortDirectorFilms sortDirectorFilms) {
        final String queryToSortByYear = FilmSql.BASE_FILM_SELECT + " " +
                """
                WHERE fd.director_id = ?
                ORDER BY EXTRACT(YEAR FROM f.release_date) ASC
                """;
        final String queryToSortByLikes =
                """
                SELECT f.*, COUNT(fl.user_id) AS likes_count
                FROM films AS f
                     LEFT JOIN film_likes fl ON fl.film_id = f.film_id
                     JOIN films_directors fd ON f.film_id = fd.film_id
                WHERE fd.director_id = ?
                GROUP BY f.film_id
                ORDER BY likes_count DESC
                """;

        switch (sortDirectorFilms) {
            case YEAR -> {
                return jdbcTemplate.query(queryToSortByYear, filmsExtractor, directorId);
            }
            case LIKES -> {
                return jdbcTemplate.query(queryToSortByLikes, filmRowMapperWithLikesCount, directorId);
            }
        }
        return List.of();
    }

    @Override
    public List<Film> findByTitleLike(String pattern) {
        // 1) Сначала получаем список ID, отсортированный по количеству лайков
        String sqlIds = """
                SELECT f.film_id                         AS film_id,
                       COUNT(fl.user_id)               AS likes_count
                FROM films f
                     LEFT JOIN film_likes fl ON fl.film_id = f.film_id
                WHERE LOWER(f.name) LIKE ?
                GROUP BY f.film_id
                ORDER BY likes_count DESC
                """;
        List<Long> ids = jdbcTemplate.query(sqlIds,
                (rs, rn) -> rs.getLong("film_id"),
                pattern);

        // 2) Потом для каждого id вызываем уже готовый метод find(id),
        //    который поднимает полные данные с жанрами, режиссёрами и likesCount
        return ids.stream()
                .flatMap(id -> find(id).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> findByDirectorLike(String pattern) {
        String sqlIds = """
                SELECT f.film_id                         AS film_id,
                       COUNT(fl.user_id)               AS likes_count
                FROM films f
                     LEFT JOIN film_likes fl ON fl.film_id = f.film_id
                     JOIN films_directors fd ON f.film_id = fd.film_id
                     JOIN directors d ON fd.director_id = d.id
                WHERE LOWER(d.name) LIKE ?
                GROUP BY f.film_id
                ORDER BY likes_count DESC
                """;
        List<Long> ids = jdbcTemplate.query(sqlIds,
                (rs, rn) -> rs.getLong("film_id"),
                pattern);

        return ids.stream()
                .flatMap(id -> find(id).stream())
                .collect(Collectors.toList());
    }

    private Long requireGeneratedId(KeyHolder keyHolder) {
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated id");
        }
        return key.longValue();
    }
}
