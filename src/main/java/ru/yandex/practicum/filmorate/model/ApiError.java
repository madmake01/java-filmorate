package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO для возвращаемой клиенту информации об ошибке.
 */
@Getter
@AllArgsConstructor
public class ApiError {
    /**
     * HTTP‑статус (код ошибки).
     */
    private final int status;

    /**
     * Описание ошибки.
     */
    private final String message;
}
