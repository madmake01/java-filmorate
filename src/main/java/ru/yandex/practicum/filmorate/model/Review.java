package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;

/**
 * Модель отзыва к фильму.
 */
public class Review {
    // Уникальный идентификатор отзыва
    private long reviewId;
    // Текст отзыва (обязателен, не пустой, не более 500 символов)
    @NotBlank(message = "content: must not be blank")
    @Size(max = 500, message = "content: size must be between 1 and 500")
    private String content;
    // Тип отзыва: true — положительный, false — негативный (обязательно)
    @NotNull(message = "isPositive: must not be null")
    @JsonProperty("isPositive")
    private Boolean isPositive;
    // Идентификатор пользователя, добавившего отзыв
    private long userId;
    // Идентификатор фильма, к которому привязан отзыв
    private long filmId;
    // Рейтинг полезности (лайки — дизлайки)
    private int useful;

    public Review() {
        // Конструктор по умолчанию
    }

    public Review(long reviewId, String content, Boolean isPositive, long userId, long filmId, int useful) {
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

    /**
     * Признак отзыва: true — положительный, false — негативный.
     */
    @JsonProperty("isPositive")
    public Boolean isPositive() {
        return isPositive;
    }

    @JsonProperty("isPositive")
    public void setPositive(Boolean positive) {
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
                Objects.equals(isPositive, review.isPositive) &&
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
