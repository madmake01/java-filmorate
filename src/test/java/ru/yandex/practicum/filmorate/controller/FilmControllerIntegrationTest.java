package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void initData() {
        // 1) Очистка таблиц
        jdbcTemplate.execute("DELETE FROM film_likes");
        jdbcTemplate.execute("DELETE FROM films_directors");
        jdbcTemplate.execute("DELETE FROM directors");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM ratings");
        jdbcTemplate.execute("DELETE FROM users");

        // 2) Вставка пользователя (необходим для FK в film_likes)
        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login, name, birthday) VALUES (?, ?, ?, ?, ?)",
                1L, "user1@example.com", "user1", "User One", LocalDate.of(1990, 1, 1)
        );

        // 3) Вставка рейтинга
        jdbcTemplate.update("INSERT INTO ratings (rating_id, name) VALUES (?, ?)", 1, "G");

        // 4) Вставка режиссёра
        jdbcTemplate.update("INSERT INTO directors (id, name) VALUES (?, ?)", 20, "Тарковский");

        // 5) Вставка первого фильма и связь с режиссёром
        jdbcTemplate.update(
                "INSERT INTO films (film_id, name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?, ?)",
                200, "Солярис", "описание", LocalDate.of(1972, 4, 5), 167, 1
        );
        jdbcTemplate.update(
                "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)",
                200, 20
        );

        // 6) Лайк первому фильму
        jdbcTemplate.update("INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)", 200, 1L);

        // 7) Вставка второго фильма того же режиссёра без лайков
        jdbcTemplate.update(
                "INSERT INTO films (film_id, name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?, ?)",
                201, "Зеркало", "описание", LocalDate.of(1975, 2, 22), 108, 1
        );
        jdbcTemplate.update(
                "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)",
                201, 20
        );
    }

    @Test
    void searchByDirectorReturnsSorted() throws Exception {
        mockMvc.perform(get("/films/search")
                        .param("query", "тарко")
                        .param("by", "director")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                // первый — Солярис (film_id=200), т.к. у него 1 лайк
                .andExpect(jsonPath("$[0].id", is(200)))
                .andExpect(jsonPath("$[1].id", is(201)));
    }

    @Test
    void searchByTitleReturnsSingle() throws Exception {
        mockMvc.perform(get("/films/search")
                        .param("query", "оля")
                        .param("by", "title")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                // Название Солярис содержит подстроку "оля" (case-insensitive)
                .andExpect(jsonPath("$[0].name", containsStringIgnoringCase("олярис")));
    }
}
