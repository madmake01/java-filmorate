package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Обёртка для ошибок API.
 */
@Setter
@Getter
public class ApiError {
    private int status;
    private String error;
    private List<String> errors;

    public ApiError(int status, String error) {
        this.status = status;
        this.error = error;
    }

    public ApiError(int status, String error, List<String> errors) {
        this.status = status;
        this.error = error;
        this.errors = errors;
    }
}
