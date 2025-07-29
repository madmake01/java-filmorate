package ru.yandex.practicum.filmorate.model;

import java.util.List;

/**
 * Обёртка для ошибок API.
 */
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
