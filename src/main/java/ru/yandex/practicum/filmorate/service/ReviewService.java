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

    /**
     * Добавляет новый отзыв. Проверяет, что пользователь и фильм существуют.
     */
    public Review addReview(Review review) {
        userService.getUser(review.getUserId());
        filmService.getFilm(review.getFilmId());
        review.setUseful(0);
        return reviewStorage.addReview(review);
    }

    /**
     * Обновляет существующий отзыв. Проверяет, что отзыв, пользователь и фильм существуют.
     */
    public Review updateReview(Review review) {
        reviewStorage.getReviewById(review.getReviewId())
                .orElseThrow(() -> new EntityNotFoundException("Отзыв не найден: " + review.getReviewId()));
        userService.getUser(review.getUserId());
        filmService.getFilm(review.getFilmId());
        return reviewStorage.updateReview(review);
    }

    /**
     * Удаляет отзыв по идентификатору.
     */
    public void deleteReview(Long reviewId) {
        reviewStorage.getReviewById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Отзыв не найден: " + reviewId));
        reviewStorage.deleteReview(reviewId);
    }

    /**
     * Возвращает отзыв по идентификатору.
     */
    public Review getReview(Long reviewId) {
        return reviewStorage.getReviewById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Отзыв не найден: " + reviewId));
    }

    /**
     * Возвращает список отзывов. Если передан filmId > 0, возвращает отзывы по фильму, иначе все.
     */
    public List<Review> getReviews(Long filmId, int count) {
        if (filmId != null && filmId > 0) {
            filmService.getFilm(filmId);
            return reviewStorage.getReviewsByFilmId(filmId, count);
        }
        return reviewStorage.getAllReviews(count);
    }

    /**
     * Добавляет лайк отзыву.
     */
    public void addLike(Long reviewId, Long userId) {
        getReview(reviewId);
        userService.getUser(userId);
        reviewStorage.addLike(reviewId, userId);
    }

    /**
     * Убирает лайк от отзыва.
     */
    public void removeLike(Long reviewId, Long userId) {
        getReview(reviewId);
        userService.getUser(userId);
        reviewStorage.removeLike(reviewId, userId);
    }

    /**
     * Добавляет дизлайк отзыву.
     */
    public void addDislike(Long reviewId, Long userId) {
        getReview(reviewId);
        userService.getUser(userId);
        reviewStorage.addDislike(reviewId, userId);
    }

    /**
     * Убирает дизлайк от отзыва.
     */
    public void removeDislike(Long reviewId, Long userId) {
        getReview(reviewId);
        userService.getUser(userId);
        reviewStorage.removeDislike(reviewId, userId);
    }
}