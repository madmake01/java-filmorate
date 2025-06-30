package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.config.TestJdbcConfig;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = TestJdbcConfig.class)
class RatingDbStorageTest {

    private final RatingDbStorage ratingDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @Test
    void getById_shouldReturnRatingWhenExists() {
        Long id = jdbcTemplate.queryForObject("SELECT rating_id FROM ratings WHERE name = 'PG'", Long.class);
        Optional<Rating> rating = ratingDbStorage.getById(id);

        assertThat(rating).isPresent();
        assertThat(rating.get().getId()).isEqualTo(id);
        assertThat(rating.get().getName()).isEqualTo("PG");
    }

    @Test
    void getById_shouldReturnEmptyWhenNotExists() {
        Optional<Rating> rating = ratingDbStorage.getById(-999L);
        assertThat(rating).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllPredefinedRatings() {
        Collection<Rating> ratings = ratingDbStorage.findAll();

        assertThat(ratings).hasSize(5);
        assertThat(ratings).extracting(Rating::getName)
                .containsExactlyInAnyOrder("G", "PG", "PG-13", "R", "NC-17");
    }
}
