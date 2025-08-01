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

    public static final String FIND_COMMON_FILMS = """
             SELECT
                 f.film_id,
                 f.name AS film_name,
                 f.description AS film_description,
                 f.release_date AS film_release_date,
                 f.duration AS film_duration,
                 f.rating_id,
                 r.name AS rating_name,
                 '[' || GROUP_CONCAT(
                     '{ "id": ' || g.genre_id || ', "name": "' || g.name || '" }'
                     ORDER BY g.genre_id
                     SEPARATOR ', '
                 ) || ']' AS genres
             FROM film_likes fl1
             JOIN film_likes fl2 ON fl1.film_id = fl2.film_id
             JOIN films f ON fl1.film_id = f.film_id
             LEFT JOIN film_genres fg ON f.film_id = fg.film_id
             LEFT JOIN genres g ON fg.genre_id = g.genre_id
             JOIN ratings r ON f.rating_id = r.rating_id
             JOIN (
                 SELECT film_id, COUNT(user_id) AS like_count
                 FROM film_likes
                 GROUP BY film_id
             ) fl ON f.film_id = fl.film_id
             WHERE fl1.user_id = ?
               AND fl2.user_id = ?
             GROUP BY
                 f.film_id,
                 f.name,
                 f.description,
                 f.release_date,
                 f.duration,
                 f.rating_id,
                 r.name
             ORDER BY fl.like_count DESC
            """;

}
