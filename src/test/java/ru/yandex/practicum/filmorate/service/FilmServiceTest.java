package ru.yandex.practicum.filmorate.service;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

class FilmServiceTest {

    @Mock
    private FilmStorage filmStorage;

    @InjectMocks
    private FilmService filmService;

    private Film film1;
    private Film film2;

/*    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        film1 = new Film();
        film1.setId(1L);
        film1.setName("Крадущийся тигр");
        film1.setLikesCount(5);

        film2 = new Film();
        film2.setId(2L);
        film2.setName("Крадущийся в ночи");
        film2.setLikesCount(2);
    }

    @Test
    void searchByTitleReturnsSortedByLikes() {
        when(filmStorage.findByTitleLike("%крад%"))
                .thenReturn(List.of(film2, film1));
        when(filmStorage.findByDirectorLike("%крад%"))
                .thenReturn(Collections.emptyList());

        List<Film> result = filmService.search("крад", Set.of("title"));

        assertEquals(2, result.size());
        assertEquals(film1, result.get(0));
        assertEquals(film2, result.get(1));
        verify(filmStorage).findByTitleLike("%крад%");
        verifyNoMoreInteractions(filmStorage);
    }

    @Test
    void searchByDirectorAndTitleRemovesDuplicates() {
        when(filmStorage.findByTitleLike("%крад%"))
                .thenReturn(List.of(film1));
        when(filmStorage.findByDirectorLike("%крад%"))
                .thenReturn(List.of(film1, film2));

        List<Film> result = filmService.search("крад", Set.of("title", "director"));

        assertEquals(2, result.size());
        assertEquals(film1, result.get(0));
        assertEquals(film2, result.get(1));
        verify(filmStorage).findByTitleLike("%крад%");
        verify(filmStorage).findByDirectorLike("%крад%");
    }*/
}
