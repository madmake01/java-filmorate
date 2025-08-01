package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель отзыва к фильму.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    /**
     * Идентификатор (может быть null до сохранения)
     */
    @PositiveOrZero(message = "reviewId: must be >= 0")
    private Long reviewId;

    /**
     * Текст отзыва, не пустой
     */
    @NotBlank(message = "content: must not be blank")
    private String content;

    /**
     * Положительный ли отзыв (true=положительный; false=негативный)
     */
    @JsonProperty("isPositive")
    @NotNull(message = "isPositive: must not be null")
    private Boolean positive;

    /**
     * Автор (юзер)
     */
    @NotNull(message = "userId: must not be null")
    private Long userId;

    /**
     * К какому фильму
     */
    @NotNull(message = "filmId: must not be null")
    private Long filmId;

    /**
     * Рейтинг “полезности” (по умолчанию = 0)
     */
    @PositiveOrZero(message = "useful: must be >= 0")
    private Integer useful = 0;
}
