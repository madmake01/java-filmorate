package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.sql.FilmGenreSql;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FilmGenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public void saveFilmGenres(Long filmId, List<Genre> genres) {
        List<Object[]> params = genres.stream()
                .map(Genre::getId)
                .distinct()
                .map(g -> new Object[]{filmId, g})
                .toList();

        jdbcTemplate.batchUpdate(FilmGenreSql.INSERT_FILM_GENRES, params);
    }

    public void deleteFilmGenresByFilmId(Long filmId) {
        jdbcTemplate.update(FilmGenreSql.DELETE_FILM_GENRES_BY_FILM_ID, filmId);
    }
}
