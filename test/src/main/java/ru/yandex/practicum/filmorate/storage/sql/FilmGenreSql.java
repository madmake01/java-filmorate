package ru.yandex.practicum.filmorate.storage.sql;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmGenreSql {
    public static final String INSERT_FILM_GENRES = """
            INSERT INTO film_genres(film_id, genre_id)
            VALUES (?, ?)
            """;
    public static final String DELETE_FILM_GENRES_BY_FILM_ID = """
            DELETE FROM film_genres
            WHERE film_id = ?
            """;
}
