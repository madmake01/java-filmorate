package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.sql.FilmSql;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final ResultSetExtractor<Film> filmExtractor;
    private final ResultSetExtractor<List<Film>> filmsExtractor;

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

    private Long requireGeneratedId(KeyHolder keyHolder) {
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated id");
        }
        return key.longValue();
    }
}
