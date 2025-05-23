package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;

public class PositiveDurationValidator implements
        ConstraintValidator<PositiveDuration, Duration> {


    @Override
    public boolean isValid(Duration value, ConstraintValidatorContext context) {
        return value == null || value.getSeconds() > 0;
    }
}