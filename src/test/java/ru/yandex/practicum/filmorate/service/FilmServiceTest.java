package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    private FilmStorage filmStorage;

    @InjectMocks
    private FilmService filmService;

    private Film film1;
    private Film film2;

    @BeforeEach
    void setUp() {
        film1 = new Film();
        film1.setId(1L);
        film1.setName("Крадущийся тигр");

        film2 = new Film();
        film2.setId(2L);
        film2.setName("Крадущийся в ночи");
    }

    @Test
    void searchByTitleReturnsFilmsFromStorageInOriginalOrder() {
        when(filmStorage.findByTitleLike("%крад%"))
                .thenReturn(List.of(film2, film1));

        List<Film> result = filmService.search("крад", Set.of("title"));

        assertEquals(2, result.size());
        assertSame(film2, result.get(0));
        assertSame(film1, result.get(1));

        verify(filmStorage).findByTitleLike("%крад%");
        verifyNoMoreInteractions(filmStorage);
    }

    @Test
    void searchByDirectorReturnsFilmsFromStorageInOriginalOrder() {
        when(filmStorage.findByDirectorLike("%крад%"))
                .thenReturn(List.of(film1, film2));

        List<Film> result = filmService.search("крад", Set.of("director"));

        assertEquals(2, result.size());
        assertSame(film1, result.get(0));
        assertSame(film2, result.get(1));

        verify(filmStorage).findByDirectorLike("%крад%");
        verifyNoMoreInteractions(filmStorage);
    }

    @Test
    void searchByTitleAndDirectorUsesFindByBoth() {
        when(filmStorage.findByDirectorAndTitle("%крад%"))
                .thenReturn(List.of(film2, film1));

        List<Film> result = filmService.search("крад", Set.of("title", "director"));

        assertEquals(2, result.size());
        assertSame(film2, result.get(0));
        assertSame(film1, result.get(1));

        verify(filmStorage).findByDirectorAndTitle("%крад%");
        verifyNoMoreInteractions(filmStorage);
    }
}
