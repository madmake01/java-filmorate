package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

/**
 * REST-контроллер для работы с отзывами на фильмы.
 */
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Добавить новый отзыв.
     * @param review JSON-объект отзыва
     * @return созданный отзыв с id и рейтингом
     */
    @PostMapping
    public Review addReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }

    /**
     * Обновить существующий отзыв.
     * @param review JSON-объект отзыва с существующим id
     * @return обновлённый отзыв
     */
    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    /**
     * Удалить отзыв по id.
     * @param reviewId идентификатор отзыва
     */
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable("id") long reviewId) {
        reviewService.deleteReview(reviewId);
    }

    /**
     * Получить отзыв по id.
     * @param reviewId идентификатор отзыва
     * @return найденный отзыв
     */
    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable("id") long reviewId) {
        return reviewService.getReviewById(reviewId);
    }

    /**
     * Получить список отзывов по фильму.
     * @param filmId (опционально) id фильма; если не указан, вернуть все
     * @param count максимальное число отзывов (по умолчанию 10)
     * @return список отзывов, отсортированных по рейтингу полезности
     */
    @GetMapping
    public List<Review> getReviews(
            @RequestParam(value = "filmId", required = false) Long filmId,
            @RequestParam(value = "count", defaultValue = "10") int count
    ) {
        long id = (filmId == null) ? 0 : filmId;
        return reviewService.getReviewsByFilmId(id, count);
    }

    /**
     * Поставить лайк отзыву.
     * @param reviewId идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable("id") long reviewId,
            @PathVariable long userId
    ) {
        reviewService.addLike(reviewId, userId);
    }

    /**
     * Убрать лайк (полезность) у отзыва.
     * @param reviewId идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @PathVariable("id") long reviewId,
            @PathVariable long userId
    ) {
        reviewService.removeLike(reviewId, userId);
    }

    /**
     * Поставить дизлайк отзыву.
     * @param reviewId идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(
            @PathVariable("id") long reviewId,
            @PathVariable long userId
    ) {
        reviewService.addDislike(reviewId, userId);
    }

    /**
     * Убрать дизлайк у отзыва.
     * @param reviewId идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(
            @PathVariable("id") long reviewId,
            @PathVariable long userId
    ) {
        reviewService.removeDislike(reviewId, userId);
    }
}
