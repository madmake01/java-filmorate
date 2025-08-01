package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit-тесты для {@link ReviewService}.
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @Mock
    private ReviewStorage reviewStorage;

    @Mock
    private UserService userService;

    @Mock
    private FilmService filmService;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private ReviewService reviewService;

    private Review sample;
    private User dummyUser;
    private Film dummyFilm;

    @BeforeEach
    void setUp() {
        sample = new Review();
        sample.setContent("Test content");
        sample.setPositive(true);
        sample.setUserId(1L);
        sample.setFilmId(1L);

        dummyUser = new User();
        dummyFilm = new Film();
    }

    @Test
    void addReview_successful() {
        when(userService.getUser(1L)).thenReturn(dummyUser);
        when(filmService.getFilm(1L)).thenReturn(dummyFilm);
        when(reviewStorage.addReview(sample)).thenReturn(sample);

        Review created = reviewService.addReview(sample);

        assertThat(created).isEqualTo(sample);
        verify(userService).getUser(1L);
        verify(filmService).getFilm(1L);
        verify(reviewStorage).addReview(sample);
    }

    @Test
    void addReview_userNotFound_throws() {
        doThrow(new EntityNotFoundException("Пользователь не найден"))
                .when(userService).getUser(1L);

        assertThatThrownBy(() -> reviewService.addReview(sample))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void addReview_filmNotFound_throws() {
        when(userService.getUser(1L)).thenReturn(dummyUser);
        doThrow(new EntityNotFoundException("Фильм не найден"))
                .when(filmService).getFilm(1L);

        assertThatThrownBy(() -> reviewService.addReview(sample))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Фильм не найден");
    }

    @Test
    void getReview_notFound_throws() {
        when(reviewStorage.getReviewById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.getReview(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Отзыв не найден");
    }

    @Test
    void getReviews_allWithoutFilm() {
        when(reviewStorage.getAllReviews(10)).thenReturn(Collections.emptyList());

        reviewService.getReviews(null, 10);

        verify(reviewStorage).getAllReviews(10);
    }

    @Test
    void getReviews_byFilm_successful() {
        when(filmService.getFilm(1L)).thenReturn(dummyFilm);
        when(reviewStorage.getReviewsByFilmId(1L, 5)).thenReturn(Collections.emptyList());

        reviewService.getReviews(1L, 5);

        verify(filmService).getFilm(1L);
        verify(reviewStorage).getReviewsByFilmId(1L, 5);
    }

    @Test
    void updateReview_successful() {
        sample.setReviewId(1L);
        when(reviewStorage.getReviewById(1L)).thenReturn(Optional.of(sample));
        when(userService.getUser(1L)).thenReturn(dummyUser);
        when(filmService.getFilm(1L)).thenReturn(dummyFilm);
        when(reviewStorage.updateReview(sample)).thenReturn(sample);

        Review updated = reviewService.updateReview(sample);

        assertThat(updated).isEqualTo(sample);
        verify(reviewStorage).updateReview(sample);
    }

    @Test
    void deleteReview_notFound_throws() {
        when(reviewStorage.getReviewById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.deleteReview(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Отзыв не найден");
    }

    @Test
    void likeFlow_successful() {
        sample.setReviewId(1L);
        when(reviewStorage.getReviewById(1L)).thenReturn(Optional.of(sample));
        when(userService.getUser(1L)).thenReturn(dummyUser);

        reviewService.addLike(1L, 1L);
        reviewService.removeLike(1L, 1L);

        verify(reviewStorage).addLike(1L, 1L);
        verify(reviewStorage).removeLike(1L, 1L);
    }

    @Test
    void dislikeFlow_successful() {
        sample.setReviewId(1L);
        when(reviewStorage.getReviewById(1L)).thenReturn(Optional.of(sample));
        when(userService.getUser(1L)).thenReturn(dummyUser);

        reviewService.addDislike(1L, 1L);
        reviewService.removeDislike(1L, 1L);

        verify(reviewStorage).addDislike(1L, 1L);
        verify(reviewStorage).removeDislike(1L, 1L);
    }
}
