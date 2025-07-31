package ru.yandex.practicum.filmorate.model;

import java.util.List;

/**
 * Обёртка для ошибок API.
 */
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
    }
  
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
