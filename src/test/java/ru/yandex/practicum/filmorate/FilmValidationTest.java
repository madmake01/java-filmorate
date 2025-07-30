package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FilmValidationTest {

    private static Validator validator;
    private Film film;

    @BeforeAll
    void setupValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @BeforeEach
    void setupValidFilm() {
        film = new Film();
        film.setId(1L);
        film.setName("Inception");
        film.setDescription("A dream within a dream");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setRating(new Rating(1L, "G"));
        film.setDuration(148);
    }

    private Set<ConstraintViolation<Film>> validate(Film film) {
        return validator.validate(film);
    }

    @Test
    void shouldPassValidation() {
        Set<ConstraintViolation<Film>> violations = validate(film);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailOnBlankName() {
        film.setName("  ");
        Set<ConstraintViolation<Film>> violations = validate(film);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @ParameterizedTest
    @MethodSource("descriptionLengthCases")
    void testDescriptionLengthValidation(int length, boolean shouldBeValid) {
        film.setDescription("A".repeat(length));

        boolean hasViolation = validate(film).stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description"));

        assertThat(hasViolation).isEqualTo(!shouldBeValid);
    }

    private Stream<Arguments> descriptionLengthCases() {
        return Stream.of(
                Arguments.of(200, true),
                Arguments.of(201, false)
        );
    }

    @ParameterizedTest
    @MethodSource("releaseDateBoundaryCases")
    void testReleaseDateValidation(LocalDate date, boolean shouldBeValid) {
        film.setReleaseDate(date);

        boolean hasViolation = validate(film).stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate"));

        assertThat(hasViolation).isEqualTo(!shouldBeValid);
    }

    private Stream<Arguments> releaseDateBoundaryCases() {
        return Stream.of(
                Arguments.of(LocalDate.of(1895, 12, 27), false),
                Arguments.of(LocalDate.of(1895, 12, 28), true),
                Arguments.of(LocalDate.of(1895, 12, 29), true)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidDurations")
    void shouldFailOnInvalidDuration(Integer duration) {
        film.setDuration(duration);

        boolean hasViolation = validate(film).stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("duration"));

        assertThat(hasViolation).isTrue();
    }

    private Stream<Integer> invalidDurations() {
        return Stream.of(0, -1, null);
    }
}
