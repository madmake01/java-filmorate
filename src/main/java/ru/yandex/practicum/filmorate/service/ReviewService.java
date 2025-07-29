package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

/**
 * Сервис для работы с отзывами о фильмах.
 */
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserService    userService;
    private final FilmService    filmService;

    public Review addReview(Review review) {
        userService.getUser(review.getUserId());
        filmService.getFilm(review.getFilmId());
        review.setUseful(0);
        return reviewStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        reviewStorage.getReviewById(review.getReviewId())
                .orElseThrow(() -> new EntityNotFoundException("Отзыв не найден: " + review.getReviewId()));
        userService.getUser(review.getUserId());
        filmService.getFilm(review.getFilmId());
        return reviewStorage.updateReview(review);
    }

    public void deleteReview(Long reviewId) {
        reviewStorage.getReviewById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Отзыв не найден: " + reviewId));
        reviewStorage.deleteReview(reviewId);
    }

    public Review getReview(Long reviewId) {
        return reviewStorage.getReviewById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Отзыв не найден: " + reviewId));
    }

    public List<Review> getReviews(Long filmId, int count) {
        if (filmId != null && filmId > 0) {
            filmService.getFilm(filmId);
            return reviewStorage.getReviewsByFilmId(filmId, count);
        }
        return reviewStorage.getAllReviews(count);
    }

    public Review addLike(Long reviewId, Long userId) {
        getReview(reviewId);
        userService.getUser(userId);
        reviewStorage.addLike(reviewId, userId);
        return getReview(reviewId);
    }

    public Review removeLike(Long reviewId, Long userId) {
        getReview(reviewId);
        userService.getUser(userId);
        reviewStorage.removeLike(reviewId, userId);
        return getReview(reviewId);
    }

    public Review addDislike(Long reviewId, Long userId) {
        getReview(reviewId);
        userService.getUser(userId);
        reviewStorage.addDislike(reviewId, userId);
        return getReview(reviewId);
    }

    public Review removeDislike(Long reviewId, Long userId) {
        getReview(reviewId);
        userService.getUser(userId);
        reviewStorage.removeDislike(reviewId, userId);
        return getReview(reviewId);
    }
}
