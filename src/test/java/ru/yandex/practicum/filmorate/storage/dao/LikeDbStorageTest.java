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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = TestJdbcConfig.class)
class LikeDbStorageTest {

    private final LikeDbStorage likeDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {

        jdbcTemplate.update("INSERT INTO users (user_id, email, login, name, birthday) VALUES (1, 'u1@mail.com', 'u1', 'User 1', '1990-01-01')");
        jdbcTemplate.update("INSERT INTO users (user_id, email, login, name, birthday) VALUES (2, 'u2@mail.com', 'u2', 'User 2', '1991-01-01')");
        jdbcTemplate.update("INSERT INTO users (user_id, email, login, name, birthday) VALUES (3, 'u3@mail.com', 'u3', 'User 3', '1992-01-01')");

        jdbcTemplate.update("""
                    INSERT INTO films (film_id, name, description, release_date, duration, rating_id)
                    VALUES (10, 'Film A', 'Desc A', '2000-01-01', 100, 1)
                """);
        jdbcTemplate.update("""
                    INSERT INTO films (film_id, name, description, release_date, duration, rating_id)
                    VALUES (20, 'Film B', 'Desc B', '2001-01-01', 90, 1)
                """);
        jdbcTemplate.update("""
                    INSERT INTO films (film_id, name, description, release_date, duration, rating_id)
                    VALUES (30, 'Film C', 'Desc C', '1999-02-05', 80, 2)
                """);
        jdbcTemplate.update("""
                    INSERT INTO films (film_id, name, description, release_date, duration, rating_id)
                    VALUES (40, 'Film D', 'Desc D', '1999-09-12', 70, 2)
                """);
        jdbcTemplate.update("""
                    INSERT INTO films (film_id, name, description, release_date, duration, rating_id)
                    VALUES (50, 'Film F', 'Desc F', '1999-02-04', 100, 2)
                """);
        jdbcTemplate.update("""
                    INSERT INTO films (film_id, name, description, release_date, duration, rating_id)
                    VALUES (60, 'Film G', 'Desc G', '2001-02-11', 120, 4)
                """);
    }

    @Test
    void addLike_shouldInsertLike() {
        likeDbStorage.addLike(new Like(1L, 10L));

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_likes WHERE user_id = ? AND film_id = ?",
                Integer.class, 1L, 10L
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    void removeLike_shouldDeleteLike() {
        jdbcTemplate.update("INSERT INTO film_likes (user_id, film_id) VALUES (?, ?)", 1L, 10L);

        likeDbStorage.removeLike(new Like(1L, 10L));

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_likes WHERE user_id = ? AND film_id = ?",
                Integer.class, 1L, 10L
        );
        assertThat(count).isZero();
    }

    @Test
    void findTopFilmsByLikes_shouldReturnSortedList() {
        jdbcTemplate.update("INSERT INTO film_likes (user_id, film_id) VALUES (1, 10)");
        jdbcTemplate.update("INSERT INTO film_likes (user_id, film_id) VALUES (2, 10)");
        jdbcTemplate.update("INSERT INTO film_likes (user_id, film_id) VALUES (1, 20)");

        List<Film> result = likeDbStorage.findTopFilmsByLikes(10);

        assertThat(result).hasSize(6);
        assertThat(result.get(0).getId()).isEqualTo(10L); // 2 лайка
        assertThat(result.get(1).getId()).isEqualTo(20L); // 1 лайк
    }

    @Test
    void getPopularFilmsWithCountAndGenreId() {
        jdbcTemplate.update("INSERT INTO film_likes (user_id, film_id) VALUES (1, 10)");
        jdbcTemplate.update("INSERT INTO film_likes (user_id, film_id) VALUES (2, 10)");
        jdbcTemplate.update("INSERT INTO film_likes (user_id, film_id) VALUES (1, 30)");
        jdbcTemplate.update("INSERT INTO film_likes (user_id, film_id) VALUES (1, 40)");
        jdbcTemplate.update("INSERT INTO film_likes (user_id, film_id) VALUES (2, 40)");
        jdbcTemplate.update("INSERT INTO film_likes (user_id, film_id) VALUES (3, 40)");

        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (10, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (20, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (30, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (40, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (50, 2)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (60, 2)");

        List<Film> result = likeDbStorage.getPopularFilmsWithCountAndGenreId(10, 1);

        assertThat(result).hasSize(4);
        assertThat(result.get(0).getId()).isEqualTo(40L); // 3 лайка
        assertThat(result.get(1).getId()).isEqualTo(10L); // 2 лайк
        assertThat(result.get(2).getId()).isEqualTo(30L); // 1 лайк
    }

    @Test
    void getPopularFilmsWithCountAndYear() {
        jdbcTemplate.update("INSERT INTO film_likes (user_id, film_id) VALUES (1, 10)");
        jdbcTemplate.update("INSERT INTO film_likes (user_id, film_id) VALUES (2, 10)");
        jdbcTemplate.update("INSERT INTO film_likes (user_id, film_id) VALUES (1, 30)");
        jdbcTemplate.update("INSERT INTO film_likes (user_id, film_id) VALUES (1, 40)");
        jdbcTemplate.update("INSERT INTO film_likes (user_id, film_id) VALUES (2, 40)");
        jdbcTemplate.update("INSERT INTO film_likes (user_id, film_id) VALUES (3, 40)");

        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (10, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (20, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (30, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (40, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (50, 2)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (60, 2)");

        List<Film> result = likeDbStorage.getPopularFilmsWithCountAndYear(10, 1999);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getId()).isEqualTo(40L); // 3 лайка
        assertThat(result.get(1).getId()).isEqualTo(30L); // 1 лайк

        for (Film film : result) {
            assertThat(film.getReleaseDate().getYear()).isEqualTo(1999);
        }
    }

    @Test
    void getPopularFilmsWithGenreId() {
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (10, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (20, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (30, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (40, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (50, 2)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (60, 2)");

        List<Film> result = likeDbStorage.getPopularFilmsWithGenreId(1);
        System.out.println(result);

        assertThat(result).hasSize(4);
    }

    @Test
    void getPopularFilmsWithYear() {
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (10, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (20, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (30, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (40, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (50, 2)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (60, 2)");

        List<Film> result = likeDbStorage.getPopularFilmsWithYear(1999);

        assertThat(result).hasSize(3);
        for (Film film : result) {
            assertThat(film.getReleaseDate().getYear()).isEqualTo(1999);
        }
    }

    @Test
    void getPopularFilmsWithGenreIdAndYear() {
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (10, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (20, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (30, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (40, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (50, 2)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (60, 2)");

        List<Film> result = likeDbStorage.getPopularFilmsWithGenreIdAndYear(1, 1999);

        assertThat(result).hasSize(2);
        for (Film film : result) {
            assertThat(film.getReleaseDate().getYear()).isEqualTo(1999);
        }
    }
}
