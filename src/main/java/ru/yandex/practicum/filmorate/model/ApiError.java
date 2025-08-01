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
    private String message;
    private List<String> errors;

    public ApiError(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ApiError(int status, String message, List<String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

}
