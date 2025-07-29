package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для хранения отзывов.
 */
public interface ReviewStorage {
    /**
     * Добавить новый отзыв.
     * @param review отзыв без заполненного reviewId и useful
     * @return отзыв с сгенерированным идентификатором и рейтингом useful=0
     */
    Review addReview(Review review);

    /**
     * Обновить существующий отзыв.
     * @param review отзыв с существующим reviewId
     * @return обновлённый отзыв
     */
    Review updateReview(Review review);

    /**
     * Удалить отзыв.
     * @param reviewId идентификатор отзыва
     */
    void deleteReview(long reviewId);

    /**
     * Получить отзыв по идентификатору.
     * @param reviewId идентификатор отзыва
     * @return Optional с отзывом, если найден
     */
    Optional<Review> getReviewById(long reviewId);

    /**
     * Получить список отзывов по фильму, отсортированных по рейтингу полезности.
     * @param filmId идентификатор фильма
     * @param count максимальное число отзывов
     * @return список отзывов (может быть пустым)
     */
    List<Review> getReviewsByFilmId(long filmId, int count);

    /**
     * Получить список всех отзывов, отсортированных по рейтингу полезности.
     * @param count максимальное число отзывов
     * @return список отзывов (может быть пустым)
     */
    List<Review> getAllReviews(int count);

    /**
     * Добавить лайк (полезно) к отзыву от пользователя.
     * @param reviewId идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    void addLike(long reviewId, long userId);

    /**
     * Удалить лайк (полезно) от пользователя.
     * @param reviewId идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    void removeLike(long reviewId, long userId);

    /**
     * Добавить дизлайк (бесполезно) к отзыву от пользователя.
     * @param reviewId идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    void addDislike(long reviewId, long userId);

    /**
     * Удалить дизлайк (бесполезно) от пользователя.
     * @param reviewId идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    void removeDislike(long reviewId, long userId);
}
