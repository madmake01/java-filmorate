package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Repository
public class FilmDirectorsDbStorage {
    private final JdbcTemplate jdbc;

    @Autowired
    public FilmDirectorsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    public void createConnectionFilmDirector(long filmId, List<Director> directors) {
        final String queryToCreateConnectionFilmDirector =
                "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)";
        List<Object[]> params = directors.stream()
                .map(Director::getId)
                .distinct()
                .map(g -> new Object[]{filmId, g})
                .toList();

        jdbc.batchUpdate(queryToCreateConnectionFilmDirector, params);
    }

    public void removeConnectionFilmDirector(long filmId) {
        final String queryToRemoveConnectionFilmDirector = "DELETE FROM films_directors WHERE film_id = ?";
        jdbc.update(queryToRemoveConnectionFilmDirector, filmId);
    }
}
