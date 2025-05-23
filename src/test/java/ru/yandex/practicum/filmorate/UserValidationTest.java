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
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserValidationTest {

    private static Validator validator;
    private User user;


    @BeforeAll
    void setupValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @BeforeEach
    void setupValidUser() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setLogin("validLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.now());
    }

    private Set<ConstraintViolation<User>> validate(User user) {
        return validator.validate(user);
    }

    @Test
    void shouldPassValidation() {
        Set<ConstraintViolation<User>> violations = validate(user);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("validEmailCases")
    void testEmailValidation(String email, boolean shouldBeValid) {
        user.setEmail(email);

        boolean hasViolation = validate(user).stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));

        assertThat(hasViolation).isEqualTo(!shouldBeValid);
    }

    private Stream<Arguments> validEmailCases() {
        return Stream.of(
                Arguments.of("test@example.com", true),
                Arguments.of("   ", false),
                Arguments.of("invalid-email", false),
                Arguments.of(null, false)
        );
    }

    @ParameterizedTest
    @MethodSource("loginValidationCases")
    void testLoginValidation(String login, boolean shouldBeValid) {
        user.setLogin(login);

        boolean hasViolation = validate(user).stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("login"));

        assertThat(hasViolation).isEqualTo(!shouldBeValid);
    }

    private Stream<Arguments> loginValidationCases() {
        return Stream.of(
                Arguments.of("validLogin", true),
                Arguments.of(" ", false),
                Arguments.of("", false),
                Arguments.of(null, false)
        );
    }

    @ParameterizedTest
    @MethodSource("birthdayValidationCases")
    void testBirthdayValidation(LocalDate birthday, boolean shouldBeValid) {
        user.setBirthday(birthday);

        boolean hasViolation = validate(user).stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("birthday"));

        assertThat(hasViolation).isEqualTo(!shouldBeValid);
    }

    private Stream<Arguments> birthdayValidationCases() {
        return Stream.of(
                Arguments.of(LocalDate.now(), true),
                Arguments.of(LocalDate.now().minusYears(30), true),
                Arguments.of(LocalDate.now().plusDays(1), false)
        );
    }
}
