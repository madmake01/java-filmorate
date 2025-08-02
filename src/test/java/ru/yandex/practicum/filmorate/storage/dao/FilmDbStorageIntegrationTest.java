package ru.yandex.practicum.filmorate.storage.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class FilmDbStorageIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FilmStorage filmStorage;

    @BeforeEach
    void initData() {
        // Очистка всех зависимых таблиц
        jdbcTemplate.execute("DELETE FROM review_likes");
        jdbcTemplate.execute("DELETE FROM reviews");
        jdbcTemplate.execute("DELETE FROM film_likes");
        jdbcTemplate.execute("DELETE FROM films_directors");
        jdbcTemplate.execute("DELETE FROM film_genres");
        jdbcTemplate.execute("DELETE FROM friendships");
        jdbcTemplate.execute("DELETE FROM feed_events");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM directors");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM ratings");

        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login, name, birthday) " +
                        "VALUES (1, 'user1@example.com', 'user1', 'User One', '1990-01-01')"
        );
        jdbcTemplate.update(
                "INSERT INTO ratings (rating_id, name) VALUES (1, 'G')"
        );

        jdbcTemplate.update(
                "INSERT INTO films (film_id, name, description, release_date, duration, rating_id) VALUES " +
                        "(1, 'Крадущийся тигр', 'desc', '2000-01-01', 120, 1), " +
                        "(2, 'Крадущийся в ночи', 'desc', '2001-01-01', 100, 1), " +
                        "(3, 'Ничего общего',      'desc', '2002-02-02',  90, 1)"
        );

        jdbcTemplate.update(
                "INSERT INTO directors (id, name) VALUES (1, 'Аниг Режиссер')"
        );
        jdbcTemplate.update(
                "INSERT INTO films_directors (film_id, director_id) VALUES (3, 1)"
        );
    }

    @Test
    void findByTitleLikeIntegration() {
        List<Film> films = filmStorage.findByTitleLike("%крад%");
        assertNotNull(films, "Список не должен быть null");
        assertEquals(2, films.size(), "Ожидаем два фильма по части названия 'крад'");
        assertTrue(films.stream()
                        .allMatch(f -> f.getName().toLowerCase().contains("крад")),
                "Все фильмы в ответе должны содержать 'крад' в названии");
    }

    @Test
    void findByDirectorLikeIntegration() {
        List<Film> films = filmStorage.findByDirectorLike("%аниг%");
        assertNotNull(films, "Список не должен быть null");
        assertEquals(1, films.size(), "Ожидаем один фильм по части имени режиссёра 'аниг'");
        Film only = films.get(0);
        assertEquals(3L, only.getId(), "Должен быть фильм с id=3");
        assertTrue(only.getDirectors().stream()
                        .anyMatch(d -> d.getName().toLowerCase().contains("аниг")),
                "Режиссёр у фильма должен содержать 'аниг'");
    }

    @Test
    void findByBothIntegration() {
        List<Film> films = filmStorage.findByBoth("%ночи%");
        assertNotNull(films, "Список не должен быть null");
        assertFalse(films.isEmpty(), "Ожидаем хотя бы один фильм по combined-поиску");
        assertTrue(films.stream()
                        .anyMatch(f -> f.getId().equals(2L)),
                "Должен присутствовать фильм с id=2");
    }
}
