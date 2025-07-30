package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.config.TestJdbcConfig;
import ru.yandex.practicum.filmorate.storage.recommendations.RecommendationsDbStorage;
import ru.yandex.practicum.filmorate.storage.sql.LikeSql;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = TestJdbcConfig.class)
public class RecommendationsDbStorageTest {

    public static final String INSERT_USER = """
            INSERT INTO users (user_id, email, login, name, birthday) VALUES (?, ?, ?, ?, ?)
            """;
    public static final String INSERT_FILM = """
            INSERT INTO films (film_id, name, description, release_date, duration)
            VALUES (?, ?, ?, ?, ?)
            """;

    private final RecommendationsDbStorage storage;
    private final JdbcTemplate jdbcTemplate;


    @BeforeEach
    void setup() {
        // Очистим таблицы перед тестом
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM films");

        // Вставляем пользователей
        jdbcTemplate.update(
                INSERT_USER,
                1L,
                "user1@example.com",
                "user1",
                "User1",
                java.sql.Date.valueOf("1993-07-12")
        );
        jdbcTemplate.update(
                INSERT_USER,
                2L,
                "user2@example.com",
                "user2",
                "User2",
                java.sql.Date.valueOf("1994-09-05")
        );
        jdbcTemplate.update(
                INSERT_USER,
                3L,
                "user3@example.com",
                "user3",
                "User3",
                java.sql.Date.valueOf("1995-12-10")
        );

        // Вставляем фильмы
        jdbcTemplate.update(INSERT_FILM, 10L, "Film10", "Desc10", java.sql.Date.valueOf("2005-03-08"), 120);
        jdbcTemplate.update(INSERT_FILM, 20L, "Film20", "Desc20", java.sql.Date.valueOf("2021-01-20"), 110);

        // Вставляем лайки
        jdbcTemplate.update(LikeSql.INSERT_LIKE, 1L, 10L);
        jdbcTemplate.update(LikeSql.INSERT_LIKE, 1L, 20L);
        jdbcTemplate.update(LikeSql.INSERT_LIKE, 2L, 10L);
    }

    @Test
    void getUsersFilms_returnsCorrectFilms() {
        Collection<Long> filmsUser1 = storage.getUsersFilms(1L);
        assertThat(filmsUser1).containsExactlyInAnyOrder(10L, 20L);

        Collection<Long> filmsUser2 = storage.getUsersFilms(2L);
        assertThat(filmsUser2).containsExactly(10L);

        Collection<Long> filmsUser3 = storage.getUsersFilms(3L);
        assertThat(filmsUser3).isEmpty();
    }
}
