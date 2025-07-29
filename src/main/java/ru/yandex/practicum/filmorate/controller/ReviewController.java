package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
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
    public Review addReview(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    /**
     * Обновить существующий отзыв.
     * @param review JSON-объект отзыва с существующим id
     * @return обновлённый отзыв
     */
    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    /**
     * Удалить отзыв по id.
     * @param reviewId идентификатор отзыва
     */
    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable long reviewId) {
        reviewService.deleteReview(reviewId);
    }

    /**
     * Получить отзыв по id.
     * @param reviewId идентификатор отзыва
     * @return найденный отзыв
     */
    @GetMapping("/{reviewId}")
    public Review getReviewById(@PathVariable long reviewId) {
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
    @PutMapping("/{reviewId}/like/{userId}")
    public void addLike(@PathVariable long reviewId, @PathVariable long userId) {
        reviewService.addLike(reviewId, userId);
    }

    /**
     * Убрать лайк (полезность) у отзыва.
     * @param reviewId идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @DeleteMapping("/{reviewId}/like/{userId}")
    public void removeLike(@PathVariable long reviewId, @PathVariable long userId) {
        reviewService.removeLike(reviewId, userId);
    }

    /**
     * Поставить дизлайк отзыву.
     * @param reviewId идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @PutMapping("/{reviewId}/dislike/{userId}")
    public void addDislike(@PathVariable long reviewId, @PathVariable long userId) {
        reviewService.addDislike(reviewId, userId);
    }

    /**
     * Убрать дизлайк у отзыва.
     * @param reviewId идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public void removeDislike(@PathVariable long reviewId, @PathVariable long userId) {
        reviewService.removeDislike(reviewId, userId);
    }

    /**
     * Обработка ошибок валидации и некорректных аргументов.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleBadRequest(IllegalArgumentException e) {
        return e.getMessage();
    }

    /**
     * Обработка случаев, когда ресурс не найден.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException e) {
        return e.getMessage();
    }
}
