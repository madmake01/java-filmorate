package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Модель отзыва к фильму.
 */
public class Review {
    // Уникальный идентификатор отзыва
    private long reviewId;

    // Текст отзыва
    @NotBlank(message = "content: must not be blank")
    private String content;

    // Тип отзыва: true - положительный, false - негативный
    @NotNull(message = "isPositive: must not be null")
    private Boolean isPositive;

    // Идентификатор пользователя, добавившего отзыв
    @NotNull(message = "userId: must not be null")
    private Long userId;

    // Идентификатор фильма, к которому привязан отзыв
    @NotNull(message = "filmId: must not be null")
    private Long filmId;

    // Рейтинг полезности (лайки - дизлайки)
    private int useful;

    public Review() {
        // Конструктор по умолчанию
    }

    public Review(long reviewId,
                  String content,
                  Boolean isPositive,
                  Long userId,
                  Long filmId,
                  int useful) {
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

    /**
     * Сеттер для Jackson, чтобы обязательно требовать поле isPositive в JSON
     */
    @JsonProperty("isPositive")
    public void setPositive(Boolean positive) {
        this.isPositive = positive;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFilmId() {
        return filmId;
    }

    public void setFilmId(Long filmId) {
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
                userId.equals(review.userId) &&
                filmId.equals(review.filmId) &&
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
