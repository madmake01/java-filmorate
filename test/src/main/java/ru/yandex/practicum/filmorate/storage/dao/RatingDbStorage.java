package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.sql.RatingSql;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RatingDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Rating> ratingMapper;

    public Optional<Rating> getById(Long id) {
        List<Rating> ratings = jdbcTemplate.query(RatingSql.FIND_BY_ID, ratingMapper, id);
        return ratings.isEmpty() ? Optional.empty() : Optional.of(ratings.getFirst());
    }

    public Collection<Rating> findAll() {
        return jdbcTemplate.query(RatingSql.FIND_ALL, ratingMapper);
    }
}
