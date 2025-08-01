package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Модель, представляющая связь между пользователем и фильмом.
 * <p>
 * Используется для обозначения того, что пользователь лайкнул
 * или взаимодействовал с фильмом.
 * </p>
 */
@Getter
@AllArgsConstructor
public class UserFilm {

    /**
     * Идентификатор пользователя.
     */
    private final Long userId;

    /**
     * Идентификатор фильма.
     */
    private final Long filmId;
}
