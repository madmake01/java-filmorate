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
        // Очистка связанных таблиц в порядке зависимостей
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

        // Подготовка тестовых записей
        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login, name, birthday) VALUES (1, 'user1@example.com', 'user1', 'User One', '1990-01-01')"
        );
        jdbcTemplate.update(
                "INSERT INTO ratings (rating_id, name) VALUES (1, 'G')"
        );
        jdbcTemplate.update(
                "INSERT INTO films (film_id, name, description, release_date, duration, rating_id) VALUES " +
                        "(1, 'Крадущийся тигр', 'desc', '2000-01-01', 120, 1), " +
                        "(2, 'Крадущийся в ночи', 'desc', '2001-01-01', 100, 1), " +
                        "(3, 'Ничего общего', 'desc', '2002-02-02', 90, 1)"
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
        assertNotNull(films);
        assertTrue(films.stream()
                .allMatch(f -> f.getName().toLowerCase().contains("крад")));
        for (int i = 1; i < films.size(); i++) {
            assertTrue(films.get(i - 1).getLikesCount() >= films.get(i).getLikesCount());
        }
    }


    @Test
    void findByDirectorLikeIntegration() {
        List<Film> films = filmStorage.findByDirectorLike("%аниг%");
        assertNotNull(films);
        assertFalse(films.isEmpty(), "Ожидаем хотя бы один фильм при поиске по режиссёру");
        assertTrue(films.stream()
                .flatMap(f -> f.getDirectors().stream())
                .anyMatch(d -> d.getName().toLowerCase().contains("аниг")));
    }
}
