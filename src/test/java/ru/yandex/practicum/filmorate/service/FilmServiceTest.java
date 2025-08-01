package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmDirectorsDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreDbStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FilmServiceTest {

    private FilmStorage filmStorage;
    private FilmGenreDbStorage filmGenreDbStorage;
    private GenreService genreService;
    private RatingService ratingService;
    private DirectorService directorService;
    private FilmDirectorsDbStorage filmDirectorsDbStorage;
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        filmStorage           = mock(FilmStorage.class);
        filmGenreDbStorage    = mock(FilmGenreDbStorage.class);
        genreService          = mock(GenreService.class);
        ratingService         = mock(RatingService.class);
        directorService       = mock(DirectorService.class);
        filmDirectorsDbStorage= mock(FilmDirectorsDbStorage.class);

        filmService = new FilmService(
                filmStorage,
                filmGenreDbStorage,
                genreService,
                ratingService,
                directorService,
                filmDirectorsDbStorage
        );
    }

    @Test
    void whenQueryBlank_thenValidationException() {
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> filmService.search("   ", List.of("title")))
                .withMessage("Параметр 'query' не должен быть пустым");
    }

    @Test
    void whenByEmpty_thenValidationException() {
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> filmService.search("test", List.of()))
                .withMessage("Параметр 'by' должен содержать хотя бы одно значение");
    }

    @Test
    void whenByContainsInvalid_thenValidationException() {
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> filmService.search("test", List.of("foo")))
                .withMessageStartingWith("Недопустимое значение в 'by'");
    }

    @Test
    void whenValid_thenDelegateToStorage() {
        List<Film> mockList = List.of(new Film());
        when(filmStorage.search("q", List.of("title"))).thenReturn(mockList);

        List<Film> result = filmService.search("q", List.of("title"));
        assertThat(result).isSameAs(mockList);
        verify(filmStorage).search("q", List.of("title"));
    }
}
