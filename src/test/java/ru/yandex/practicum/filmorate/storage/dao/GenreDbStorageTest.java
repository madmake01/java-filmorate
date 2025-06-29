package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.config.TestJdbcConfig;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = TestJdbcConfig.class)
class GenreDbStorageTest {

    private final GenreDbStorage genreDbStorage;

    @Test
    void getById_shouldReturnGenreWhenExists() {
        Optional<Genre> genre = genreDbStorage.getById(1L);
        assertThat(genre).isPresent();
        assertThat(genre.get().getId()).isEqualTo(1L);
        assertThat(genre.get().getName()).isEqualTo("Комедия");
    }

    @Test
    void getById_shouldReturnEmptyWhenNotExists() {
        Optional<Genre> genre = genreDbStorage.getById(999L);
        assertThat(genre).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllGenres() {
        Collection<Genre> genres = genreDbStorage.findAll();
        assertThat(genres).hasSize(6);
        assertThat(genres).extracting(Genre::getName)
                .containsExactlyInAnyOrder("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик");
    }
}
