package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.recommendations.RecommendationsStorage;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class RecommendationsServiceTest {
    private RecommendationsStorage storage;
    private UserService userService;
    private FilmService filmService;
    private RecommendationsService recommendationsService;

    @BeforeEach
    void setUp() {
        storage = mock(RecommendationsStorage.class);
        userService = mock(UserService.class);
        filmService = mock(FilmService.class);
        recommendationsService = new RecommendationsService(filmService, userService, storage);
    }

    @Test
    void getRecommendedFilms_UserNotFound() {
        Long userId = 1L;

        when(userService.getUser(userId)).thenThrow(new EntityNotFoundException("User not found"));

        Set<Film> result = recommendationsService.getRecommendedFilms(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userService).getUser(userId);
        verifyNoMoreInteractions(storage, filmService);
    }

    @Test
    void getRecommendedFilms_UserHasNoLikes() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userService.getUser(userId)).thenReturn(user);
        when(storage.getUserFilms(userId)).thenReturn(Collections.emptyList());

        Set<Film> result = recommendationsService.getRecommendedFilms(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userService).getUser(userId);
        verify(storage).getUserFilms(userId);
        verifyNoMoreInteractions(userService, storage, filmService);
    }

    @Test
    void getRecommendedFilms_UsersWithLikes() {
        Long userId = 1L;
        Long similarUserId = 2L;

        User user1 = new User();
        user1.setId(userId);
        User user2 = new User();
        user2.setId(similarUserId);

        when(userService.getUser(userId)).thenReturn(user1);
        when(userService.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<Long> user1Films = Arrays.asList(10L, 20L, 30L);
        List<Long> user2Films = Arrays.asList(20L, 30L, 40L, 50L);

        when(storage.getUserFilms(userId)).thenReturn(user1Films);
        when(storage.getUsersFilms(Collections.singletonList(similarUserId)))
                .thenReturn(Map.of(
                        similarUserId, user2Films
                ));

        Film film40 = new Film();
        film40.setId(40L);
        Film film50 = new Film();
        film50.setId(50L);

        when(filmService.getFilm(40L)).thenReturn(film40);
        when(filmService.getFilm(50L)).thenReturn(film50);

        Set<Film> result = recommendationsService.getRecommendedFilms(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(film40));
        assertTrue(result.contains(film50));

        verify(userService).getUser(userId);
        verify(userService).findAll();
        verify(storage).getUserFilms(userId);
        verify(storage).getUsersFilms(Collections.singletonList(similarUserId));
        verify(filmService).getFilm(40L);
        verify(filmService).getFilm(50L);
    }

    @Test
    void getRecommendedFilms_NoSimilarUsers() {
        Long userId = 1L;
        Long similarUserId = 2L;

        User user1 = new User();
        user1.setId(userId);
        User user2 = new User();
        user2.setId(similarUserId);

        when(userService.getUser(userId)).thenReturn(user1);
        when(userService.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<Long> user1Films = Arrays.asList(10L, 20L);
        List<Long> user2Films = Arrays.asList(30L, 40L);

        when(storage.getUserFilms(userId)).thenReturn(user1Films);
        when(storage.getUsersFilms(Collections.singletonList(similarUserId)))
                .thenReturn(Map.of(
                        similarUserId, user2Films
                ));

        Set<Film> result = recommendationsService.getRecommendedFilms(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userService).getUser(userId);
        verify(userService).findAll();
        verify(storage).getUserFilms(userId);
        verify(storage).getUsersFilms(Collections.singletonList(similarUserId));
        verifyNoMoreInteractions(filmService);
    }
}
