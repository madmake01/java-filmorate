package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.config.TestJdbcConfig;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = TestJdbcConfig.class)
class FilmDbStorageTest {

    private static final Rating DEFAULT_RATING = createRating();
    private final FilmDbStorage filmDbStorage;

    private static Rating createRating() {
        Rating rating = new Rating();
        rating.setId(1L);
        rating.setName("G");
        return rating;
    }

    private Film createFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        film.setRating(DEFAULT_RATING);
        film.setGenres(List.of());
        return film;
    }

    @Test
    void persist_shouldInsertFilmAndSetId() {
        Film film = createFilm("Film 1");

        Film saved = filmDbStorage.persist(film);

        assertThat(saved.getId()).isNotNull();
        Optional<Film> loaded = filmDbStorage.find(saved.getId());
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getName()).isEqualTo("Film 1");
    }

    @Test
    void find_shouldReturnEmptyIfNotExists() {
        Optional<Film> result = filmDbStorage.find(999L);
        assertThat(result).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllFilms() {
        filmDbStorage.persist(createFilm("A"));
        filmDbStorage.persist(createFilm("B"));

        Collection<Film> films = filmDbStorage.findAll();

        assertThat(films).hasSize(2);
    }

    @Test
    void update_shouldModifyFilm() {
        Film film = filmDbStorage.persist(createFilm("Old Name"));
        film.setName("Updated Name");

        Optional<Film> updated = filmDbStorage.update(film);

        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Updated Name");

        Optional<Film> reloaded = filmDbStorage.find(film.getId());
        assertThat(reloaded).isPresent();
        assertThat(reloaded.get().getName()).isEqualTo("Updated Name");
    }

    @Test
    void update_shouldReturnEmptyIfNotFound() {
        Film film = createFilm("Ghost");
        film.setId(999L);

        Optional<Film> result = filmDbStorage.update(film);

        assertThat(result).isEmpty();
    }
}
