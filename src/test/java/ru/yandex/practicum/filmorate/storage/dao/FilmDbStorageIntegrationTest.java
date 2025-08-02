package ru.yandex.practicum.filmorate.storage.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class FilmDbStorageIntegrationTest {

    @Autowired
    private FilmStorage filmStorage;

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
        if (!films.isEmpty()) {
            assertTrue(films.stream()
                    .flatMap(f -> f.getDirectors().stream())
                    .anyMatch(d -> d.getName().toLowerCase().contains("аниг")));
        }
    }
}
