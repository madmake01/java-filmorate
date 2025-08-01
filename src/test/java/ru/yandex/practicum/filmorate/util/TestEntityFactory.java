package ru.yandex.practicum.filmorate.util;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class TestEntityFactory {

    private static final LocalDate DEFAULT_BIRTHDAY = LocalDate.of(2000, 1, 1);

    private static final List<Rating> RATINGS = List.of(
            new Rating(1L, "G"),
            new Rating(2L, "PG"),
            new Rating(3L, "PG-13"),
            new Rating(4L, "R"),
            new Rating(5L, "NC-17")
    );

    private static final List<Genre> GENRES = List.of(
            new Genre(1L, "Комедия"),
            new Genre(2L, "Драма"),
            new Genre(3L, "Мультфильм"),
            new Genre(4L, "Триллер"),
            new Genre(5L, "Документальный"),
            new Genre(6L, "Боевик")
    );

    private static final Random RANDOM = ThreadLocalRandom.current();

    private TestEntityFactory() {
    }

    public static User createUser(String name) {
        User user = new User();
        user.setEmail("email");
        user.setLogin("login");
        user.setName(name);
        user.setBirthday(DEFAULT_BIRTHDAY);
        return user;
    }

    public static Rating getRandomRating() {
        return RATINGS.get(RANDOM.nextInt(RATINGS.size()));
    }

    public static List<Genre> getRandomGenres() {
        int count = RANDOM.nextInt(1, GENRES.size() + 1);
        List<Genre> shuffled = new ArrayList<>(GENRES);
        Collections.shuffle(shuffled, RANDOM);
        return shuffled.subList(0, count);
    }

    public static Film createFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        film.setRating(getRandomRating());
        film.setGenres(getRandomGenres());
        return film;
    }
}
