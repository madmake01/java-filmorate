package ru.yandex.practicum.filmorate.storage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    private ReviewStorage reviewStorage;
    private UserStorage userStorage;
    private FilmStorage filmStorage;
    private ReviewService reviewService;
    private Review sample;

    @BeforeEach
    void setUp() {
        reviewStorage = mock(ReviewStorage.class);
        userStorage   = mock(UserStorage.class);
        filmStorage   = mock(FilmStorage.class);
        reviewService = new ReviewService(reviewStorage, userStorage, filmStorage);

        sample = new Review();
        sample.setContent("Текст");
        sample.setPositive(true);
        sample.setUserId(1L);
        sample.setFilmId(1L);
    }

    @Test
    void addReview_userNotFound() {
        when(userStorage.find(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.addReview(sample))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void addReview_filmNotFound() {
        when(userStorage.find(1L)).thenReturn(Optional.of(mock(User.class)));
        when(filmStorage.find(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.addReview(sample))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Фильм не найден");
    }

    @Test
    void addReview_emptyContent() {
        when(userStorage.find(1L)).thenReturn(Optional.of(mock(User.class)));
        when(filmStorage.find(1L)).thenReturn(Optional.of(mock(Film.class)));
        sample.setContent("   ");

        assertThatThrownBy(() -> reviewService.addReview(sample))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Текст отзыва не может быть пустым");
    }

    @Test
    void updateReview_notExists() {
        sample.setReviewId(100L);
        when(reviewStorage.getReviewById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.updateReview(sample))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Отзыв не найден");
    }

    @Test
    void deleteReview_notExists() {
        when(reviewStorage.getReviewById(50L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.deleteReview(50L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Отзыв не найден");
    }

    @Test
    void addLike_reviewNotFound() {
        when(reviewStorage.getReviewById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.addLike(5L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Отзыв не найден");
    }

    @Test
    void addLike_userNotFound() {
        when(reviewStorage.getReviewById(5L)).thenReturn(Optional.of(sample));
        when(userStorage.find(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.addLike(5L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void addDislike_reviewNotFound() {
        when(reviewStorage.getReviewById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.addDislike(10L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Отзыв не найден");
    }

    @Test
    void addDislike_userNotFound() {
        when(reviewStorage.getReviewById(10L)).thenReturn(Optional.of(sample));
        when(userStorage.find(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.addDislike(10L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пользователь не найден");
    }
}
