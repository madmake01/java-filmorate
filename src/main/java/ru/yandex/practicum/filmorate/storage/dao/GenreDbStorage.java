package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.sql.GenreSql;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Genre> genreMapper;

    public Optional<Genre> getById(Long id) {
        List<Genre> genres = jdbcTemplate.query(GenreSql.FIND_BY_ID, genreMapper, id);
        return genres.isEmpty() ? Optional.empty() : Optional.of(genres.getFirst());
    }

    public Collection<Genre> findAll() {
        return jdbcTemplate.query(GenreSql.FIND_ALL, genreMapper);
    }
}
