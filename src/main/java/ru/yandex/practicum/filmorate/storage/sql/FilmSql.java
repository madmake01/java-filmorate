package ru.yandex.practicum.filmorate.storage.sql;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmSql {
    public static final String BASE_FILM_SELECT = """
                SELECT
                  f.film_id       AS film_id,
                  f.name          AS film_name,
                  f.description   AS film_description,
                  f.release_date  AS film_release_date,
                  f.duration      AS film_duration,
                  f.rating_id     AS rating_id,
                  r.name          AS rating_name,
                  g.genre_id      AS genre_id,
                  g.name          AS genre_name
                FROM films f
                JOIN ratings r ON f.rating_id = r.rating_id
                LEFT JOIN film_genres fg ON f.film_id = fg.film_id
                LEFT JOIN genres g ON fg.genre_id = g.genre_id
            """;

    public static final String FIND_FILM_SQL = BASE_FILM_SELECT + " WHERE f.film_id = ?;";
    public static final String FIND_ALL_FILMS_SQL = BASE_FILM_SELECT + " ORDER BY f.film_id;";
    public static final String UPDATE_FILM_SQL = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?
            WHERE film_id = ?;
            """;

    public static final String INSERT_FILM = """
            INSERT INTO films (name, description, release_date, duration, rating_id)
            VALUES (?, ?, ?, ?, ?)
            """;
}
