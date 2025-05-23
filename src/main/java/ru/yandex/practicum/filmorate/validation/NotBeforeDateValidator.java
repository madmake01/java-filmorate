package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class NotBeforeDateValidator implements ConstraintValidator<NotBeforeDate, LocalDate> {

    private LocalDate threshold;

    @Override
    public void initialize(NotBeforeDate constraintAnnotation) {
        this.threshold = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isBefore(threshold);
    }
}

