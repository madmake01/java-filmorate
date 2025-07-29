package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с отзывами.
 */
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage,
                         UserStorage userStorage,
                         FilmStorage filmStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    /**
     * Добавить новый отзыв.
     */
    public Review addReview(Review review) {
        // Проверка существования пользователя
        if (userStorage.find(review.getUserId()).isEmpty()) {
            throw new IllegalArgumentException("Пользователь не найден: " + review.getUserId());
        }
        // Проверка существования фильма
        if (filmStorage.find(review.getFilmId()).isEmpty()) {
            throw new IllegalArgumentException("Фильм не найден: " + review.getFilmId());
        }
        // Проверка текста отзыва
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new IllegalArgumentException("Текст отзыва не может быть пустым");
        }
        return reviewStorage.addReview(review);
    }

    /**
     * Обновить существующий отзыв.
     */
    public Review updateReview(Review review) {
        // Проверка, что отзыв существует
        Optional<Review> existing = reviewStorage.getReviewById(review.getReviewId());
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Отзыв не найден: " + review.getReviewId());
        }
        // Проверка текста
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new IllegalArgumentException("Текст отзыва не может быть пустым");
        }
        return reviewStorage.updateReview(review);
    }

    /**
     * Удалить отзыв.
     */
    public void deleteReview(long reviewId) {
        // Проверяем существование
        if (reviewStorage.getReviewById(reviewId).isEmpty()) {
            throw new IllegalArgumentException("Отзыв не найден: " + reviewId);
        }
        reviewStorage.deleteReview(reviewId);
    }

    /**
     * Получить отзыв по ID.
     */
    public Review getReviewById(long reviewId) {
        return reviewStorage.getReviewById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Отзыв не найден: " + reviewId));
    }

    /**
     * Получить отзывы по фильму.
     */
    public List<Review> getReviewsByFilmId(long filmId, int count) {
        // Если filmId = 0, возвращаем все
        if (filmId == 0) {
            return reviewStorage.getAllReviews(count);
        }
        // Проверка существования фильма
        if (filmStorage.find(filmId).isEmpty()) {
            throw new IllegalArgumentException("Фильм не найден: " + filmId);
        }
        return reviewStorage.getReviewsByFilmId(filmId, count);
    }

    /**
     * Добавить лайк отзыву.
     */
    public void addLike(long reviewId, long userId) {
        // Проверка существования отзыва
        if (reviewStorage.getReviewById(reviewId).isEmpty()) {
            throw new IllegalArgumentException("Отзыв не найден: " + reviewId);
        }
        // Проверка существования пользователя
        if (userStorage.find(userId).isEmpty()) {
            throw new IllegalArgumentException("Пользователь не найден: " + userId);
        }
        reviewStorage.addLike(reviewId, userId);
    }

    /**
     * Удалить лайк.
     */
    public void removeLike(long reviewId, long userId) {
        reviewStorage.removeLike(reviewId, userId);
    }

    /**
     * Добавить дизлайк отзыву.
     */
    public void addDislike(long reviewId, long userId) {
        // Проверка существования отзыва
        if (reviewStorage.getReviewById(reviewId).isEmpty()) {
            throw new IllegalArgumentException("Отзыв не найден: " + reviewId);
        }
        // Проверка существования пользователя
        if (userStorage.find(userId).isEmpty()) {
            throw new IllegalArgumentException("Пользователь не найден: " + userId);
        }
        reviewStorage.addDislike(reviewId, userId);
    }

    /**
     * Удалить дизлайк.
     */
    public void removeDislike(long reviewId, long userId) {
        reviewStorage.removeDislike(reviewId, userId);
    }
}