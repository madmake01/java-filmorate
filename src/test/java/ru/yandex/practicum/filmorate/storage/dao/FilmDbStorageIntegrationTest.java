package ru.yandex.practicum.filmorate.storage.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FilmDbStorageIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FilmStorage filmStorage;

    @Autowired
    private DirectorStorage directorStorage;

    private Film filmNight;
    private Film filmNoRelation;

    @BeforeEach
    void initData() {
        Rating ratingG = new Rating();
        ratingG.setId(1L);
        Director directorAnig = directorStorage.createDirector(new Director(null, "Аниг Режиссер"));

        filmStorage.persist(new Film(
                null,
                "Крадущийся тигр",
                "desc",
                LocalDate.of(2000, 1, 1),
                120,
                ratingG,
                Collections.emptyList(),
                Collections.emptyList()
        ));
        filmNight = filmStorage.persist(new Film(
                null,
                "Крадущийся в ночи",
                "desc",
                LocalDate.of(2001, 1, 1),
                100,
                ratingG,
                Collections.emptyList(),
                Collections.emptyList()
        ));
        filmNoRelation = filmStorage.persist(new Film(
                null,
                "Ничего общего",
                "desc",
                LocalDate.of(2002, 2, 2),
                90,
                ratingG,
                Collections.emptyList(),
                Collections.emptyList()
        ));

        jdbcTemplate.update(
                "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)",
                filmNoRelation.getId(),
                directorAnig.getId()
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
        Film only = films.getFirst();
        assertEquals(filmNoRelation.getId(), only.getId(),
                "Должен быть фильм с тем же id, что и filmNoRelation");
        assertTrue(only.getDirectors().stream()
                        .anyMatch(d -> d.getName().toLowerCase().contains("аниг")),
                "Режиссёр у фильма должен содержать 'аниг'");
    }

    @Test
    void findByDirectorAndTitleIntegration() {
        List<Film> films = filmStorage.findByDirectorAndTitle("%ночи%");
        assertNotNull(films, "Список не должен быть null");
        assertFalse(films.isEmpty(), "Ожидаем хотя бы один фильм по combined-поиску");
        assertTrue(films.stream()
                        .anyMatch(f -> f.getId().equals(filmNight.getId())),
                "Должен присутствовать фильм с тем же id, что и filmNight");
    }
}