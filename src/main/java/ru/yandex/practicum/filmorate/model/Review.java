package ru.yandex.practicum.filmorate.model;

import java.util.Objects;

/**
 * Модель отзыва к фильму.
 */
public class Review {
    // Уникальный идентификатор отзыва
    private long reviewId;
    // Текст отзыва
    private String content;
    // Тип отзыва: true - положительный, false - негативный
    private boolean isPositive;
    // Идентификатор пользователя, добавившего отзыв
    private long userId;
    // Идентификатор фильма, к которому привязан отзыв
    private long filmId;
    // Рейтинг полезности (лайки - дизлайки)
    private int useful;

    public Review() {
        // Конструктор по умолчанию
    }

    public Review(long reviewId, String content, boolean isPositive, long userId, long filmId, int useful) {
        this.reviewId = reviewId;
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
        this.useful = useful;
    }

    public long getReviewId() {
        return reviewId;
    }

    public void setReviewId(long reviewId) {
        this.reviewId = reviewId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isPositive() {
        return isPositive;
    }

    public void setPositive(boolean positive) {
        isPositive = positive;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getFilmId() {
        return filmId;
    }

    public void setFilmId(long filmId) {
        this.filmId = filmId;
    }

    public int getUseful() {
        return useful;
    }

    public void setUseful(int useful) {
        this.useful = useful;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Review review = (Review) o;
        return reviewId == review.reviewId &&
                isPositive == review.isPositive &&
                userId == review.userId &&
                filmId == review.filmId &&
                useful == review.useful &&
                Objects.equals(content, review.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId, content, isPositive, userId, filmId, useful);
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewId=" + reviewId +
                ", content='" + content + '\'' +
                ", isPositive=" + isPositive +
                ", userId=" + userId +
                ", filmId=" + filmId +
                ", useful=" + useful +
                '}';
    }
}
