package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * Добавление нового отзыва.
     */
    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    /**
     * Обновление существующего отзыва.
     */
    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    /**
     * Удаление отзыва по ID.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }

    /**
     * Получение отзыва по ID.
     */
    @GetMapping("/{id}")
    public Review getById(@PathVariable Long id) {
        return reviewService.getReview(id);
    }

    /**
     * Список отзывов: по фильму или все.
     */
    @GetMapping
    public List<Review> listByFilm(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") int count
    ) {
        return reviewService.getReviews(filmId, count);
    }

    /**
     * Добавление лайка отзыву.
     * Возвращает обновлённый отзыв.
     */
    @PutMapping("/{id}/like/{userId}")
    public Review addLike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.addLike(id, userId);
    }

    /**
     * Удаление лайка отзыву.
     * Возвращает обновлённый отзыв.
     */
    @DeleteMapping("/{id}/like/{userId}")
    public Review removeLike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.removeLike(id, userId);
    }

    /**
     * Добавление дизлайка отзыву.
     * Возвращает обновлённый отзыв.
     */
    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.addDislike(id, userId);
    }

    /**
     * Удаление дизлайка отзыву.
     * Возвращает обновлённый отзыв.
     */
    @DeleteMapping("/{id}/dislike/{userId}")
    public Review removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.removeDislike(id, userId);
    }
}
